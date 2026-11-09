package com.eng1c4t3.goosesurvivor.entities;

import com.badlogic.ashley.core.Entity;
import com.eng1c4t3.goosesurvivor.GooseSurvivor;
import com.eng1c4t3.goosesurvivor.TextureManager;
import com.eng1c4t3.goosesurvivor.components.CollisionComponent;
import com.eng1c4t3.goosesurvivor.components.EnemyComponent;
import com.eng1c4t3.goosesurvivor.components.HealthComponent;
import com.eng1c4t3.goosesurvivor.components.PlayerComponent;
import com.eng1c4t3.goosesurvivor.components.TextureComponent;
import com.eng1c4t3.goosesurvivor.components.TransformComponent;
import com.eng1c4t3.goosesurvivor.components.VelocityComponent;

/**
 * Factory for creating game entities with configured components.
 */
public final class EntityFactory {

  public enum EnemyType {
    GOOSE,
    FAST_GOOSE,
    TANK_GOOSE,
    GRADIENT_GOOSE,
    LONG_BOI
  }

  private EntityFactory() {
  }


  public static Entity createEnemy(EnemyType type, float startX, float startY) {
    Entity enemy = new Entity();
    switch (type) {
      case FAST_GOOSE:
        enemy.add(new TransformComponent(startX, startY, 32f, 32f));
        enemy.add(new HealthComponent(100));
        enemy.add(new VelocityComponent(100));
        enemy.add(new EnemyComponent(5f, 60f));
        enemy.add(new TextureComponent(GooseSurvivor.textureManager.getTextureRegion(TextureManager.GOOSE_TEXTURE)));
        break;

      case LONG_BOI:
        enemy.add(new TransformComponent(startX, startY, 64f, 64f));
        enemy.add(new HealthComponent(500));
        enemy.add(new VelocityComponent(100f));
        enemy.add(new EnemyComponent(20f, 100f));
        enemy.add(new TextureComponent(GooseSurvivor.textureManager.getTextureRegion(TextureManager.LONG_BOI_TEXTURE)));
        break;
    }

    enemy.add(new CollisionComponent(CollisionComponent.Type.SOLID));
    if (!EnemyComponent.MAPPER.has(enemy)) {
      enemy.add(new EnemyComponent());
    }

    return enemy;
  }

  /**
   * Creates a player entity with custom initial coordinates.
   *
   * @param x The initial x-coordinate.
   * @param y The initial y-coordinate.
   *
   * @return A newly constructed player {@link Entity}.
   */
  public static Entity createPlayer(float x, float y) {
    Entity player = new Entity();
    player.add(new PlayerComponent());
    player.add(new TransformComponent(x, y, 32f, 32f));
    player.add(new HealthComponent(100));
    player.add(new VelocityComponent(100f));
    player.add(new TextureComponent(GooseSurvivor.textureManager.getTextureRegion(TextureManager.PLAYER_TEXTURE)));
    player.add(new CollisionComponent(CollisionComponent.Type.SOLID));
    return player;
  }

  /**
   * Creates a block entity with custom initial coordinates, dimensions, and texture region.
   *
   * @param x The initial x-coordinate.
   * @param y The initial y-coordinate.
   * @param width The width of the block.
   * @param height The height of the block.
   *
   * @return A newly constructed block {@link Entity}.
   */
  public static Entity createBlock(
      float x, float y, float width, float height) {
    Entity block = new Entity();
    block.add(new TransformComponent(x, y, width, height));
    block.add(new TextureComponent(GooseSurvivor.textureManager.getTextureRegion(TextureManager.BLOCK_TEXTURE)));
    block.add(new CollisionComponent(CollisionComponent.Type.SOLID));
    return block;
  }
}
