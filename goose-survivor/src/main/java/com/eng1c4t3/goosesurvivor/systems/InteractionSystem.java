package com.eng1c4t3.goosesurvivor.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.eng1c4t3.goosesurvivor.components.HealOnInteractComponent;
import com.eng1c4t3.goosesurvivor.components.HealthComponent;
import com.eng1c4t3.goosesurvivor.components.InteractableComponent;
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

  /**
   * Constructs an InteractionSystem that processes trigger event entities.
   */
  public InteractionSystem() {
    super(Family.all(TriggerEventComponent.class).get());
  }

  /**
   * Handles a single trigger event.
   *
   * @param entity The event entity.
   * @param deltaTime The time passed since the last frame.
   */
  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    TriggerEventComponent event = TriggerEventComponent.MAPPER.get(entity);
    Entity target = event.targetInteractable;

    // The target may have been used up by an earlier event this frame, or stopped being
    // interactable since the event was raised
    if (target == null
        || target.isScheduledForRemoval()
        || !InteractableComponent.MAPPER.has(target)) {
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
   * Applies every interaction effect on the target to the player, then removes the target if
   * it is single use.
   *
   * @param event The interaction event.
   */
  protected void handleInteract(TriggerEventComponent event) {
    Entity player = event.sourcePlayer;
    Entity target = event.targetInteractable;

    HealOnInteractComponent heal = HealOnInteractComponent.MAPPER.get(target);
    HealthComponent playerHealth = HealthComponent.MAPPER.get(player);
    if (heal != null && playerHealth != null) {
      playerHealth.heal(heal.amount);
    }

    if (InteractableComponent.MAPPER.get(target).singleUse) {
      getEngine().removeEntity(target);
    }
  }
}
