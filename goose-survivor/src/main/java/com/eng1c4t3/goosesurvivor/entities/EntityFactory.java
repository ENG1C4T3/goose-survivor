package com.eng1c4t3.goosesurvivor.entities;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.eng1c4t3.goosesurvivor.components.CollisionComponent;
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
  private static Entity createPlayer(float x, float y, float speed, TextureRegion region) {
    Entity player = new Entity();
    player.add(new PlayerComponent());
    player.add(new TransformComponent(x, y, 32f, 32f));
    player.add(new VelocityComponent(speed));
    player.add(new TextureComponent(region));
    player.add(new CollisionComponent(CollisionComponent.CollisionType.PLAYER));
    return player;
  }

  /**
   * Creates a block entity with custom initial coordinates, dimensions, and texture region.
   *
   * @param x The initial x-coordinate.
   * @param y The initial y-coordinate.
   * @param width The width of the block.
   * @param height The height of the block.
   * @param region The texture region for the block sprite.
   * @return A newly constructed block {@link Entity}.
   */
  public static Entity createBlock(
      float x, float y, float width, float height, TextureRegion region) {
    Entity block = new Entity();
    block.add(new TransformComponent(x, y, width, height));
    block.add(new TextureComponent(region));
    block.add(new CollisionComponent(CollisionComponent.CollisionType.SOLID));
    return block;
  }
}
