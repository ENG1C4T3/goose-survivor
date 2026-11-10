package com.eng1c4t3.goosesurvivor.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.eng1c4t3.goosesurvivor.systems.HealthSystem;

/**
 * Holds an entity's health.
 *
 * <p>After taking damage the entity is invulnerable for {@link #damageCooldown} seconds, so
 * several hits in quick succession only count once. {@link HealthSystem} counts the cooldown
 * down each frame.
 */
public class HealthComponent implements Component {
  public static final ComponentMapper<HealthComponent> MAPPER =
      ComponentMapper.getFor(HealthComponent.class);

  public float health;
  public float maxHealth;
  /** Time in seconds the entity is invulnerable for after taking damage. */
  public float damageCooldown;
  /** Remaining invulnerability time in seconds; damage is ignored while this is positive. */
  public float damageCooldownTimer;

  /**
   * Constructs a HealthComponent at full health with no damage cooldown.
   *
   * @param maxHealth The maximum (and starting) health.
   */
  public HealthComponent(float maxHealth) {
    this(maxHealth, 0f);
  }

  /**
   * Constructs a HealthComponent at full health.
   *
   * @param maxHealth The maximum (and starting) health.
   * @param damageCooldown Time in seconds the entity is invulnerable for after taking damage.
   */
  public HealthComponent(float maxHealth, float damageCooldown) {
    this.maxHealth = maxHealth;
    this.health = maxHealth;
    this.damageCooldown = damageCooldown;
  }

  /**
   * Deals damage unless the entity is still in its damage cooldown, then starts the cooldown.
   *
   * @param damage The amount of damage to deal.
   * @return {@code true} if the damage was applied, {@code false} if it was ignored.
   */
  public boolean takeDamage(float damage) {
    if (isInvulnerable()) {
      return false;
    }
    health -= damage;
    if (health < 0) {
      health = 0;
    }
    damageCooldownTimer = damageCooldown;
    return true;
  }

  /**
   * Restores health, up to {@link #maxHealth}.
   *
   * @param amount The amount of health to restore.
   */
  public void heal(float amount) {
    health += amount;
    if (health > maxHealth) {
      health = maxHealth;
    }
  }

  /**
   * Returns whether the entity is in its damage cooldown.
   *
   * @return {@code true} if damage is currently ignored.
   */
  public boolean isInvulnerable() {
    return damageCooldownTimer > 0;
  }

  /**
   * Returns whether the entity has run out of health.
   *
   * @return {@code true} if health is zero or less.
   */
  public boolean isDead() {
    return health <= 0;
  }
}
