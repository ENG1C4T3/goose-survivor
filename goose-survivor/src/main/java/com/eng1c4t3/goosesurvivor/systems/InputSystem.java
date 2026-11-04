package com.eng1c4t3.goosesurvivor.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.eng1c4t3.goosesurvivor.components.PlayerComponent;
import com.eng1c4t3.goosesurvivor.components.VelocityComponent;

/**
 * Handles player input and updates the velocity component accordingly.
 *
 * @see PlayerComponent
 * @see VelocityComponent
 */
public class InputSystem extends IteratingSystem {

  /**
   * Constructs an InputSystem that processes entities with PlayerComponent and VelocityComponent.
   */
  public InputSystem() {
    super(Family.all(PlayerComponent.class, VelocityComponent.class).get());
  }

  /**
   * Handles player input and updates the velocity component of the entity accordingly.
   *
   * @param entity The entity to process.
   * @param deltaTime The time passed since the last frame.
   */
  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    VelocityComponent vel = VelocityComponent.MAPPER.get(entity);

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
}
