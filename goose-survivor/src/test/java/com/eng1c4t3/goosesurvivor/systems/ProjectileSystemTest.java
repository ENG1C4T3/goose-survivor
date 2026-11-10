package com.eng1c4t3.goosesurvivor.systems;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.eng1c4t3.goosesurvivor.components.CollisionComponent;
import com.eng1c4t3.goosesurvivor.components.CollisionComponent.Type;
import com.eng1c4t3.goosesurvivor.components.EnemyComponent;
import com.eng1c4t3.goosesurvivor.components.HealthComponent;
import com.eng1c4t3.goosesurvivor.components.PlayerComponent;
import com.eng1c4t3.goosesurvivor.components.ProjectileComponent;
import com.eng1c4t3.goosesurvivor.components.TransformComponent;
import com.eng1c4t3.goosesurvivor.components.VelocityComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProjectileSystemTest {

  private static final float DAMAGE = 15f;

  private Engine engine;

  @BeforeEach
  void setUp() {
    engine = new Engine();
    CollisionSystem collisionSystem = new CollisionSystem();
    ProjectileSystem projectileSystem = new ProjectileSystem();
    engine.addSystem(collisionSystem);
    engine.addSystem(projectileSystem);
    collisionSystem.addCollisionListener(projectileSystem);
  }

  private Entity projectile(Entity owner, float x, float y, float speed) {
    Entity projectile = new Entity();
    projectile.add(new TransformComponent(x, y, 16f, 16f));
    VelocityComponent velocity = new VelocityComponent(speed);
    velocity.velocity.set(speed, 0f);
    projectile.add(velocity);
    projectile.add(new ProjectileComponent(owner, DAMAGE, 100f, 50f));
    projectile.add(new CollisionComponent(Type.TRIGGER));
    engine.addEntity(projectile);
    return projectile;
  }

  private Entity enemy(float x, float y) {
    Entity enemy = new Entity();
    enemy.add(new EnemyComponent());
    enemy.add(new TransformComponent(x, y, 32f, 32f));
    enemy.add(new VelocityComponent(0f));
    enemy.add(new HealthComponent(100f));
    enemy.add(new CollisionComponent(Type.SOLID));
    engine.addEntity(enemy);
    return enemy;
  }

  private Entity player(float x, float y) {
    Entity player = new Entity();
    player.add(new PlayerComponent());
    player.add(new TransformComponent(x, y, 32f, 32f));
    player.add(new VelocityComponent(0f));
    player.add(new HealthComponent(100f));
    player.add(new CollisionComponent(Type.SOLID));
    engine.addEntity(player);
    return player;
  }

  private boolean isAlive(Entity entity) {
    return engine.getEntities().contains(entity, true);
  }

  @Test
  void testProjectileDamagesPlayerAndDespawns() {
    Entity owner = enemy(500f, 500f);
    Entity player = player(0f, 0f);
    Entity projectile = projectile(owner, 10f, 10f, 300f);

    engine.update(0.016f);

    assertEquals(100f - DAMAGE, HealthComponent.MAPPER.get(player).health, 1e-5f);
    assertFalse(isAlive(projectile));
  }

  @Test
  void testProjectileDamagesEnemyThatIsNotItsOwner() {
    Entity owner = player(500f, 500f);
    Entity enemy = enemy(0f, 0f);
    Entity projectile = projectile(owner, 10f, 10f, 300f);

    engine.update(0.016f);

    assertEquals(100f - DAMAGE, HealthComponent.MAPPER.get(enemy).health, 1e-5f);
    assertFalse(isAlive(projectile));
  }

  @Test
  void testProjectileIgnoresItsOwner() {
    Entity owner = enemy(0f, 0f);
    Entity projectile = projectile(owner, 10f, 10f, 300f);

    engine.update(0.016f);

    assertEquals(100f, HealthComponent.MAPPER.get(owner).health, 1e-5f);
    assertTrue(isAlive(projectile));
  }

  @Test
  void testProjectileDespawnsOnSolid() {
    final Entity owner = enemy(500f, 500f);
    Entity block = new Entity();
    block.add(new TransformComponent(0f, 0f, 50f, 50f));
    block.add(new CollisionComponent(Type.SOLID));
    engine.addEntity(block);
    Entity projectile = projectile(owner, 10f, 10f, 300f);

    engine.update(0.016f);

    assertFalse(isAlive(projectile));
    assertTrue(isAlive(block));
  }

  @Test
  void testProjectilePassesThroughTriggers() {
    final Entity owner = enemy(500f, 500f);
    Entity trigger = new Entity();
    trigger.add(new TransformComponent(0f, 0f, 50f, 50f));
    trigger.add(new CollisionComponent(Type.TRIGGER));
    engine.addEntity(trigger);
    Entity projectile = projectile(owner, 10f, 10f, 300f);

    engine.update(0.016f);

    assertTrue(isAlive(projectile));
  }

  @Test
  void testProjectileOnlyHitsOneEntity() {
    Entity owner = enemy(500f, 500f);
    Entity first = enemy(0f, 0f);
    Entity second = enemy(5f, 5f);
    projectile(owner, 10f, 10f, 300f);

    engine.update(0.016f);

    float totalDamage = (100f - HealthComponent.MAPPER.get(first).health)
        + (100f - HealthComponent.MAPPER.get(second).health);
    assertEquals(DAMAGE, totalDamage, 1e-5f);
  }

  @Test
  void testProjectileDeceleratesKeepingDirection() {
    Entity projectile = projectile(null, 0f, 0f, 300f);
    VelocityComponent velocity = VelocityComponent.MAPPER.get(projectile);
    velocity.velocity.set(180f, 240f); // Speed 300

    engine.update(0.5f); // Loses 50 speed

    assertEquals(250f, velocity.velocity.len(), 1e-3f);
    assertEquals(150f, velocity.velocity.x, 1e-3f);
    assertEquals(200f, velocity.velocity.y, 1e-3f);
  }

  @Test
  void testProjectileDespawnsWhenTooSlow() {
    Entity projectile = projectile(null, 0f, 0f, 100f);

    engine.update(0.4f); // Speed 60, still above the minimum of 50
    assertTrue(isAlive(projectile));

    engine.update(0.2f); // Speed 40
    assertFalse(isAlive(projectile));
  }
}
