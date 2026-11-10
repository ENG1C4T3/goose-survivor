package com.eng1c4t3.goosesurvivor.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.eng1c4t3.goosesurvivor.systems.InteractionSystem;

/**
 * Interaction effect: heals the player who interacts with this entity.
 *
 * <p>Only has an effect on an entity that also has an {@link InteractableComponent}.
 *
 * @see InteractionSystem
 */
public class HealOnInteractComponent implements Component {
  public static final ComponentMapper<HealOnInteractComponent> MAPPER =
      ComponentMapper.getFor(HealOnInteractComponent.class);

  /** Amount of health restored to the player. */
  public float amount;

  /**
   * Constructs a new HealOnInteractComponent.
   *
   * @param amount The amount of health restored to the player.
   */
  public HealOnInteractComponent(float amount) {
    this.amount = amount;
  }
}
