package com.eng1c4t3.goosesurvivor.systems;

import com.eng1c4t3.goosesurvivor.components.TransformComponent;
import com.eng1c4t3.goosesurvivor.components.VelocityComponent;
import com.eng1c4t3.goosesurvivor.ecs.Entity;
import com.eng1c4t3.goosesurvivor.ecs.EntitySystem;

/**
 * Handles the movement of entities based on their velocity.
 *
 * @see TransformComponent
 * @see VelocityComponent
 */
public class MovementSystem extends EntitySystem {

  /**
   * Handles the movement of entities based on their velocity.
   *
   * @param deltaTime The time passed since the last frame.
   */
  @Override
  public void update(float deltaTime) {

    for (Entity entity : engine.getEntities()) {
      if (entity.hasComponent(TransformComponent.class)
          && entity.hasComponent(VelocityComponent.class)) {

        TransformComponent transform = entity.getComponent(TransformComponent.class);
        VelocityComponent velocity = entity.getComponent(VelocityComponent.class);
        transform.position.x += velocity.velocity.x * deltaTime;
        transform.position.y += velocity.velocity.y * deltaTime;
      }
    }
  }
}
