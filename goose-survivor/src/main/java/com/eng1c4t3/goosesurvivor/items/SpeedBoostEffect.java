package com.eng1c4t3.goosesurvivor.items;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.eng1c4t3.goosesurvivor.components.SpeedBoostComponent;
import com.eng1c4t3.goosesurvivor.components.VelocityComponent;

/**
 * Multiplies the user's max speed for a number of seconds.
 *
 * <p>Boosts do not stack: using a boost while already boosted refreshes the duration (to
 * whichever is longer) and applies the new multiplier.
 *
 * <p>Only {@link VelocityComponent#speedMultiplier} is changed, never the base
 * {@link VelocityComponent#maxSpeed}, so permanent speed upgrades gained during the boost are
 * kept when it ends. The SpeedBoostSystem counts the boost down and resets the multiplier.
 */
public class SpeedBoostEffect implements ItemEffect {

  /** Factor the user's speed is multiplied by, e.g. 1.5 for 50% faster. */
  public final float multiplier;
  /** How long the boost lasts, in seconds. */
  public final float duration;

  /**
   * Constructs a new SpeedBoostEffect.
   *
   * @param multiplier Factor the user's speed is multiplied by, e.g. 1.5 for 50% faster.
   * @param duration How long the boost lasts, in seconds.
   * @throws IllegalArgumentException if multiplier or duration is not positive.
   */
  public SpeedBoostEffect(float multiplier, float duration) {
    if (multiplier <= 0 || duration <= 0) {
      throw new IllegalArgumentException("Multiplier and duration must be positive");
    }
    this.multiplier = multiplier;
    this.duration = duration;
  }

  @Override
  public void apply(Entity user, Engine engine) {
    VelocityComponent velocity = VelocityComponent.MAPPER.get(user);
    if (velocity == null) {
      return;
    }

    SpeedBoostComponent boost = SpeedBoostComponent.MAPPER.get(user);
    if (boost == null) {
      boost = new SpeedBoostComponent(multiplier, duration);
      user.add(boost);
    } else {
      boost.multiplier = multiplier;
      boost.remainingTime = Math.max(boost.remainingTime, duration);
    }
    velocity.speedMultiplier = boost.multiplier;
  }
}
