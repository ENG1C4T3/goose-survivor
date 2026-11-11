package com.eng1c4t3.goosesurvivor.items;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.eng1c4t3.goosesurvivor.components.SpeedBoostComponent;
import com.eng1c4t3.goosesurvivor.components.VelocityComponent;
import com.eng1c4t3.goosesurvivor.systems.SpeedBoostSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests SpeedBoostEffect together with the SpeedBoostSystem that ends the boost.
 */
class SpeedBoostEffectTest {

  private Engine engine;
  private Entity user;

  @BeforeEach
  void setUp() {
    engine = new Engine();
    engine.addSystem(new SpeedBoostSystem());
    user = new Entity();
    user.add(new VelocityComponent(100f));
    engine.addEntity(user);
  }

  private float maxSpeed() {
    return VelocityComponent.MAPPER.get(user).maxSpeed;
  }

  @Test
  void testBoostMultipliesSpeed() {
    new SpeedBoostEffect(1.5f, 5f).apply(user, engine);

    assertEquals(150f, maxSpeed(), 1e-5f);
    assertTrue(SpeedBoostComponent.MAPPER.has(user));
  }

  @Test
  void testBoostLastsForDurationThenSpeedIsRestored() {
    new SpeedBoostEffect(2f, 5f).apply(user, engine);

    engine.update(4.9f);
    assertEquals(200f, maxSpeed(), 1e-5f);

    engine.update(0.2f);
    assertEquals(100f, maxSpeed(), 1e-5f);
    assertFalse(SpeedBoostComponent.MAPPER.has(user));
  }

  @Test
  void testReapplyingRefreshesInsteadOfStacking() {
    SpeedBoostEffect boost = new SpeedBoostEffect(2f, 5f);
    boost.apply(user, engine);
    engine.update(3f);

    boost.apply(user, engine);

    assertEquals(200f, maxSpeed(), 1e-5f);
    assertEquals(5f, SpeedBoostComponent.MAPPER.get(user).remainingTime, 1e-5f);

    engine.update(5.1f);
    assertEquals(100f, maxSpeed(), 1e-5f);
  }

  @Test
  void testUserWithoutVelocityIsIgnored() {
    Entity statue = new Entity();
    engine.addEntity(statue);

    new SpeedBoostEffect(2f, 5f).apply(statue, engine);

    assertFalse(SpeedBoostComponent.MAPPER.has(statue));
  }

  @Test
  void testInvalidArgumentsAreRejected() {
    assertThrows(IllegalArgumentException.class, () -> new SpeedBoostEffect(0f, 5f));
    assertThrows(IllegalArgumentException.class, () -> new SpeedBoostEffect(2f, 0f));
  }
}
