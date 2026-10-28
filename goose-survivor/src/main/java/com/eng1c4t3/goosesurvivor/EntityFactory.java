package com.eng1c4t3.goosesurvivor;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.eng1c4t3.goosesurvivor.components.PlayerComponent;
import com.eng1c4t3.goosesurvivor.components.TextureComponent;
import com.eng1c4t3.goosesurvivor.components.TransformComponent;
import com.eng1c4t3.goosesurvivor.components.VelocityComponent;

/**
 * Factory for creating game entities with configured components.
 */
public final class EntityFactory {

  private EntityFactory() {
    // Utility class, no instantiation allowed
  }

  /**
   * Creates a player entity with default position and velocity.
   *
   * @param region The texture region for the player sprite.
   * @return A newly constructed player {@link Entity}.
   */
  public static Entity createPlayer(TextureRegion region) {
    return createPlayer(10f, 10f, 200f, region);
  }

  /**
   * Creates a player entity with custom initial coordinates and speed.
   *
   * @param x The initial x-coordinate.
   * @param y The initial y-coordinate.
   * @param speed The maximum movement speed.
   * @param region The texture region for the player sprite.
   * @return A newly constructed player {@link Entity}.
   */
  public static Entity createPlayer(float x, float y, float speed, TextureRegion region) {
    Entity player = new Entity();
    player.addComponent(new PlayerComponent());
    player.addComponent(new TransformComponent(x, y));
    player.addComponent(new VelocityComponent(speed));
    player.addComponent(new TextureComponent(region));
    return player;
  }
}
