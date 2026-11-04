package com.eng1c4t3.goosesurvivor.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.badlogic.ashley.core.Entity;
import com.eng1c4t3.goosesurvivor.components.PlayerComponent;
import com.eng1c4t3.goosesurvivor.components.TextureComponent;
import com.eng1c4t3.goosesurvivor.components.TransformComponent;
import com.eng1c4t3.goosesurvivor.components.VelocityComponent;
import org.junit.jupiter.api.Test;

class EntityFactoryTest {

  @Test
  void testCreatePlayerDefault() {
    Entity player = EntityFactory.createPlayer(null);

    assertNotNull(player);
    assertNotNull(PlayerComponent.MAPPER.get(player));
    assertNotNull(TransformComponent.MAPPER.get(player));
    assertNotNull(VelocityComponent.MAPPER.get(player));
    assertNotNull(TextureComponent.MAPPER.get(player));

    TransformComponent transform = TransformComponent.MAPPER.get(player);
    assertEquals(10f, transform.position.x);
    assertEquals(10f, transform.position.y);

    VelocityComponent velocity = VelocityComponent.MAPPER.get(player);
    assertEquals(200f, velocity.maxSpeed);
  }

  @Test
  void testCreatePlayerCustom() {
    Entity player = EntityFactory.createPlayer(50f, 60f, 300f, null);

    assertNotNull(player);
    assertNotNull(PlayerComponent.MAPPER.get(player));
    assertNotNull(TransformComponent.MAPPER.get(player));
    assertNotNull(VelocityComponent.MAPPER.get(player));
    assertNotNull(TextureComponent.MAPPER.get(player));

    TransformComponent transform = TransformComponent.MAPPER.get(player);
    assertEquals(50f, transform.position.x);
    assertEquals(60f, transform.position.y);

    VelocityComponent velocity = VelocityComponent.MAPPER.get(player);
    assertEquals(300f, velocity.maxSpeed);
  }
}
