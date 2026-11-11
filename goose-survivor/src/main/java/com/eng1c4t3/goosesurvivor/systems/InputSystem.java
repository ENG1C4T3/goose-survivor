package com.eng1c4t3.goosesurvivor.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.eng1c4t3.goosesurvivor.components.InteractableComponent;
import com.eng1c4t3.goosesurvivor.components.InventoryComponent;
import com.eng1c4t3.goosesurvivor.components.PlayerComponent;
import com.eng1c4t3.goosesurvivor.components.TransformComponent;
import com.eng1c4t3.goosesurvivor.components.TriggerEventComponent;
import com.eng1c4t3.goosesurvivor.components.UseItemEventComponent;
import com.eng1c4t3.goosesurvivor.components.VelocityComponent;

/**
 * Handles player input: updates the player's velocity from the movement keys, and raises a
 * trigger event when the interact key is pressed within range of an interactable, and raises a
 * use item event when a hotbar key (1-9) is pressed.
 *
 * @see PlayerComponent
 * @see VelocityComponent
 * @see InteractableComponent
 * @see TriggerEventComponent
 * @see UseItemEventComponent
 */
public class InputSystem extends IteratingSystem {

  /** The key that interacts with the nearest interactable in range. */
  public static final int INTERACT_KEY = Input.Keys.E;

  /** Number of hotbar slots that have a key (1 to 9). */
  public static final int MAX_ITEM_KEYS = 9;

  private ImmutableArray<Entity> interactables;

  /**
   * Returns the key that uses the item in an inventory slot.
   *
   * @param slotIndex The slot index, from 0 to {@link #MAX_ITEM_KEYS} - 1.
   * @return The key code: 1 for slot 0, 2 for slot 1, and so on.
   * @throws IllegalArgumentException if the slot has no key.
   */
  public static int itemKey(int slotIndex) {
    if (slotIndex < 0 || slotIndex >= MAX_ITEM_KEYS) {
      throw new IllegalArgumentException("Slot " + slotIndex + " has no key");
    }
    return Input.Keys.NUM_1 + slotIndex;
  }

  /**
   * Constructs an InputSystem that processes entities with PlayerComponent and VelocityComponent.
   */
  public InputSystem() {
    super(Family.all(PlayerComponent.class, VelocityComponent.class).get());
  }

  /**
   * Caches the interactable entities as well as the player entities.
   *
   * @param engine The {@link Engine} this system was added to.
   */
  @Override
  public void addedToEngine(Engine engine) {
    super.addedToEngine(engine);
    interactables = engine.getEntitiesFor(
        Family.all(InteractableComponent.class, TransformComponent.class).get()
    );
  }

  /**
   * Handles player input for the entity.
   *
   * @param entity The entity to process.
   * @param deltaTime The time passed since the last frame.
   */
  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    handleMovement(entity);
    if (Gdx.input.isKeyJustPressed(INTERACT_KEY)) {
      handleInteract(entity);
    }
    handleItemKeys(entity);
  }

  /**
   * Creates a use item event for each hotbar key pressed this frame. Whether the item is
   * actually used (it might be empty or cooling down) is decided by {@link InventorySystem}.
   *
   * @param player The player entity.
   */
  private void handleItemKeys(Entity player) {
    InventoryComponent inventory = InventoryComponent.MAPPER.get(player);
    if (inventory == null) {
      return;
    }
    int keyed = Math.min(inventory.capacity(), MAX_ITEM_KEYS);
    for (int i = 0; i < keyed; ++i) {
      if (Gdx.input.isKeyJustPressed(itemKey(i))) {
        Entity event = new Entity();
        event.add(new UseItemEventComponent(player, i));
        getEngine().addEntity(event);
      }
    }
  }

  /**
   * Updates the velocity component of the player from the movement keys.
   *
   * @param player The player entity.
   */
  private void handleMovement(Entity player) {
    VelocityComponent vel = VelocityComponent.MAPPER.get(player);

    vel.velocity.setZero();
    if (Gdx.input.isKeyPressed(Input.Keys.W)
        || Gdx.input.isKeyPressed(Input.Keys.UP)) {
      vel.velocity.y += 1;
    }
    if (Gdx.input.isKeyPressed(Input.Keys.S)
        || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
      vel.velocity.y -= 1;
    }
    if (Gdx.input.isKeyPressed(Input.Keys.A)
        || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
      vel.velocity.x -= 1;
    }
    if (Gdx.input.isKeyPressed(Input.Keys.D)
        || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
      vel.velocity.x += 1;
    }

    // Normalise the vector so diagonal movement isn't faster, then scale to max speed
    if (!vel.velocity.isZero()) {
      vel.velocity.nor().scl(vel.maxSpeed);
    }
  }

  /**
   * Creates a trigger event entity for the nearest interactable within range of the player.
   * Does nothing if the player has no position or no interactable is in range.
   *
   * @param player The player entity.
   */
  private void handleInteract(Entity player) {
    Entity nearest = findNearestInteractable(player, interactables);
    if (nearest != null) {
      Entity event = new Entity();
      event.add(new TriggerEventComponent(
          player, nearest, TriggerEventComponent.ActionType.INTERACT));
      getEngine().addEntity(event);
    }
  }

  /**
   * Returns the interactable that pressing the interact key would target: the one nearest to
   * the player whose range the player is within.
   *
   * <p>Shared with the HUD so the on-screen prompt always matches what E will do. Candidates
   * without an {@link InteractableComponent} and {@link TransformComponent}, or that are
   * scheduled for removal, are skipped.
   *
   * @param player The player entity.
   * @param candidates The entities to search, e.g. every entity in the world.
   * @return The targeted interactable, or {@code null} if none is in range or the player has
   *         no position.
   */
  public static Entity findNearestInteractable(Entity player, Iterable<Entity> candidates) {
    TransformComponent playerTransform = TransformComponent.MAPPER.get(player);
    if (playerTransform == null) {
      return null;
    }

    Entity nearest = null;
    float nearestDist2 = Float.MAX_VALUE;
    for (Entity candidate : candidates) {
      InteractableComponent interactable = InteractableComponent.MAPPER.get(candidate);
      TransformComponent transform = TransformComponent.MAPPER.get(candidate);
      if (interactable == null || transform == null || candidate.isScheduledForRemoval()) {
        continue;
      }
      float dist2 = centreDistance2(playerTransform.position, transform.position);
      if (dist2 <= interactable.range * interactable.range && dist2 < nearestDist2) {
        nearest = candidate;
        nearestDist2 = dist2;
      }
    }
    return nearest;
  }

  /**
   * Returns the on-screen prompt for an interactable, e.g. "[E] Pick up Coffee".
   *
   * <p>The key name comes from {@link #INTERACT_KEY}, so it follows the binding if it changes.
   *
   * @param interactable The interactable the prompt is for.
   * @return The prompt text.
   */
  public static String interactPrompt(InteractableComponent interactable) {
    return "[" + Input.Keys.toString(INTERACT_KEY) + "] " + interactable.promptText;
  }

  /**
   * Returns the squared distance between the centres of two rectangles.
   */
  private static float centreDistance2(Rectangle a, Rectangle b) {
    float dx = (a.x + a.width / 2) - (b.x + b.width / 2);
    float dy = (a.y + a.height / 2) - (b.y + b.height / 2);
    return dx * dx + dy * dy;
  }
}
