package com.eng1c4t3.goosesurvivor.components;

import com.badlogic.gdx.math.Vector2;
import com.eng1c4t3.goosesurvivor.systems.MovementSystem;

/**
 * Represents the position of an entity in the game.
 *
 * <p>The position of an entity in the game, represented by a {@link Vector2}.
 *
 * @see MovementSystem
 */
public class TransformComponent implements Component {
  public final Vector2 position;

  /**
   * Constructs a new TransformComponent with the given position.
   *
   * @param x The x-coordinate of the entity's position.
   * @param y The y-coordinate of the entity's position.
   */
  public TransformComponent(float x, float y) {
    this.position = new Vector2(x, y);
  }
}
