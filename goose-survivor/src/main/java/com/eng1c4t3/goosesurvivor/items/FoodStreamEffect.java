package com.eng1c4t3.goosesurvivor.items;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.eng1c4t3.goosesurvivor.components.StreamFireComponent;
import com.eng1c4t3.goosesurvivor.systems.ShooterSystem;

/**
 * Starts a stream of food projectiles from the user. The shots are fired one after another by
 * the {@link ShooterSystem}, each aimed at the mouse at the moment it fires, so the player can
 * sweep the stream across enemies.
 *
 * <p>Using it again while a stream is still going restarts the stream.
 *
 * @see StreamFireComponent
 */
public class FoodStreamEffect implements ItemEffect {

  /** Number of projectiles in each stream. */
  public final int shots;
  /** Seconds between shots. */
  public final float interval;
  /** Launch speed of each projectile. */
  public final float speed;
  /** Damage dealt by each projectile. */
  public final float damage;
  /** Total angle in degrees each shot wobbles across. */
  public final float wobbleDegrees;
  /** Texture file of each projectile, or null for the default projectile. */
  public final String projectileTexture;

  /**
   * Constructs a new FoodStreamEffect.
   *
   * @param shots         Number of projectiles in each stream. Must be positive.
   * @param interval      Seconds between shots. Must be positive.
   * @param speed         Launch speed of each projectile.
   * @param damage        Damage dealt by each projectile.
   * @param wobbleDegrees Total angle in degrees each shot wobbles across.
   */
  public FoodStreamEffect(int shots, float interval, float speed, float damage,
      float wobbleDegrees) {
    this(shots, interval, speed, damage, wobbleDegrees, null);
  }

  /**
   * Constructs a new FoodStreamEffect whose projectiles use the given texture.
   *
   * @param shots             Number of projectiles in each stream. Must be positive.
   * @param interval          Seconds between shots. Must be positive.
   * @param speed             Launch speed of each projectile.
   * @param damage            Damage dealt by each projectile.
   * @param wobbleDegrees     Total angle in degrees each shot wobbles across.
   * @param projectileTexture Texture file of each projectile, or null for the default.
   */
  public FoodStreamEffect(int shots, float interval, float speed, float damage,
      float wobbleDegrees, String projectileTexture) {
    if (shots <= 0 || interval <= 0f) {
      throw new IllegalArgumentException("shots and interval must be positive");
    }
    this.shots = shots;
    this.interval = interval;
    this.speed = speed;
    this.damage = damage;
    this.wobbleDegrees = wobbleDegrees;
    this.projectileTexture = projectileTexture;
  }

  @Override
  public void apply(Entity user, Engine engine) {
    StreamFireComponent stream =
        new StreamFireComponent(shots, interval, speed, damage, wobbleDegrees);
    stream.projectileTexture = projectileTexture;
    user.add(stream);
  }
}
