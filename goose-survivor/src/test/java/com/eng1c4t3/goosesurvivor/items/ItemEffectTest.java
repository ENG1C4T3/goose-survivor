package com.eng1c4t3.goosesurvivor.items;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.eng1c4t3.goosesurvivor.components.EnemyComponent;
import com.eng1c4t3.goosesurvivor.components.HealthComponent;
import com.eng1c4t3.goosesurvivor.components.TransformComponent;
import com.eng1c4t3.goosesurvivor.components.VelocityComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ItemEffectTest {

  private Engine engine;

  @BeforeEach
  void setUp() {
    engine = new Engine();
  }

  private Entity addUser(float x, float y, float health) {
    Entity user = new Entity();
    user.add(new TransformComponent(x, y, 32f, 32f));
    HealthComponent hp = new HealthComponent(100f);
    hp.health = health;
    user.add(hp);
    engine.addEntity(user);
    return user;
  }

  private HealthComponent addEnemy(float x, float y) {
    Entity enemy = new Entity();
    enemy.add(new TransformComponent(x, y, 32f, 32f));
    HealthComponent hp = new HealthComponent(100f);
    enemy.add(hp);
    enemy.add(new EnemyComponent(10f, 0f));
    engine.addEntity(enemy);
    return hp;
  }

  @Test
  void testHealEffectHealsUpToMax() {
    Entity user = addUser(0, 0, 50f);

    new HealEffect(30f).apply(user, engine);
    assertEquals(80f, HealthComponent.MAPPER.get(user).health, 1e-5f);

    new HealEffect(30f).apply(user, engine);
    assertEquals(100f, HealthComponent.MAPPER.get(user).health, 1e-5f);
  }

  @Test
  void testHealEffectWithoutHealthDoesNothing() {
    Entity user = new Entity();
    new HealEffect(30f).apply(user, engine);
  }

  @Test
  void testKnockbackPushesEnemiesInRadiusAwayWithoutDamage() {
    Entity user = addUser(0, 0, 100f);
    final HealthComponent nearHp = addEnemy(50, 0);
    final HealthComponent farHp = addEnemy(500, 0);
    Entity near = engine.getEntities().get(1);
    Entity far = engine.getEntities().get(2);
    near.add(new VelocityComponent(70f));
    far.add(new VelocityComponent(70f));

    new KnockbackEffect(100f, 150f).apply(user, engine);

    EnemyComponent nearEnemy = EnemyComponent.MAPPER.get(near);
    Vector2 nearVelocity = VelocityComponent.MAPPER.get(near).velocity;
    assertTrue(nearEnemy.isKnockedBack());
    // Pushed directly away from the user (+x), covering 150 units over the knockback duration
    assertEquals(150f / nearEnemy.knockbackDuration, nearVelocity.x, 1e-3f);
    assertEquals(0f, nearVelocity.y, 1e-3f);
    assertFalse(EnemyComponent.MAPPER.get(far).isKnockedBack());
    assertTrue(VelocityComponent.MAPPER.get(far).velocity.isZero());
    assertEquals(100f, nearHp.health, 1e-5f);
    assertEquals(100f, farHp.health, 1e-5f);
  }

  @Test
  void testAreaDamageOnlyHitsEnemiesInRadius() {
    Entity user = addUser(0, 0, 100f);
    final HealthComponent near = addEnemy(50, 0);
    final HealthComponent far = addEnemy(500, 0);

    new AreaDamageEffect(100f, 25f).apply(user, engine);

    assertEquals(75f, near.health, 1e-5f);
    assertEquals(100f, far.health, 1e-5f);
    assertEquals(100f, HealthComponent.MAPPER.get(user).health, 1e-5f);
  }
}
