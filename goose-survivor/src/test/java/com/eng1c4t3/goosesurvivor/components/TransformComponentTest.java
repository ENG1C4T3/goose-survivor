package com.eng1c4t3.goosesurvivor.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TransformComponentTest {

  @Test
  void testTransformComponentInitialization() {
    TransformComponent transform = new TransformComponent(15.5f, 25.0f);
    assertNotNull(transform);
    assertTrue(transform instanceof Component);
    assertEquals(15.5f, transform.position.x);
    assertEquals(25.0f, transform.position.y);
  }

  @Test
  void testTransformComponentModification() {
    TransformComponent transform = new TransformComponent(0f, 0f);
    transform.position.set(100f, 200f);
    assertEquals(100f, transform.position.x);
    assertEquals(200f, transform.position.y);

    transform.position.x += 5f;
    transform.position.y -= 10f;
    assertEquals(105f, transform.position.x);
    assertEquals(190f, transform.position.y);
  }
}
