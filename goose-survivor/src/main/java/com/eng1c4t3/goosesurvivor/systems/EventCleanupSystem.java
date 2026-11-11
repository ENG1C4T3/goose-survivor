package com.eng1c4t3.goosesurvivor.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.eng1c4t3.goosesurvivor.components.TriggerEventComponent;
import com.eng1c4t3.goosesurvivor.components.UseItemEventComponent;

/**
 * Destroys all event entities ({@link TriggerEventComponent} and {@link UseItemEventComponent})
 * at the end of the frame, so each event is handled once.
 *
 * <p>This system has the highest possible priority value, so the engine always runs it after
 * every other system regardless of the order systems were added in.
 *
 * @see TriggerEventComponent
 * @see UseItemEventComponent
 */
public class EventCleanupSystem extends EntitySystem {

  private ImmutableArray<Entity> events;

  /**
   * Constructs an EventCleanupSystem that runs last in the frame.
   */
  public EventCleanupSystem() {
    super(Integer.MAX_VALUE);
  }

  /**
   * Caches the event entities.
   *
   * @param engine The {@link Engine} this system was added to.
   */
  @Override
  public void addedToEngine(Engine engine) {
    events = engine.getEntitiesFor(
        Family.one(TriggerEventComponent.class, UseItemEventComponent.class).get());
  }

  /**
   * Removes every event entity from the engine.
   *
   * @param deltaTime The time passed since the last frame.
   */
  @Override
  public void update(float deltaTime) {
    // Iterate backwards: outside of an engine update, removal happens immediately and shrinks
    // the array.
    for (int i = events.size() - 1; i >= 0; --i) {
      getEngine().removeEntity(events.get(i));
    }
  }
}
