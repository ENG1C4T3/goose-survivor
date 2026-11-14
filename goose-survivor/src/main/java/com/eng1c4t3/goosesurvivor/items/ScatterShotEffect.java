package com.eng1c4t3.goosesurvivor.items;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.eng1c4t3.goosesurvivor.components.TransformComponent;
import com.eng1c4t3.goosesurvivor.systems.ShooterSystem;

/**
 * Fires a fan of projectiles all at once from the user, centred on where the player is aiming
 * with the mouse (or the nearest enemy if there is no aim).
 *
 * <p>Does nothing if there is nothing to aim at or the engine has no {@link ShooterSystem}.
 */
public class ScatterShotEffect implements ItemEffect {

  /** Number of projectiles fired per use. */
  public final int projectiles;
  /** Total angle in degrees the projectiles are spread across. */
  public final float spreadDegrees;
  /** Launch speed of each projectile. */
  public final float speed;
  /** Damage dealt by each projectile. */
  public final float damage;
  /** Texture file of each projectile, or null for the default projectile. */
  public final String projectileTexture;

  /**
   * Constructs a new ScatterShotEffect.
   *
   * @param projectiles   Number of projectiles fired per use. Must be positive.
   * @param spreadDegrees Total angle in degrees the projectiles are spread across.
   * @param speed         Launch speed of each projectile.
   * @param damage        Damage dealt by each projectile.
   */
  public ScatterShotEffect(int projectiles, float spreadDegrees, float speed, float damage) {
    this(projectiles, spreadDegrees, speed, damage, null);
  }

  /**
   * Constructs a new ScatterShotEffect whose projectiles use the given texture.
   *
   * @param projectiles       Number of projectiles fired per use. Must be positive.
   * @param spreadDegrees     Total angle in degrees the projectiles are spread across.
   * @param speed             Launch speed of each projectile.
   * @param damage            Damage dealt by each projectile.
   * @param projectileTexture Texture file of each projectile, or null for the default.
   */
  public ScatterShotEffect(int projectiles, float spreadDegrees, float speed, float damage,
      String projectileTexture) {
    if (projectiles <= 0) {
      throw new IllegalArgumentException("projectiles must be positive");
    }
    this.projectiles = projectiles;
    this.spreadDegrees = spreadDegrees;
    this.speed = speed;
    this.damage = damage;
    this.projectileTexture = projectileTexture;
  }

  @Override
  public void apply(Entity user, Engine engine) {
    ShooterSystem shooter = engine.getSystem(ShooterSystem.class);
    TransformComponent userTransform = TransformComponent.MAPPER.get(user);
    Vector2 aim = new Vector2();
    if (shooter == null || userTransform == null || !shooter.aimDirection(user, aim)) {
      return;
    }
    Rectangle from = userTransform.position;
    float originX = from.x + from.width / 2;
    float originY = from.y + from.height / 2;
    aim.nor().scl(speed);

    float step = projectiles > 1 ? spreadDegrees / (projectiles - 1) : 0f;
    float start = projectiles > 1 ? -spreadDegrees / 2 : 0f;
    for (int i = 0; i < projectiles; ++i) {
      Vector2 velocity = new Vector2(aim).rotateDeg(start + step * i);
      shooter.fire(user, originX, originY, velocity, damage, projectileTexture);
    }
  }
}
