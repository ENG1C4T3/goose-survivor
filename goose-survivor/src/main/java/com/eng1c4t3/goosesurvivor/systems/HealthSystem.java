package com.eng1c4t3.goosesurvivor.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.eng1c4t3.goosesurvivor.components.HealthComponent;

/**
 * This system removes entities when their health component is dead.
 */
public class HealthSystem extends IteratingSystem {
  public HealthSystem() {
    super(Family.all(HealthComponent.class).get());
  }

  /**
   * This method is called on every entity on every update call of the EntitySystem. Override this to implement your system's
   * specific processing.
   *
   * @param entity    The current Entity being processed
   * @param deltaTime The delta time between the last and current frame
   */
  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    HealthComponent healthComponent = HealthComponent.MAPPER.get(entity);
    if (healthComponent.isDead()) {
      getEngine().removeEntity(entity);
    }
  }
}
