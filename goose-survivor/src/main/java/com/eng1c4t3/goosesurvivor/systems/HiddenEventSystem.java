package com.eng1c4t3.goosesurvivor.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.eng1c4t3.goosesurvivor.SessionStats;
import com.eng1c4t3.goosesurvivor.components.HiddenEventComponent;
import com.eng1c4t3.goosesurvivor.components.HiddenEventComponent.EventState;
import com.eng1c4t3.goosesurvivor.components.HiddenEventComponent.EventType;
import com.eng1c4t3.goosesurvivor.components.InteractableComponent;
import com.eng1c4t3.goosesurvivor.components.TriggerEventComponent;
import java.util.Objects;
import java.util.function.Function;

/**
 * Manages hidden gameplay events and records their progress.
 */
public class HiddenEventSystem extends EntitySystem {
  private final SessionStats stats;
  private final Function<Entity, Entity> bossFactory;
  private ImmutableArray<Entity> triggerEvents;

  /**
   * Creates a hidden event system.
   *
   * @param stats       The statistic for the current session.
   * @param bossFactory bossFactory Creates a new boss for the supplied statue
   *                    entity.
   * @throws NullPointerException if either argument is null.
   */
  public HiddenEventSystem(SessionStats stats, Function<Entity, Entity> bossFactory) {
    this.stats = Objects.requireNonNull(stats, "Session statistic must not be null");
    this.bossFactory = Objects.requireNonNull(bossFactory,
        "bossFactory must not be null");
  }

  /**
   * (non-Javadoc)
   * Tracks interaction notification in this engine.
   *
   * @param engine The engine this system belongs to.
   */
  @Override
  public void addedToEngine(Engine engine) {
    triggerEvents = engine.getEntitiesFor(
        Family.all(TriggerEventComponent.class).get());
  }

  @Override
  public void update(float deltaTime) {
    for (Entity notification : triggerEvents) {
      final TriggerEventComponent trigger = TriggerEventComponent.MAPPER.get(notification);

      if (trigger.action != TriggerEventComponent.ActionType.INTERACT) {
        continue;
      }

      final Entity target = trigger.targetInteractable;
      if (target == null || target.isScheduledForRemoval()
          || !InteractableComponent.MAPPER.has(target)) {
        continue;
      }
      triggerStatue(target);
    }
  }

  /**
   * Starts an avalable statue event and adds its boss to the engine.
   *
   * @param statue The statue entity belonging to this engine.
   * @return Trun if the event started, otherwise false.
   */
  public boolean triggerStatue(Entity statue) {
    final HiddenEventComponent event = HiddenEventComponent.MAPPER.get(statue);
    if (event == null || event.type != EventType.LONG_BOI_STATUE
        || event.state != EventState.AVAILABLE) {
      return false;
    }

    final Entity boss = bossFactory.apply(statue);
    event.eventBoss = boss;
    event.state = EventState.ACTIVE;
    getEngine().addEntity(boss);
    stats.recordEventTriggered();
    return true;
  }
}
