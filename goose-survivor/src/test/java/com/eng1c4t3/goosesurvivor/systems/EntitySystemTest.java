package com.eng1c4t3.goosesurvivor.systems;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.eng1c4t3.goosesurvivor.Engine;
import org.junit.jupiter.api.Test;

class EntitySystemTest {

  private static class TestEntitySystem extends EntitySystem {
    private float lastDeltaTime = -1f;

    @Override
    public void update(float deltaTime) {
      this.lastDeltaTime = deltaTime;
    }

    public Engine getEngine() {
      return this.engine;
    }
  }

  @Test
  void testSetEngineAndEngineAccess() {
    TestEntitySystem system = new TestEntitySystem();
    assertNull(system.getEngine());

    Engine engine = new Engine();
    system.setEngine(engine);
    assertEquals(engine, system.getEngine());
  }

  @Test
  void testUpdateReceivesDeltaTime() {
    TestEntitySystem system = new TestEntitySystem();
    system.update(0.016f);
    assertEquals(0.016f, system.lastDeltaTime);
  }

  @Test
  void testAddSystemToEngineSetsEngine() {
    Engine engine = new Engine();
    TestEntitySystem system = new TestEntitySystem();
    engine.addSystem(system);

    assertEquals(engine, system.getEngine());
  }
}
