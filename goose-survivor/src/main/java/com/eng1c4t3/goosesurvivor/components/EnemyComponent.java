package com.eng1c4t3.goosesurvivor.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

/**
 * Component for enemy entities.
 *
 * <p>Defines how much damage the enemy deals when it touches the player, and how far it is
 * knocked back afterwards. While {@link #knockbackTimer} is positive the enemy is being knocked
 * back and does not steer towards the player.
 */
public class EnemyComponent implements Component {
  public static final ComponentMapper<EnemyComponent> MAPPER =
      ComponentMapper.getFor(EnemyComponent.class);

  public static final float DEFAULT_DAMAGE = 10f;
  public static final float DEFAULT_KNOCKBACK_DISTANCE = 80f;
  public static final float DEFAULT_KNOCKBACK_DURATION = 0.2f;

  /** Damage dealt to the player on contact. */
  public float damage;
  /** Total distance the enemy is pushed back after touching the player. */
  public float knockbackDistance;
  /** Time in seconds the knockback takes to cover {@link #knockbackDistance}. */
  public float knockbackDuration;
  /** Remaining knockback time in seconds; the enemy is knocked back while this is positive. */
  public float knockbackTimer;

  /**
   * Constructs an EnemyComponent with default damage and knockback values.
   */
  public EnemyComponent() {
    this(DEFAULT_DAMAGE, DEFAULT_KNOCKBACK_DISTANCE);
  }

  /**
   * Constructs an EnemyComponent with the given damage and knockback distance.
   *
   * @param damage Damage dealt to the player on contact.
   * @param knockbackDistance Distance the enemy is pushed back after touching the player.
   */
  public EnemyComponent(float damage, float knockbackDistance) {
    this.damage = damage;
    this.knockbackDistance = knockbackDistance;
    this.knockbackDuration = DEFAULT_KNOCKBACK_DURATION;
  }

  /**
   * Returns whether the enemy is currently being knocked back.
   *
   * @return {@code true} if a knockback is in progress.
   */
  public boolean isKnockedBack() {
    return knockbackTimer > 0;
  }
}
