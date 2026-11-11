package com.eng1c4t3.goosesurvivor.systems;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.eng1c4t3.goosesurvivor.SessionStats;
import com.eng1c4t3.goosesurvivor.components.HiddenEventComponent;
import com.eng1c4t3.goosesurvivor.components.HiddenEventComponent.EventState;
import com.eng1c4t3.goosesurvivor.components.HiddenEventComponent.EventType;
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
}
