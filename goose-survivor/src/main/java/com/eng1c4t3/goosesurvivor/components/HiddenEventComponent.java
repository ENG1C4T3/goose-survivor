package com.eng1c4t3.goosesurvivor.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

/**
 * Stores the persistent progress of a hidden gameplay event.
 */
public class HiddenEventComponent implements Component {

  public static final ComponentMapper<HiddenEventComponent> MAPPER = ComponentMapper
      .getFor(HiddenEventComponent.class);

  /**
   * Identifies the gameplay event in event type.
   */
  public enum EventType {
    LONG_BOI_STATUE,
    MUSIC_STUDENT,
    /** Spawns a random item or enemy from a {@link SpawnerComponent} when used. */
    SPAWNER,
    /** A fox that runs away until petted, then follows the player and drops a launcher. */
    FOX,
    /** A fountain that only records a visit in the session stats. */
    FOUNTAIN,
    /** A locked door that opens, using up a key, when the player has one. */
    DOOR,
  }

  /**
   * Describe the event's progress in event state.
   */
  public enum EventState {
    AVAILABLE,
    ACTIVE,
    COMPLETED
  }

  public final EventType type;

  public EventState state = EventState.AVAILABLE;

  /**
   * If no boss is associated, it will be null.
   */
  public Entity eventBoss;

  /**
   * Creates an event that not triggered yet.
   *
   * @param type the type of event.
   * @throws IllegalArgumentException if the event type is null.
   */
  public HiddenEventComponent(EventType type) {
    if (type == null) {
      throw new IllegalArgumentException(
          "Event type must not be null");
    }
    this.type = type;
  }

}
