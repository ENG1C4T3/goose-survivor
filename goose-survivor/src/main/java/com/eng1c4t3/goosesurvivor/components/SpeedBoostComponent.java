package com.eng1c4t3.goosesurvivor.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.eng1c4t3.goosesurvivor.items.SpeedBoostEffect;
import com.eng1c4t3.goosesurvivor.systems.SpeedBoostSystem;

/**
 * A temporary speed boost on an entity with a {@link VelocityComponent}.
 *
 * <p>While present, the entity's {@link VelocityComponent#maxSpeed} is {@link #baseSpeed} times
 * {@link #multiplier}. When {@link #remainingTime} runs out the {@link SpeedBoostSystem}
 * restores the base speed and removes this component.
 *
 * @see SpeedBoostEffect
 */
public class SpeedBoostComponent implements Component {
  public static final ComponentMapper<SpeedBoostComponent> MAPPER =
      ComponentMapper.getFor(SpeedBoostComponent.class);

  /** The entity's max speed before the boost, restored when it ends. */
  public float baseSpeed;
  /** Factor the base speed is multiplied by while boosted. */
  public float multiplier;
  /** Seconds until the boost ends. */
  public float remainingTime;

  /**
   * Constructs a new SpeedBoostComponent.
   *
   * @param baseSpeed The entity's max speed before the boost.
   * @param multiplier Factor the base speed is multiplied by while boosted.
   * @param remainingTime Seconds until the boost ends.
   */
  public SpeedBoostComponent(float baseSpeed, float multiplier, float remainingTime) {
    this.baseSpeed = baseSpeed;
    this.multiplier = multiplier;
    this.remainingTime = remainingTime;
  }
}
