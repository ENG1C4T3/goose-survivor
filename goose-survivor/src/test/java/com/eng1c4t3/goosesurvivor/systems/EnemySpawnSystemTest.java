package com.eng1c4t3.goosesurvivor.systems;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Rectangle;
import com.eng1c4t3.goosesurvivor.components.EnemyComponent;
import com.eng1c4t3.goosesurvivor.components.PlayerComponent;
import com.eng1c4t3.goosesurvivor.components.TransformComponent;
import com.eng1c4t3.goosesurvivor.entities.EntityFactory;
import com.eng1c4t3.goosesurvivor.entities.EntityFactory.EnemyType;
import com.eng1c4t3.goosesurvivor.systems.EnemySpawnSystem.SpawnRule;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

class EnemySpawnSystemTest {
  private static final float WIDTH = 1280f;
  private static final float HEIGHT = 720f;

  private static Engine engineWith(EnemySpawnSystem system) {
    Engine engine = new Engine();
    Entity player = new Entity();
    player.add(new PlayerComponent());
    player.add(new TransformComponent(WIDTH / 2f, HEIGHT / 2f, 36f, 62f));
    engine.addEntity(player);
    system.setRandom(new Random(1));
    engine.addSystem(system);
    return engine;
  }

  private static int count(Engine engine, EnemyType type) {
    int n = 0;
    for (Entity e : engine.getEntitiesFor(Family.all(EnemyComponent.class).get())) {
      if (EnemyComponent.MAPPER.get(e).type == type) {
        n++;
      }
    }
    return n;
  }

  private static void run(Engine engine, float seconds) {
    for (float t = 0f; t < seconds; t += 0.5f) {
      engine.update(0.5f);
    }
  }

  @Test
  void onlyBasicGeeseSpawnInFirstTwoMinutes() {
    Engine engine = engineWith(new EnemySpawnSystem(WIDTH, HEIGHT, name -> null));
    run(engine, 119f);
    assertTrue(count(engine, EnemyType.GOOSE) > 0);
    assertEquals(0, count(engine, EnemyType.FAST_GOOSE));
    assertEquals(0, count(engine, EnemyType.TANK_GOOSE));
  }

  @Test
  void fastGeeseFromTwoMinutesAndTankGeeseFromThree() {
    Engine engine = engineWith(new EnemySpawnSystem(WIDTH, HEIGHT, name -> null));
    run(engine, 121f);
    final int basicAtTwoMinutes = count(engine, EnemyType.GOOSE);
    assertTrue(count(engine, EnemyType.FAST_GOOSE) > 0);
    assertEquals(0, count(engine, EnemyType.TANK_GOOSE));

    run(engine, 60f);
    assertTrue(count(engine, EnemyType.TANK_GOOSE) > 0);
    assertEquals(basicAtTwoMinutes, count(engine, EnemyType.GOOSE));
  }

  @Test
  void spawnsOnceImmediatelyThenEveryInterval() {
    EnemySpawnSystem system = new EnemySpawnSystem(
        List.of(new SpawnRule(EnemyType.GOOSE, 0f, 100f, 10f)), WIDTH, HEIGHT, name -> null);
    Engine engine = engineWith(system);
    engine.update(0.1f);
    assertEquals(1, count(engine, EnemyType.GOOSE));
    engine.update(9.8f);
    assertEquals(1, count(engine, EnemyType.GOOSE));
    engine.update(0.2f);
    assertEquals(2, count(engine, EnemyType.GOOSE));
  }

  @Test
  void spawnsAtEdgeAwayFromPlayer() {
    Engine engine = engineWith(new EnemySpawnSystem(WIDTH, HEIGHT, name -> null));
    run(engine, 60f);
    for (Entity e : engine.getEntitiesFor(Family.all(EnemyComponent.class).get())) {
      Rectangle r = TransformComponent.MAPPER.get(e).position;
      boolean onEdge = r.x <= 0.01f || r.y <= 0.01f
          || r.x + r.width >= WIDTH - 0.01f || r.y + r.height >= HEIGHT - 0.01f;
      assertTrue(onEdge, "spawned at " + r);
      float dx = r.x + r.width / 2f - (WIDTH / 2f + 18f);
      float dy = r.y + r.height / 2f - (HEIGHT / 2f + 31f);
      assertTrue(Math.sqrt(dx * dx + dy * dy) >= EnemySpawnSystem.MIN_PLAYER_DISTANCE);
    }
  }

  @Test
  void expMultipliersMatchDesign() {
    int basic = EntityFactory.statsFor(EnemyType.GOOSE).score();
    assertEquals(basic * 2, EntityFactory.statsFor(EnemyType.FAST_GOOSE).score());
    assertEquals(basic * 4, EntityFactory.statsFor(EnemyType.TANK_GOOSE).score());
  }

  @Test
  void rejectsInvalidRules() {
    assertThrows(IllegalArgumentException.class,
        () -> new SpawnRule(EnemyType.GOOSE, 10f, 5f, 1f));
    assertThrows(IllegalArgumentException.class,
        () -> new SpawnRule(EnemyType.GOOSE, 0f, 5f, 0f));
  }
}
