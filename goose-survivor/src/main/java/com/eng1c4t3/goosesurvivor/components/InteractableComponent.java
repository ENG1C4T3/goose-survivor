package com.eng1c4t3.goosesurvivor.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.eng1c4t3.goosesurvivor.systems.InputSystem;
import com.eng1c4t3.goosesurvivor.systems.InteractionSystem;

/**
 * Marks an entity as something the player can interact with.
 *
 * <p>When the player presses the interact key while within {@link #range} of this entity,
 * the {@link InputSystem} creates a trigger event entity which the {@link InteractionSystem}
 * then processes. What the interaction does is decided by the effect components on the entity
 * (e.g. {@link HealOnInteractComponent}).
 *
 * @see TriggerEventComponent
 */
public class InteractableComponent implements Component {
  public static final ComponentMapper<InteractableComponent> MAPPER =
      ComponentMapper.getFor(InteractableComponent.class);

  /** Maximum distance between the player's centre and this entity's centre to interact. */
  public float range;
  /** Text shown to the player when they are in range, e.g. "Press E to open". */
  public String promptText;
  /** Whether the entity is removed from the game after being interacted with once. */
  public boolean singleUse;

  /**
   * Constructs a new reusable InteractableComponent.
   *
   * @param range The maximum distance from which the player can interact with the entity.
   * @param promptText The text prompt shown to the player when in range.
   */
  public InteractableComponent(float range, String promptText) {
    this(range, promptText, false);
  }

  /**
   * Constructs a new InteractableComponent.
   *
   * @param range The maximum distance from which the player can interact with the entity.
   * @param promptText The text prompt shown to the player when in range.
   * @param singleUse Whether the entity is removed after being interacted with once.
   */
  public InteractableComponent(float range, String promptText, boolean singleUse) {
    this.range = range;
    this.promptText = promptText;
    this.singleUse = singleUse;
  }
}
