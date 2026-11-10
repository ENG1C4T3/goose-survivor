package com.eng1c4t3.goosesurvivor.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.eng1c4t3.goosesurvivor.systems.HealthSystem;
import org.junit.jupiter.api.Test;

class HealthComponentTest {

  @Test
  void testNoCooldownAllowsRepeatedDamage() {
    HealthComponent hp = new HealthComponent(100f);

    assertTrue(hp.takeDamage(10f));
    assertTrue(hp.takeDamage(10f));
    assertEquals(80f, hp.health, 1e-5f);
  }

  @Test
  void testDamageIgnoredDuringCooldown() {
    HealthComponent hp = new HealthComponent(100f, 0.5f);

    assertTrue(hp.takeDamage(10f));
    assertTrue(hp.isInvulnerable());
    assertFalse(hp.takeDamage(10f));
    assertEquals(90f, hp.health, 1e-5f);
  }

  @Test
  void testHealthSystemCountsDownCooldown() {
    Engine engine = new Engine();
    engine.addSystem(new HealthSystem());
    HealthComponent hp = new HealthComponent(100f, 0.5f);
    Entity entity = new Entity();
    entity.add(hp);
    engine.addEntity(entity);

    hp.takeDamage(10f);
    engine.update(0.3f);
    assertTrue(hp.isInvulnerable());

    engine.update(0.3f);
    assertFalse(hp.isInvulnerable());
    assertTrue(hp.takeDamage(10f));
    assertEquals(80f, hp.health, 1e-5f);
  }

  @Test
  void testHealCappedAndDeathDetected() {
    HealthComponent hp = new HealthComponent(100f);
    hp.heal(50f);
    assertEquals(100f, hp.health, 1e-5f);

    hp.takeDamage(150f);
    assertEquals(0f, hp.health, 1e-5f);
    assertTrue(hp.isDead());
  }
}
