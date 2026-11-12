package com.eng1c4t3.goosesurvivor.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.eng1c4t3.goosesurvivor.SessionStats;
import com.eng1c4t3.goosesurvivor.components.HealthComponent;
import com.eng1c4t3.goosesurvivor.components.HiddenEventComponent;
import com.eng1c4t3.goosesurvivor.components.HiddenEventComponent.EventState;
import com.eng1c4t3.goosesurvivor.components.HiddenEventComponent.EventType;
import com.eng1c4t3.goosesurvivor.components.InteractableComponent;
import com.eng1c4t3.goosesurvivor.components.InventoryComponent;
import com.eng1c4t3.goosesurvivor.components.PlayerComponent;
import com.eng1c4t3.goosesurvivor.components.TriggerEventComponent;
import com.eng1c4t3.goosesurvivor.components.VelocityComponent;
import com.eng1c4t3.goosesurvivor.items.ItemType;
import java.util.Objects;
import java.util.function.Function;

/**
 * Manages hidden gameplay events and records their progress.
 */
public class HiddenEventSystem extends EntitySystem {
  private final SessionStats stats;
  private final Function<Entity, Entity> bossFactory;
  private ImmutableArray<Entity> triggerEvents;
  private ImmutableArray<Entity> hiddenEvents;
  private ImmutableArray<Entity> players;

  /** Permanent speed reward multiplier for completing the Long Boi event. */
  public static final float LONG_BOI_SPEED_REWARD = 1.1f;

  private final EntityListener bossRemovalListener = new EntityListener() {
    @Override
    public void entityAdded(Entity entity) {
      // Adding an entity does not complete an event.
    }

    @Override
    public void entityRemoved(Entity entity) {
      completeEventForBoss(entity);
    }
  };

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
   * Tracks interaction events and registers the boss removal listener.
   *
   * @param engine The engine this system belongs to.
   */
  @Override
  public void addedToEngine(Engine engine) {
    triggerEvents = engine.getEntitiesFor(
        Family.all(TriggerEventComponent.class).get());
    hiddenEvents = engine.getEntitiesFor(
        Family.all(HiddenEventComponent.class).get());
    players = engine.getEntitiesFor(
        Family.all(PlayerComponent.class, VelocityComponent.class).get());
    engine.addEntityListener(bossRemovalListener);
  }

  /**
   * Unregisters the boss removal listener.
   *
   * @param engine The engine this system is leaving.
   */
  @Override
  public void removedFromEngine(Engine engine) {
    engine.removeEntityListener(bossRemovalListener);
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
      HiddenEventComponent event = HiddenEventComponent.MAPPER.get(target);
      if (event == null) {
        continue;
      }

      switch (event.type) {
        case LONG_BOI_STATUE:
          triggerStatue(target);
          break;
        case MUSIC_STUDENT:
          interactWithMusicStudent(target, trigger.sourcePlayer);
          break;
        default:
          break;
      }
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

  /**
   * Applies the permanent speed reward to the current player.
   */
  private void grantSpeedReward() {
    for (Entity player : players) {
      VelocityComponent velocity = VelocityComponent.MAPPER.get(player);
      velocity.maxSpeed *= LONG_BOI_SPEED_REWARD;
    }
  }

  /**
   * Completes the active statue event associated with a defeated boss.
   *
   * @param boss The entity removed from the engine.
   */
  private void completeEventForBoss(Entity boss) {
    HealthComponent health = HealthComponent.MAPPER.get(boss);
    if (health == null || !health.isDead()) {
      return;
    }

    for (Entity statue : hiddenEvents) {
      HiddenEventComponent event = HiddenEventComponent.MAPPER.get(statue);
      if (event.type != EventType.LONG_BOI_STATUE
          || event.state != EventState.ACTIVE
          || event.eventBoss != boss) {
        continue;
      }

      event.state = EventState.COMPLETED;
      event.eventBoss = null;
      statue.remove(InteractableComponent.class);
      stats.recordEventCompleted();
      grantSpeedReward();
    }
  }

  /**
   * Gives the player a trumpet and completes the music student event.
   *
   * @param student The music student being interacted with.
   * @param player The player receiving the trumpet.
   * @return True if the trumpet was received and the event completed.
   */
  public boolean interactWithMusicStudent(Entity student, Entity player) {
    HiddenEventComponent event = HiddenEventComponent.MAPPER.get(student);
    if (event == null || event.type != EventType.MUSIC_STUDENT
        || event.state != EventState.AVAILABLE) {
      return false;
    }

    if (player == null || player.isScheduledForRemoval()) {
      return false;
    }

    InventoryComponent inventory = InventoryComponent.MAPPER.get(player);
    if (inventory == null || inventory.addItem(ItemType.TRUMPET) < 0) {
      return false;
    }

    event.state = EventState.COMPLETED;
    student.remove(InteractableComponent.class);
    stats.recordEventTriggered();
    stats.recordEventCompleted();
    return true;
  }
}
