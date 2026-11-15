package com.eng1c4t3.goosesurvivor.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.eng1c4t3.goosesurvivor.systems.ShooterSystem;

/**
 * A stream of projectiles an entity is currently firing, e.g. from the Food Stream Launcher.
 *
 * <p>One projectile is fired every {@link #interval} seconds towards where the player is aiming
 * at that moment (or the nearest enemy if there is no aim), so the stream can be swept around.
 * The component is removed once every shot has been fired.
 *
 * @see ShooterSystem
 */
public class StreamFireComponent implements Component {
  public static final ComponentMapper<StreamFireComponent> MAPPER =
      ComponentMapper.getFor(StreamFireComponent.class);

  /** Shots still to fire. */
  public int shotsRemaining;
  /** Number of shots fired so far, used to wobble each shot. */
  public int shotsFired;
  /** Seconds between shots. */
  public final float interval;
  /** Seconds until the next shot; the first shot fires straight away. */
  public float timer;
  /** Launch speed of each projectile. */
  public final float speed;
  /** Damage dealt by each projectile. */
  public final float damage;
  /** Total angle in degrees each shot wobbles across, so the stream looks like a spray. */
  public final float wobbleDegrees;
  /** Texture file of each projectile, or null for the default projectile. */
  public String projectileTexture;

  /**
   * Constructs a new StreamFireComponent.
   *
   * @param shots         Number of projectiles in the stream. Must be positive.
   * @param interval      Seconds between shots. Must be positive.
   * @param speed         Launch speed of each projectile.
   * @param damage        Damage dealt by each projectile.
   * @param wobbleDegrees Total angle in degrees each shot wobbles across.
   */
  public StreamFireComponent(int shots, float interval, float speed, float damage,
      float wobbleDegrees) {
    if (shots <= 0 || interval <= 0f) {
      throw new IllegalArgumentException("shots and interval must be positive");
    }
    this.shotsRemaining = shots;
    this.interval = interval;
    this.speed = speed;
    this.damage = damage;
    this.wobbleDegrees = wobbleDegrees;
  }
}
