package com.eng1c4t3.goosesurvivor.entities;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.eng1c4t3.goosesurvivor.TextureManager;
import com.eng1c4t3.goosesurvivor.components.CollisionComponent;
import com.eng1c4t3.goosesurvivor.components.EnemyComponent;
import com.eng1c4t3.goosesurvivor.components.HealOnInteractComponent;
import com.eng1c4t3.goosesurvivor.components.HealthComponent;
import com.eng1c4t3.goosesurvivor.components.InteractableComponent;
import com.eng1c4t3.goosesurvivor.components.PlayerComponent;
import com.eng1c4t3.goosesurvivor.components.ProjectileComponent;
import com.eng1c4t3.goosesurvivor.components.ShooterComponent;
import com.eng1c4t3.goosesurvivor.components.TextureComponent;
import com.eng1c4t3.goosesurvivor.components.TransformComponent;
import com.eng1c4t3.goosesurvivor.components.VelocityComponent;

/**
 * Factory for creating game entities with configured components.
 */
public final class EntityFactory {

  /** Seconds the player is invulnerable for after being hit. */
  public static final float PLAYER_DAMAGE_COOLDOWN = 0.5f;

  /** Width and height of a projectile. */
  public static final float PROJECTILE_SIZE = 16f;
  /** Speed a projectile loses per second. */
  public static final float PROJECTILE_DECELERATION = 100f;
  /** Speed below which a projectile despawns. */
  public static final float PROJECTILE_MIN_SPEED = 50f;

  /** Seconds between Long Boi's shots at the player. */
  public static final float LONG_BOI_SHOT_INTERVAL = 20f;
  /** Initial speed of Long Boi's projectiles. */
  public static final float LONG_BOI_PROJECTILE_SPEED = 300f;
  /** Damage dealt by Long Boi's projectiles. */
  public static final float LONG_BOI_PROJECTILE_DAMAGE = 15f;

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
   * @param size      Width and height of the enemy.
   * @param health    Maximum health.
   * @param speed     Maximum movement speed.
   * @param damage    Contact damage dealt to the player.
   * @param knockback Distance the enemy is knocked back after touching the
   *                  player.
   * @param texture   File name of the enemy's texture.
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
   * @param type   The enemy type, which decides its stats and texture.
   * @param startX The initial x-coordinate.
   * @param startY The initial y-coordinate.
   * @param region The texture region for entity sprite
   *
   * @return A newly constructed enemy {@link Entity}.
   */
  public static Entity createEnemy(
      EnemyType type, float startX, float startY, TextureRegion region) {
    EnemyStats stats = statsFor(type);

    Entity enemy = new Entity();
    enemy.add(new TransformComponent(startX, startY, stats.size(), stats.size()));
    enemy.add(new HealthComponent(stats.health()));
    enemy.add(new VelocityComponent(stats.speed()));
    enemy.add(new EnemyComponent(stats.damage(), stats.knockback()));
    enemy.add(new TextureComponent(region));
    enemy.add(new CollisionComponent(CollisionComponent.Type.SOLID));
    if (type == EnemyType.LONG_BOI) {
      enemy.add(new ShooterComponent(
          LONG_BOI_SHOT_INTERVAL, LONG_BOI_PROJECTILE_SPEED, LONG_BOI_PROJECTILE_DAMAGE));
    }
    return enemy;
  }

  /**
   * Creates a projectile centred on the given point.
   *
   * <p>The projectile is a trigger, so it never pushes other entities around; the
   * {@link com.eng1c4t3.goosesurvivor.systems.ProjectileSystem} decides what it does on a hit.
   *
   * @param owner The entity that fired the projectile; it is never hit by it.
   * @param centreX The x-coordinate of the projectile's centre.
   * @param centreY The y-coordinate of the projectile's centre.
   * @param velocity The initial velocity of the projectile (copied).
   * @param damage The damage dealt to whatever the projectile hits.
   * @param region The texture region for the projectile.
   *
   * @return A newly constructed projectile {@link Entity}.
   */
  public static Entity createProjectile(
      Entity owner, float centreX, float centreY,
      Vector2 velocity, float damage, TextureRegion region) {
    Entity projectile = new Entity();
    projectile.add(new TransformComponent(centreX - PROJECTILE_SIZE / 2,
        centreY - PROJECTILE_SIZE / 2, PROJECTILE_SIZE, PROJECTILE_SIZE));
    VelocityComponent velocityComponent = new VelocityComponent(velocity.len());
    velocityComponent.velocity.set(velocity);
    projectile.add(velocityComponent);
    projectile.add(new ProjectileComponent(
        owner, damage, PROJECTILE_DECELERATION, PROJECTILE_MIN_SPEED));
    projectile.add(new CollisionComponent(CollisionComponent.Type.TRIGGER));
    projectile.add(new TextureComponent(region));
    return projectile;
  }

  /**
   * Creates a player entity with custom initial coordinates.
   *
   * @param x      The initial x-coordinate.
   * @param y      The initial y-coordinate.
   * @param region The texture region for entity sprite
   *
   * @return A newly constructed player {@link Entity}.
   */
  public static Entity createPlayer(float x, float y, TextureRegion region) {
    Entity player = new Entity();
    player.add(new PlayerComponent());
    player.add(new TransformComponent(x, y, 32f, 32f));
    player.add(new HealthComponent(100, PLAYER_DAMAGE_COOLDOWN));
    player.add(new VelocityComponent(100f));
    player.add(new TextureComponent(region));
    player.add(new CollisionComponent(CollisionComponent.Type.SOLID));
    return player;
  }

  /**
   * Creates a block entity with custom initial coordinates, dimensions, and
   * texture region.
   *
   * @param x      The initial x-coordinate.
   * @param y      The initial y-coordinate.
   * @param width  The width of the block.
   * @param height The height of the block.
   * @param region The texture region for entity sprite
   *
   * @return A newly constructed block {@link Entity}.
   */
  public static Entity createBlock(
      float x, float y, float width, float height, TextureRegion region) {
    Entity block = new Entity();
    block.add(new TransformComponent(x, y, width, height));
    block.add(new TextureComponent(region));
    block.add(new CollisionComponent(CollisionComponent.Type.SOLID));
    return block;
  }

  /**
   * Creates a single-use health pack that heals the player when they interact
   * with it.
   *
   * <p>The health pack has no collision, so the player can walk over it.
   *
   * @param x          The x-coordinate.
   * @param y          The y-coordinate.
   * @param healAmount The amount of health restored to the player.
   * @param region     The texture region for entity sprite
   *
   * @return A newly constructed health pack {@link Entity}.
   */
  public static Entity createHealthPack(float x, float y, float healAmount, TextureRegion region) {
    Entity healthPack = new Entity();
    healthPack.add(new TransformComponent(x, y, 24f, 24f));
    healthPack.add(new TextureComponent(region));
    healthPack.add(new InteractableComponent(48f, "Press E to heal", true));
    healthPack.add(new HealOnInteractComponent(healAmount));
    return healthPack;
  }
}
