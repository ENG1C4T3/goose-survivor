package com.eng1c4t3.goosesurvivor.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.eng1c4t3.goosesurvivor.components.SpeedBoostComponent;
import com.eng1c4t3.goosesurvivor.components.VelocityComponent;

/**
 * Counts down temporary speed boosts and resets the speed multiplier when one ends.
 *
 * <p>Only {@link VelocityComponent#speedMultiplier} is reset; the base speed is left alone, so
 * permanent upgrades gained while boosted are kept.
 *
 * @see SpeedBoostComponent
 * @see com.eng1c4t3.goosesurvivor.items.SpeedBoostEffect
 */
public class SpeedBoostSystem extends IteratingSystem {

  /**
   * Constructs a SpeedBoostSystem that processes boosted entities.
   */
  public SpeedBoostSystem() {
    super(Family.all(SpeedBoostComponent.class, VelocityComponent.class).get());
  }

  /**
   * Ticks a boost and ends it once its time runs out.
   *
   * @param entity The boosted entity.
   * @param deltaTime The time passed since the last frame.
   */
  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    SpeedBoostComponent boost = SpeedBoostComponent.MAPPER.get(entity);
    boost.remainingTime -= deltaTime;
    if (boost.remainingTime <= 0) {
      VelocityComponent.MAPPER.get(entity).speedMultiplier = 1f;
      entity.remove(SpeedBoostComponent.class);
    }
  }
}
