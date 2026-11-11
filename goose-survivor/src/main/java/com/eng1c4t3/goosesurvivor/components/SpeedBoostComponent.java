package com.eng1c4t3.goosesurvivor.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.eng1c4t3.goosesurvivor.items.SpeedBoostEffect;
import com.eng1c4t3.goosesurvivor.systems.SpeedBoostSystem;

/**
 * A temporary speed boost on an entity with a {@link VelocityComponent}.
 *
 * <p>While present, the entity's {@link VelocityComponent#speedMultiplier} is
 * {@link #multiplier}. The base {@link VelocityComponent#maxSpeed} is never touched, so permanent
 * upgrades gained during the boost are kept. When {@link #remainingTime} runs out the
 * {@link SpeedBoostSystem} resets the multiplier to 1 and removes this component.
 *
 * @see SpeedBoostEffect
 */
public class SpeedBoostComponent implements Component {
  public static final ComponentMapper<SpeedBoostComponent> MAPPER =
      ComponentMapper.getFor(SpeedBoostComponent.class);

  /** Factor the base speed is multiplied by while boosted. */
  public float multiplier;
  /** Seconds until the boost ends. */
  public float remainingTime;

  /**
   * Constructs a new SpeedBoostComponent.
   *
   * @param multiplier Factor the base speed is multiplied by while boosted.
   * @param remainingTime Seconds until the boost ends.
   */
  public SpeedBoostComponent(float multiplier, float remainingTime) {
    this.multiplier = multiplier;
    this.remainingTime = remainingTime;
  }
}
