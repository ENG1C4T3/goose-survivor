package com.eng1c4t3.goosesurvivor.systems;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.eng1c4t3.goosesurvivor.components.EnemyComponent;
import com.eng1c4t3.goosesurvivor.components.PlayerComponent;
import com.eng1c4t3.goosesurvivor.components.TransformComponent;
import com.eng1c4t3.goosesurvivor.components.VelocityComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EnemySystemTest {

  private Engine engine;
  private EnemySystem enemySystem;

  @BeforeEach
  void setUp() {
    engine = new Engine();
    enemySystem = new EnemySystem();
    // System is added before entities, mirroring GooseSurvivor.create()
    engine.addSystem(enemySystem);
  }

  @Test
  void testEnemyFollowsPlayerWhenEntitiesAddedAfterSystem() {
    Entity player = new Entity();
    player.add(new PlayerComponent());
    player.add(new TransformComponent(100f, 0f, 32f, 32f));
    engine.addEntity(player);

    Entity enemy = new Entity();
    enemy.add(new EnemyComponent());
    enemy.add(new TransformComponent(0f, 0f, 32f, 32f));
    VelocityComponent velocity = new VelocityComponent(50f);
    enemy.add(velocity);
    engine.addEntity(enemy);

    enemySystem.update(0.1f);

    assertEquals(50f, velocity.velocity.x, 1e-5f);
    assertEquals(0f, velocity.velocity.y, 1e-5f);
  }

  @Test
  void testEnemyDiagonalTrackingScalesToMaxSpeed() {
    Entity player = new Entity();
    player.add(new PlayerComponent());
    player.add(new TransformComponent(30f, 40f, 32f, 32f));
    engine.addEntity(player);

    Entity enemy = new Entity();
    enemy.add(new EnemyComponent());
    enemy.add(new TransformComponent(0f, 0f, 32f, 32f));
    VelocityComponent velocity = new VelocityComponent(100f);
    enemy.add(velocity);
    engine.addEntity(enemy);

    enemySystem.update(0.1f);

    // 3-4-5 triangle normalized: dirX = 3/5 = 0.6 * 100 = 60, dirY = 4/5 = 0.8 * 100 = 80
    assertEquals(60f, velocity.velocity.x, 1e-5f);
    assertEquals(80f, velocity.velocity.y, 1e-5f);
  }

  @Test
  void testEnemyDoesNotMoveWhenNoPlayer() {
    Entity enemy = new Entity();
    enemy.add(new EnemyComponent());
    enemy.add(new TransformComponent(0f, 0f, 32f, 32f));
    VelocityComponent velocity = new VelocityComponent(50f);
    enemy.add(velocity);
    engine.addEntity(enemy);

    enemySystem.update(0.1f);

    assertEquals(0f, velocity.velocity.x, 1e-5f);
    assertEquals(0f, velocity.velocity.y, 1e-5f);
  }

  @Test
  void testKnockedBackEnemyKeepsVelocityUntilTimerExpires() {
    Entity player = new Entity();
    player.add(new PlayerComponent());
    player.add(new TransformComponent(100f, 0f, 32f, 32f));
    engine.addEntity(player);

    Entity enemy = new Entity();
    EnemyComponent enemyComponent = new EnemyComponent();
    enemyComponent.knockbackTimer = 0.15f;
    enemy.add(enemyComponent);
    enemy.add(new TransformComponent(0f, 0f, 32f, 32f));
    VelocityComponent velocity = new VelocityComponent(50f);
    velocity.velocity.set(-400f, 0f); // Knockback velocity, away from the player
    enemy.add(velocity);
    engine.addEntity(enemy);

    enemySystem.update(0.1f);
    assertEquals(-400f, velocity.velocity.x, 1e-5f);

    enemySystem.update(0.1f); // Timer runs out
    enemySystem.update(0.1f); // Back to chasing the player
    assertEquals(50f, velocity.velocity.x, 1e-5f);
  }
}
