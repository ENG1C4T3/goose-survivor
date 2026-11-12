package com.eng1c4t3.goosesurvivor.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;
import com.eng1c4t3.goosesurvivor.systems.MovementSystem;

/**
 * Represents the movement velocity of an entity in the game.
 * This component holds the velocity vector and the maximum speed for the entity.
 *
 * <p>The velocity is represented as a {@link Vector2}, which indicates the direction
 * and magnitude of movement. The speed determines the maximum rate of movement
 * and is used to scale the velocity vector if needed.
 *
 * <p>Speed is split in two so permanent and temporary changes never overwrite each other:
 * {@link #maxSpeed} is the permanent base speed (change it for upgrades/rewards), and
 * {@link #speedMultiplier} is the temporary factor from effects such as speed boosts. Movement
 * code should use {@link #effectiveMaxSpeed()}.
 *
 * @see MovementSystem
 */
public class VelocityComponent implements Component {
  public static final ComponentMapper<VelocityComponent> MAPPER =
      ComponentMapper.getFor(VelocityComponent.class);

  public final Vector2 velocity;
  /** Permanent base max speed. Permanent upgrades change this. */
  public float maxSpeed;
  /** Temporary speed factor from effects; 1 means no effect. Not for permanent upgrades. */
  public float speedMultiplier = 1f;

  /**
   * Constructs a VelocityComponent with a specified maximum speed.
   *
   * <p>The velocity vector is initialised to (0, 0).
   *
   * @param maxSpeed The maximum speed of the entity represented by this component.
   */
  public VelocityComponent(float maxSpeed) {
    this.velocity = new Vector2(0, 0);
    this.maxSpeed = maxSpeed;
  }

  /**
   * Returns the speed the entity actually moves at: the base speed times any temporary
   * multiplier.
   *
   * @return {@link #maxSpeed} multiplied by {@link #speedMultiplier}.
   */
  public float effectiveMaxSpeed() {
    return maxSpeed * speedMultiplier;
  }
}
