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
import com.eng1c4t3.goosesurvivor.components.PlayerComponent;
import com.eng1c4t3.goosesurvivor.components.TransformComponent;
import com.eng1c4t3.goosesurvivor.components.TriggerEventComponent;
import com.eng1c4t3.goosesurvivor.components.VelocityComponent;

/**
 * Handles player input: updates the player's velocity from the movement keys, and raises a
 * trigger event when the interact key is pressed within range of an interactable.
 *
 * @see PlayerComponent
 * @see VelocityComponent
 * @see InteractableComponent
 * @see TriggerEventComponent
 */
public class InputSystem extends IteratingSystem {

  /** The key that interacts with the nearest interactable in range. */
  public static final int INTERACT_KEY = Input.Keys.E;

  private ImmutableArray<Entity> interactables;

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

    // Normalize the vector so diagonal movement isn't faster, then scale to max speed
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
    TransformComponent playerTransform = TransformComponent.MAPPER.get(player);
    if (playerTransform == null) {
      return;
    }

    Entity nearest = null;
    float nearestDist2 = Float.MAX_VALUE;
    for (int i = 0; i < interactables.size(); ++i) {
      Entity candidate = interactables.get(i);
      float range = InteractableComponent.MAPPER.get(candidate).range;
      float dist2 = centreDistance2(playerTransform.position,
          TransformComponent.MAPPER.get(candidate).position);

      if (dist2 <= range * range && dist2 < nearestDist2) {
        nearest = candidate;
        nearestDist2 = dist2;
      }
    }

    if (nearest != null) {
      Entity event = new Entity();
      event.add(new TriggerEventComponent(
          player, nearest, TriggerEventComponent.ActionType.INTERACT));
      getEngine().addEntity(event);
    }
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
