package com.eng1c4t3.goosesurvivor.entities;

import com.badlogic.ashley.core.Entity;
import com.eng1c4t3.goosesurvivor.GooseSurvivor;
import com.eng1c4t3.goosesurvivor.TextureManager;
import com.eng1c4t3.goosesurvivor.components.CollisionComponent;
import com.eng1c4t3.goosesurvivor.components.EnemyComponent;
import com.eng1c4t3.goosesurvivor.components.HealOnInteractComponent;
import com.eng1c4t3.goosesurvivor.components.HealthComponent;
import com.eng1c4t3.goosesurvivor.components.InteractableComponent;
import com.eng1c4t3.goosesurvivor.components.PlayerComponent;
import com.eng1c4t3.goosesurvivor.components.TextureComponent;
import com.eng1c4t3.goosesurvivor.components.TransformComponent;
import com.eng1c4t3.goosesurvivor.components.VelocityComponent;

/**
 * Factory for creating game entities with configured components.
 */
public final class EntityFactory {

  /** Seconds the player is invulnerable for after being hit. */
  public static final float PLAYER_DAMAGE_COOLDOWN = 0.5f;

  /**
   * The kinds of enemy that can be created.
   */
  public enum EnemyType {
    GOOSE,
    FAST_GOOSE,
    TANK_GOOSE,
    GRADIENT_GOOSE,
    LONG_BOI
  }

  /**
   * The stats that distinguish one enemy type from another.
   *
   * @param size Width and height of the enemy.
   * @param health Maximum health.
   * @param speed Maximum movement speed.
   * @param damage Contact damage dealt to the player.
   * @param knockback Distance the enemy is knocked back after touching the player.
   * @param texture File name of the enemy's texture.
   */
  public record EnemyStats(float size, float health, float speed, float damage,
                           float knockback, String texture) {
  }

  private EntityFactory() {
  }

  /**
   * Returns the stats for an enemy type.
   *
   * @param type The enemy type.
   * @return The {@link EnemyStats} for that type.
   */
  public static EnemyStats statsFor(EnemyType type) {
    return switch (type) {
      case GOOSE -> new EnemyStats(32f, 100f, 70f, 10f, 80f, TextureManager.GOOSE_TEXTURE);
      case FAST_GOOSE -> new EnemyStats(32f, 100f, 100f, 5f, 60f, TextureManager.GOOSE_TEXTURE);
      case TANK_GOOSE -> new EnemyStats(48f, 300f, 40f, 15f, 40f, TextureManager.GOOSE_TEXTURE);
      case GRADIENT_GOOSE ->
          new EnemyStats(32f, 150f, 80f, 10f, 80f, TextureManager.GOOSE_TEXTURE);
      case LONG_BOI ->
          new EnemyStats(64f, 500f, 100f, 20f, 100f, TextureManager.LONG_BOI_TEXTURE);
    };
  }

  /**
   * Creates an enemy entity of the given type.
   *
   * @param type The enemy type, which decides its stats and texture.
   * @param startX The initial x-coordinate.
   * @param startY The initial y-coordinate.
   *
   * @return A newly constructed enemy {@link Entity}.
   */
  public static Entity createEnemy(EnemyType type, float startX, float startY) {
    EnemyStats stats = statsFor(type);

    Entity enemy = new Entity();
    enemy.add(new TransformComponent(startX, startY, stats.size(), stats.size()));
    enemy.add(new HealthComponent(stats.health()));
    enemy.add(new VelocityComponent(stats.speed()));
    enemy.add(new EnemyComponent(stats.damage(), stats.knockback()));
    enemy.add(new TextureComponent(
        GooseSurvivor.textureManager.getTextureRegion(stats.texture())));
    enemy.add(new CollisionComponent(CollisionComponent.Type.SOLID));
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
    player.add(new HealthComponent(100, PLAYER_DAMAGE_COOLDOWN));
    player.add(new VelocityComponent(100f));
    player.add(new TextureComponent(
        GooseSurvivor.textureManager.getTextureRegion(TextureManager.PLAYER_TEXTURE)));
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
    block.add(new TextureComponent(
        GooseSurvivor.textureManager.getTextureRegion(TextureManager.BLOCK_TEXTURE)));
    block.add(new CollisionComponent(CollisionComponent.Type.SOLID));
    return block;
  }

  /**
   * Creates a single-use health pack that heals the player when they interact with it.
   *
   * <p>The health pack has no collision, so the player can walk over it.
   *
   * @param x The x-coordinate.
   * @param y The y-coordinate.
   * @param healAmount The amount of health restored to the player.
   *
   * @return A newly constructed health pack {@link Entity}.
   */
  public static Entity createHealthPack(float x, float y, float healAmount) {
    Entity healthPack = new Entity();
    healthPack.add(new TransformComponent(x, y, 24f, 24f));
    healthPack.add(new TextureComponent(
        GooseSurvivor.textureManager.getTextureRegion(TextureManager.HEALTH_PACK_TEXTURE)));
    healthPack.add(new InteractableComponent(48f, "Press E to heal", true));
    healthPack.add(new HealOnInteractComponent(healAmount));
    return healthPack;
  }
}
