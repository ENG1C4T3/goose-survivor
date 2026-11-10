package com.eng1c4t3.goosesurvivor.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.eng1c4t3.goosesurvivor.systems.ShooterSystem;

/**
 * Lets an entity periodically fire a projectile at the player.
 *
 * @see ShooterSystem
 * @see ProjectileComponent
 */
public class ShooterComponent implements Component {
  public static final ComponentMapper<ShooterComponent> MAPPER =
      ComponentMapper.getFor(ShooterComponent.class);

  /** Seconds between shots. */
  public float shotInterval;
  /** Seconds until the next shot. */
  public float shotTimer;
  /** Initial speed of each projectile. */
  public float projectileSpeed;
  /** Damage dealt by each projectile. */
  public float projectileDamage;

  /**
   * Constructs a new ShooterComponent. The first shot is fired after one full interval.
   *
   * @param shotInterval Seconds between shots.
   * @param projectileSpeed Initial speed of each projectile.
   * @param projectileDamage Damage dealt by each projectile.
   */
  public ShooterComponent(float shotInterval, float projectileSpeed, float projectileDamage) {
    this.shotInterval = shotInterval;
    this.shotTimer = shotInterval;
    this.projectileSpeed = projectileSpeed;
    this.projectileDamage = projectileDamage;
  }
}
