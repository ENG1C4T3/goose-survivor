package com.eng1c4t3.goosesurvivor.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.eng1c4t3.goosesurvivor.systems.EventCleanupSystem;
import com.eng1c4t3.goosesurvivor.systems.InputSystem;
import com.eng1c4t3.goosesurvivor.systems.InteractionSystem;

/**
 * Describes a single trigger event, using the event-as-entity pattern.
 *
 * <p>Rather than calling listeners directly, a system that wants to raise an event creates a new
 * entity holding only this component. The event then flows through the frame:
 * <ol>
 *   <li>{@link InputSystem} detects a keypress in range and creates the event entity.</li>
 *   <li>{@link InteractionSystem} reads the event entity and executes the game logic.</li>
 *   <li>{@link EventCleanupSystem} destroys the event entity at the end of the frame.</li>
 * </ol>
 */
public class TriggerEventComponent implements Component {
  public static final ComponentMapper<TriggerEventComponent> MAPPER =
      ComponentMapper.getFor(TriggerEventComponent.class);

  /**
   * The kind of action that raised the event.
   */
  public enum ActionType {
    INTERACT,
  }

  /** The player entity that raised the event. */
  public Entity sourcePlayer;
  /** The interactable entity the event is aimed at. */
  public Entity targetInteractable;
  /** The action the player performed. */
  public ActionType action;

  /**
   * Constructs a new TriggerEventComponent.
   *
   * @param sourcePlayer The player entity that raised the event.
   * @param targetInteractable The interactable entity the event is aimed at.
   * @param action The action the player performed.
   */
  public TriggerEventComponent(Entity sourcePlayer, Entity targetInteractable,
                               ActionType action) {
    this.sourcePlayer = sourcePlayer;
    this.targetInteractable = targetInteractable;
    this.action = action;
  }
}
