package com.eng1c4t3.goosesurvivor.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.eng1c4t3.goosesurvivor.components.PlayerComponent;
import com.eng1c4t3.goosesurvivor.components.VelocityComponent;
import com.eng1c4t3.goosesurvivor.ecs.Entity;
import com.eng1c4t3.goosesurvivor.ecs.EntitySystem;

/**
 * Handles player input and updates the velocity component accordingly.
 *
 * @see PlayerComponent
 * @see VelocityComponent
 */
public class InputSystem extends EntitySystem {

  /**
   * Handles player input and updates the velocity component accordingly.
   *
   * @param deltaTime The time passed since the last frame.
   */
  @Override
  public void update(float deltaTime) {
    for (Entity entity : engine.getEntities()) {
      // Only process entities that are the Player AND have Velocity
      if (entity.hasComponent(PlayerComponent.class)
          && entity.hasComponent(VelocityComponent.class)) {
        VelocityComponent vel = entity.getComponent(VelocityComponent.class);

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
  }
}
