package com.eng1c4t3.goosesurvivor.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.eng1c4t3.goosesurvivor.components.TransformComponent;
import com.eng1c4t3.goosesurvivor.components.VelocityComponent;

/**
 * Handles positional translation based on velocity.
 */
public class MovementSystem extends IteratingSystem {

  /**
   * Constructs a new MovementSystem.
   */
  public MovementSystem() {
    super(Family.all(TransformComponent.class, VelocityComponent.class).get());
  }

  /**
   * Updates the position of the entity based on its velocity.
   *
   * @param entity The current Entity being processed
   * @param deltaTime The delta time between the last and current frame
   */
  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    TransformComponent transform = TransformComponent.MAPPER.get(entity);
    VelocityComponent velocity = VelocityComponent.MAPPER.get(entity);

    transform.position.x += velocity.velocity.x * deltaTime;
    transform.position.y += velocity.velocity.y * deltaTime;
  }
}
