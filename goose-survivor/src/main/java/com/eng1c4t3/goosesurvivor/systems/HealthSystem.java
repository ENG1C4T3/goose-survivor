package com.eng1c4t3.goosesurvivor.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.eng1c4t3.goosesurvivor.components.HealthComponent;

/**
 * Counts down damage cooldowns and removes entities that have run out of health.
 */
public class HealthSystem extends IteratingSystem {

  /**
   * Constructs a HealthSystem that processes entities with a HealthComponent.
   */
  public HealthSystem() {
    super(Family.all(HealthComponent.class).get());
  }

  /**
   * Ticks the entity's damage cooldown and removes it from the engine if it is dead.
   *
   * @param entity The current Entity being processed
   * @param deltaTime The delta time between the last and current frame
   */
  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    HealthComponent healthComponent = HealthComponent.MAPPER.get(entity);
    if (healthComponent.damageCooldownTimer > 0) {
      healthComponent.damageCooldownTimer =
          Math.max(0f, healthComponent.damageCooldownTimer - deltaTime);
    }
    if (healthComponent.isDead()) {
      getEngine().removeEntity(entity);
    }
  }
}
