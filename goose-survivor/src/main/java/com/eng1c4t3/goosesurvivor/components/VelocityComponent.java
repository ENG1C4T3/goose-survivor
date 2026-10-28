package com.eng1c4t3.goosesurvivor.components;

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
 * @see MovementSystem
 */
public class VelocityComponent implements Component {
  public final Vector2 velocity;
  public float maxSpeed;

  /**
   * Constructs a VelocityComponent with a specified maximum speed.
   * Initialises the velocity vector to (0, 0).
   *
   * @param maxSpeed The maximum speed of the entity represented by this component.
   */
  public VelocityComponent(float maxSpeed) {
    this.velocity = new Vector2(0, 0);
    this.maxSpeed = maxSpeed;
  }
}