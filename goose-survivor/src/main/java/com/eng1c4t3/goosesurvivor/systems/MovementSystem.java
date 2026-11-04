package com.eng1c4t3.goosesurvivor.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.eng1c4t3.goosesurvivor.components.TransformComponent;
import com.eng1c4t3.goosesurvivor.components.VelocityComponent;

/**
 * Handles the movement of entities based on their velocity.
 *
 * @see TransformComponent
 * @see VelocityComponent
 */
public class MovementSystem extends IteratingSystem {

  /**
   * Constructs a MovementSystem that processes entities with TransformComponent
   * and VelocityComponent.
   */
  public MovementSystem() {
    super(Family.all(TransformComponent.class, VelocityComponent.class).get());
  }

  /**
   * Updates the position of the entity based on its velocity.
   *
   * @param entity The entity to process.
   * @param deltaTime The time passed since the last frame.
   */
  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    TransformComponent transform = TransformComponent.MAPPER.get(entity);
    VelocityComponent velocity = VelocityComponent.MAPPER.get(entity);

    transform.position.x += velocity.velocity.x * deltaTime;
    transform.position.y += velocity.velocity.y * deltaTime;
  }
}
