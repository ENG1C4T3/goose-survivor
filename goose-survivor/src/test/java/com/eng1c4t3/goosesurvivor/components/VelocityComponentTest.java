package com.eng1c4t3.goosesurvivor.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.eng1c4t3.goosesurvivor.ecs.Component;
import org.junit.jupiter.api.Test;

class VelocityComponentTest {

  @Test
  void testVelocityComponentInitialization() {
    VelocityComponent velocity = new VelocityComponent(150f);
    assertNotNull(velocity);
    assertTrue(velocity instanceof Component);
    assertEquals(0f, velocity.velocity.x);
    assertEquals(0f, velocity.velocity.y);
    assertEquals(150f, velocity.maxSpeed);
  }

  @Test
  void testVelocityComponentModification() {
    VelocityComponent velocity = new VelocityComponent(100f);
    velocity.velocity.set(50f, -75f);
    velocity.maxSpeed = 250f;

    assertEquals(50f, velocity.velocity.x);
    assertEquals(-75f, velocity.velocity.y);
    assertEquals(250f, velocity.maxSpeed);
  }
}
