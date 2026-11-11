package com.eng1c4t3.goosesurvivor.items;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.eng1c4t3.goosesurvivor.components.HealthComponent;

/**
 * Restores health to the user, up to their maximum health.
 */
public class HealEffect implements ItemEffect {

  /** Amount of health restored per use. */
  public final float amount;

  /**
   * Constructs a new HealEffect.
   *
   * @param amount The amount of health restored per use.
   */
  public HealEffect(float amount) {
    this.amount = amount;
  }

  @Override
  public void apply(Entity user, Engine engine) {
    HealthComponent health = HealthComponent.MAPPER.get(user);
    if (health != null) {
      health.heal(amount);
    }
  }
}
