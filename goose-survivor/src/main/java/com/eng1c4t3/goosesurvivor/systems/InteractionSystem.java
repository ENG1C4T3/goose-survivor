package com.eng1c4t3.goosesurvivor.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.eng1c4t3.goosesurvivor.SessionStats;
import com.eng1c4t3.goosesurvivor.components.HiddenEventComponent;
import com.eng1c4t3.goosesurvivor.components.InteractableComponent;
import com.eng1c4t3.goosesurvivor.components.InventoryComponent;
import com.eng1c4t3.goosesurvivor.components.ItemPickupComponent;
import com.eng1c4t3.goosesurvivor.components.TriggerEventComponent;

/**
 * Processes trigger event entities and executes the matching game logic.
 *
 * <p>Must run after {@link InputSystem} (which creates the events) and before
 * {@link EventCleanupSystem} (which destroys them).
 *
 * @see TriggerEventComponent
 */
public class InteractionSystem extends IteratingSystem {

  private final SessionStats stats;

  /**
   * Constructs an InteractionSystem that processes trigger event entities and records
   * nothing.
   */
  public InteractionSystem() {
    this(null);
  }

  /**
   * Constructs an InteractionSystem that records successful interactions.
   *
   * @param stats Where interactions are recorded, or null to record nothing.
   */
  public InteractionSystem(SessionStats stats) {
    super(Family.all(TriggerEventComponent.class).get());
    this.stats = stats;
  }

  /**
   * Handles a single trigger event.
   *
   * @param entity    The event entity.
   * @param deltaTime The time passed since the last frame.
   */
  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    TriggerEventComponent event = TriggerEventComponent.MAPPER.get(entity);
    Entity target = event.targetInteractable;

    // The target may have been used up by an earlier event this frame, or stopped
    // being
    // interactable since the event was raised
    if (target == null
        || target.isScheduledForRemoval()
        || !InteractableComponent.MAPPER.has(target)) {
      return;
    }

    if (HiddenEventComponent.MAPPER.has(target)) {
      return;
    }

    switch (event.action) {
      case INTERACT:
        handleInteract(event);
        break;
      default:
        break;
    }
  }

  /**
   * Gives the player the target's item, if it is a pickup, then removes
   * the target if
   * it is single use.
   *
   * <p>If the target is an item pickup and the player cannot take the item (no inventory, or
   * the inventory is full), nothing happens and the target stays in the world.
   *
   * @param event The interaction event.
   */
  protected void handleInteract(TriggerEventComponent event) {
    Entity player = event.sourcePlayer;
    Entity target = event.targetInteractable;

    ItemPickupComponent pickup = ItemPickupComponent.MAPPER.get(target);
    if (pickup != null) {
      InventoryComponent inventory = InventoryComponent.MAPPER.get(player);
      if (inventory == null || inventory.addItem(pickup.item) < 0) {
        return;
      }
    }

    if (stats != null) {
      stats.recordInteraction();
    }
    if (InteractableComponent.MAPPER.get(target).singleUse) {
      getEngine().removeEntity(target);
    }
  }
}
