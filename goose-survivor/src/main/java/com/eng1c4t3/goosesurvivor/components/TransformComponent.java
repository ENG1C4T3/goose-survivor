package com.eng1c4t3.goosesurvivor.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Rectangle;
import com.eng1c4t3.goosesurvivor.systems.MovementSystem;

/**
 * The single source of truth for an entity's position in the game.
 *
 * <p>The position of an entity in the game is represented by a {@link Rectangle}.
 *
 * @see MovementSystem
 */
public class TransformComponent implements Component {
  public static final ComponentMapper<TransformComponent> MAPPER =
      ComponentMapper.getFor(TransformComponent.class);

  public final Rectangle position;

  /**
   * Constructs a new TransformComponent with the given position.
   *
   * @param x The x-coordinate of the entity's position.
   * @param y The y-coordinate of the entity's position.
   * @param width The width of the entity.
   * @param height The height of the entity.
   */
  public TransformComponent(float x, float y, float width, float height) {
    this.position = new Rectangle(x, y, width, height);
  }
}
