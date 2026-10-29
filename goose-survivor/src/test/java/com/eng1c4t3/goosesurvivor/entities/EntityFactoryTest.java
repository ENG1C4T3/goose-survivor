package com.eng1c4t3.goosesurvivor.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.eng1c4t3.goosesurvivor.components.PlayerComponent;
import com.eng1c4t3.goosesurvivor.components.TextureComponent;
import com.eng1c4t3.goosesurvivor.components.TransformComponent;
import com.eng1c4t3.goosesurvivor.components.VelocityComponent;
import com.eng1c4t3.goosesurvivor.ecs.Entity;
import org.junit.jupiter.api.Test;

class EntityFactoryTest {

  @Test
  void testCreatePlayerDefault() {
    Entity player = EntityFactory.createPlayer(null);

    assertNotNull(player);
    assertTrue(player.hasComponent(PlayerComponent.class));
    assertTrue(player.hasComponent(TransformComponent.class));
    assertTrue(player.hasComponent(VelocityComponent.class));
    assertTrue(player.hasComponent(TextureComponent.class));

    TransformComponent transform = player.getComponent(TransformComponent.class);
    assertEquals(10f, transform.position.x);
    assertEquals(10f, transform.position.y);

    VelocityComponent velocity = player.getComponent(VelocityComponent.class);
    assertEquals(200f, velocity.maxSpeed);
  }
}
