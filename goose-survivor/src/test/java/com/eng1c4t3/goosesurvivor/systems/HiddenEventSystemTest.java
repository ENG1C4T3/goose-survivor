package com.eng1c4t3.goosesurvivor.systems;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
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
import com.eng1c4t3.goosesurvivor.items.SpeedBoostEffect;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class HiddenEventSystemTest {

  @Test
  void statueStartsOnceAndCreatesOneBoss() {
    final Engine engine = new Engine();
    final SessionStats stats = new SessionStats();
    final AtomicInteger factoryCalls = new AtomicInteger();
    final Entity boss = new Entity();

    final HiddenEventSystem system = new HiddenEventSystem(stats, statue -> {
      factoryCalls.incrementAndGet();
      return boss;
    });
    engine.addSystem(system);

    final Entity statue = new Entity();
    final HiddenEventComponent event = new HiddenEventComponent(EventType.LONG_BOI_STATUE);
    statue.add(event);
    engine.addEntity(statue);

    assertTrue(system.triggerStatue(statue));
    assertEquals(EventState.ACTIVE, event.state);
    assertSame(boss, event.eventBoss);
    assertTrue(engine.getEntities().contains(boss, true));
    assertEquals(1, stats.getEventsTriggered());
    assertEquals(0, stats.getEventsCompleted());

    assertFalse(system.triggerStatue(statue));
    assertEquals(1, factoryCalls.get());
    assertEquals(1, stats.getEventsTriggered());
    assertEquals(2, engine.getEntities().size());
  }

  @Test
  void completedStatueCannotStartAgain() {
    final Engine engine = new Engine();
    final SessionStats stats = new SessionStats();
    final AtomicInteger factoryCalls = new AtomicInteger();
    final HiddenEventSystem system = new HiddenEventSystem(stats, statue -> {
      factoryCalls.incrementAndGet();
      return new Entity();
    });
    engine.addSystem(system);

    final Entity statue = new Entity();
    final HiddenEventComponent event = new HiddenEventComponent(EventType.LONG_BOI_STATUE);
    event.state = EventState.COMPLETED;
    statue.add(event);
    engine.addEntity(statue);

    assertFalse(system.triggerStatue(statue));
    assertEquals(EventState.COMPLETED, event.state);
    assertEquals(0, factoryCalls.get());
    assertEquals(0, stats.getEventsTriggered());
    assertEquals(1, engine.getEntities().size());
  }

  @Test
  void musicStudentDoesNotTriggerStatueLogic() {
    final Engine engine = new Engine();
    final SessionStats stats = new SessionStats();
    final AtomicInteger factoryCalls = new AtomicInteger();
    final HiddenEventSystem system = new HiddenEventSystem(stats, statue -> {
      factoryCalls.incrementAndGet();
      return new Entity();
    });
    engine.addSystem(system);

    final Entity student = new Entity();
    final HiddenEventComponent event = new HiddenEventComponent(EventType.MUSIC_STUDENT);
    student.add(event);
    engine.addEntity(student);

    assertFalse(system.triggerStatue(student));
    assertEquals(EventState.AVAILABLE, event.state);
    assertEquals(0, factoryCalls.get());
    assertEquals(0, stats.getEventsTriggered());
  }

  @Test
  void defeatedBossCompletesEventOnlyOnce() {
    Engine engine = new Engine();
    SessionStats stats = new SessionStats();
    Entity boss = new Entity();
    HealthComponent health = new HealthComponent(100f);
    boss.add(health);

    HiddenEventSystem system = new HiddenEventSystem(stats, statue -> boss);
    system.priority = 8;
    engine.addSystem(system);

    HealthSystem healthSystem = new HealthSystem();
    healthSystem.priority = 9;
    engine.addSystem(healthSystem);

    Entity statue = new Entity();
    HiddenEventComponent event = new HiddenEventComponent(EventType.LONG_BOI_STATUE);
    statue.add(event);
    statue.add(new InteractableComponent(64f, "Awaken Long Boi", false));
    engine.addEntity(statue);

    assertTrue(system.triggerStatue(statue));

    engine.update(0f);
    assertEquals(EventState.ACTIVE, event.state);
    assertEquals(0, stats.getEventsCompleted());

    health.takeDamage(100f);
    engine.update(0f);

    assertFalse(engine.getEntities().contains(boss, true));
    assertEquals(EventState.COMPLETED, event.state);
    assertNull(event.eventBoss);
    assertFalse(InteractableComponent.MAPPER.has(statue));
    assertEquals(1, stats.getEventsCompleted());

    engine.update(0f);
    assertEquals(1, stats.getEventsCompleted());
    assertFalse(system.triggerStatue(statue));
  }

  @Test
  void removingLivingBossDoesNotCompleteEvent() {
    Engine engine = new Engine();
    SessionStats stats = new SessionStats();
    Entity boss = new Entity();
    boss.add(new HealthComponent(100f));

    HiddenEventSystem system = new HiddenEventSystem(stats, statue -> boss);
    engine.addSystem(system);

    Entity statue = new Entity();
    HiddenEventComponent event = new HiddenEventComponent(EventType.LONG_BOI_STATUE);
    statue.add(event);
    engine.addEntity(statue);
    system.triggerStatue(statue);

    engine.removeEntity(boss);

    assertEquals(EventState.ACTIVE, event.state);
    assertSame(boss, event.eventBoss);
    assertEquals(0, stats.getEventsCompleted());
  }

  @Test
  void removingUnrelatedDefeatedEntityDoesNotCompleteEvent() {
    Engine engine = new Engine();
    SessionStats stats = new SessionStats();
    Entity boss = new Entity();
    boss.add(new HealthComponent(100f));

    HiddenEventSystem system = new HiddenEventSystem(stats, statue -> boss);
    engine.addSystem(system);

    Entity statue = new Entity();
    HiddenEventComponent event = new HiddenEventComponent(EventType.LONG_BOI_STATUE);
    statue.add(event);
    engine.addEntity(statue);
    system.triggerStatue(statue);

    Entity otherEntity = new Entity();
    HealthComponent otherHealth = new HealthComponent(50f);
    otherEntity.add(otherHealth);
    engine.addEntity(otherEntity);

    otherHealth.takeDamage(50f);
    engine.removeEntity(otherEntity);

    assertEquals(EventState.ACTIVE, event.state);
    assertSame(boss, event.eventBoss);
    assertEquals(0, stats.getEventsCompleted());
  }

  @Test
  void removingSystemUnregistersBossListener() {
    Engine engine = new Engine();
    SessionStats stats = new SessionStats();
    Entity boss = new Entity();
    HealthComponent health = new HealthComponent(100f);
    boss.add(health);

    HiddenEventSystem system = new HiddenEventSystem(stats, statue -> boss);
    engine.addSystem(system);

    Entity statue = new Entity();
    HiddenEventComponent event = new HiddenEventComponent(EventType.LONG_BOI_STATUE);
    statue.add(event);
    engine.addEntity(statue);
    system.triggerStatue(statue);

    engine.removeSystem(system);
    health.takeDamage(100f);
    engine.removeEntity(boss);

    assertEquals(EventState.ACTIVE, event.state);
    assertEquals(0, stats.getEventsCompleted());
  }

  @Test
  void permanentRewardSurvivesTemporaryBoostExpiry() {
    Engine engine = new Engine();
    final SessionStats stats = new SessionStats();

    Entity player = new Entity();
    VelocityComponent velocity = new VelocityComponent(100f);
    player.add(new PlayerComponent());
    player.add(velocity);
    engine.addEntity(player);

    Entity boss = new Entity();
    HealthComponent health = new HealthComponent(100f);
    boss.add(health);

    HiddenEventSystem system = new HiddenEventSystem(stats, statue -> boss);
    engine.addSystem(system);
    engine.addSystem(new SpeedBoostSystem());

    Entity statue = new Entity();
    statue.add(new HiddenEventComponent(EventType.LONG_BOI_STATUE));
    engine.addEntity(statue);
    system.triggerStatue(statue);

    new SpeedBoostEffect(1.5f, 10f).apply(player, engine);
    assertEquals(150f, velocity.effectiveMaxSpeed(), 0.001f);

    health.takeDamage(100f);
    engine.removeEntity(boss);

    float rewardedSpeed = 100f * HiddenEventSystem.LONG_BOI_SPEED_REWARD;
    assertEquals(rewardedSpeed, velocity.maxSpeed, 0.001f);
    assertEquals(rewardedSpeed * 1.5f, velocity.effectiveMaxSpeed(), 0.001f);

    engine.update(11f);

    assertEquals(1f, velocity.speedMultiplier, 0.001f);
    assertEquals(rewardedSpeed, velocity.effectiveMaxSpeed(), 0.001f);
    assertEquals(1, stats.getEventsCompleted());

    engine.update(1f);
    assertFalse(system.triggerStatue(statue));
    assertEquals(rewardedSpeed, velocity.maxSpeed, 0.001f);
  }

  @Test
  void interactionEventStartsBossAndAwardsCompletionReward() {
    Engine engine = new Engine();
    final SessionStats stats = new SessionStats();

    Entity player = new Entity();
    VelocityComponent velocity = new VelocityComponent(100f);
    player.add(new PlayerComponent());
    player.add(velocity);
    engine.addEntity(player);

    Entity boss = new Entity();
    HealthComponent health = new HealthComponent(100f);
    boss.add(health);

    InteractionSystem interactionSystem = new InteractionSystem();
    interactionSystem.priority = 1;
    engine.addSystem(interactionSystem);

    HiddenEventSystem hiddenSystem = new HiddenEventSystem(stats, statue -> boss);
    hiddenSystem.priority = 8;
    engine.addSystem(hiddenSystem);

    HealthSystem healthSystem = new HealthSystem();
    healthSystem.priority = 9;
    engine.addSystem(healthSystem);
    engine.addSystem(new EventCleanupSystem());

    Entity statue = new Entity();
    HiddenEventComponent event = new HiddenEventComponent(EventType.LONG_BOI_STATUE);
    statue.add(event);
    statue.add(new InteractableComponent(64f, "Awaken Long Boi", false));
    engine.addEntity(statue);

    Entity notification = new Entity();
    notification.add(new TriggerEventComponent(
        player, statue, TriggerEventComponent.ActionType.INTERACT));
    engine.addEntity(notification);

    engine.update(0f);

    assertEquals(EventState.ACTIVE, event.state);
    assertSame(boss, event.eventBoss);
    assertTrue(engine.getEntities().contains(boss, true));
    assertFalse(engine.getEntities().contains(notification, true));
    assertEquals(1, stats.getEventsTriggered());

    health.takeDamage(100f);
    engine.update(0f);

    assertEquals(EventState.COMPLETED, event.state);
    assertEquals(1, stats.getEventsCompleted());
    assertEquals(
        100f * HiddenEventSystem.LONG_BOI_SPEED_REWARD,
        velocity.maxSpeed,
        0.001f);
  }

  @Test
  void musicStudentGivesTrumpetOnceThroughInteraction() {
    final Engine engine = new Engine();
    final SessionStats stats = new SessionStats();

    InteractionSystem interactionSystem = new InteractionSystem();
    interactionSystem.priority = 1;
    engine.addSystem(interactionSystem);

    HiddenEventSystem system = new HiddenEventSystem(stats, statue -> {
      throw new AssertionError("Music student must not create a boss");
    });
    system.priority = 8;
    engine.addSystem(system);
    engine.addSystem(new EventCleanupSystem());

    final Entity player = new Entity();
    final InventoryComponent inventory = new InventoryComponent(2);
    player.add(new PlayerComponent());
    player.add(inventory);
    engine.addEntity(player);

    final Entity student = new Entity();
    final HiddenEventComponent event =
        new HiddenEventComponent(EventType.MUSIC_STUDENT);
    student.add(event);
    student.add(new InteractableComponent(64f, "Talk to music student", false));
    engine.addEntity(student);

    Entity notification = new Entity();
    notification.add(new TriggerEventComponent(
        player, student, TriggerEventComponent.ActionType.INTERACT));
    engine.addEntity(notification);

    engine.update(0f);

    assertEquals(ItemType.TRUMPET, inventory.getSlot(0).item);
    assertNull(inventory.getSlot(1));
    assertEquals(EventState.COMPLETED, event.state);
    assertEquals(1, stats.getEventsTriggered());
    assertEquals(1, stats.getEventsCompleted());
    assertEquals(0, stats.getItemsUsed());
    assertTrue(engine.getEntities().contains(student, true));
    assertFalse(InteractableComponent.MAPPER.has(student));
    assertFalse(engine.getEntities().contains(notification, true));

    assertFalse(system.interactWithMusicStudent(student, player));
    assertNull(inventory.getSlot(1));
    assertEquals(1, stats.getEventsTriggered());
    assertEquals(1, stats.getEventsCompleted());
  }

  @Test
  void musicStudentCanBeRetriedAfterInventorySpaceIsFreed() {
    final Engine engine = new Engine();
    final SessionStats stats = new SessionStats();
    HiddenEventSystem system = new HiddenEventSystem(stats, statue -> {
      throw new AssertionError("Music student must not create a boss");
    });
    engine.addSystem(system);

    final Entity player = new Entity();
    final InventoryComponent inventory = new InventoryComponent(1);
    inventory.addItem(ItemType.COFFEE);
    player.add(new PlayerComponent());
    player.add(inventory);
    engine.addEntity(player);

    Entity student = new Entity();
    HiddenEventComponent event = new HiddenEventComponent(EventType.MUSIC_STUDENT);
    student.add(event);
    student.add(new InteractableComponent(64f, "Talk to music student", false));
    engine.addEntity(student);

    assertFalse(system.interactWithMusicStudent(student, player));
    assertEquals(ItemType.COFFEE, inventory.getSlot(0).item);
    assertEquals(EventState.AVAILABLE, event.state);
    assertTrue(InteractableComponent.MAPPER.has(student));
    assertEquals(0, stats.getEventsTriggered());
    assertEquals(0, stats.getEventsCompleted());

    inventory.clearSlot(0);

    assertTrue(system.interactWithMusicStudent(student, player));
    assertEquals(ItemType.TRUMPET, inventory.getSlot(0).item);
    assertEquals(EventState.COMPLETED, event.state);
    assertFalse(InteractableComponent.MAPPER.has(student));
    assertEquals(1, stats.getEventsTriggered());
    assertEquals(1, stats.getEventsCompleted());
  }
}
