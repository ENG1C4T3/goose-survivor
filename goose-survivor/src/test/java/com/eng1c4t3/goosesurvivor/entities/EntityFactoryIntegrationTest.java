package com.eng1c4t3.goosesurvivor.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.eng1c4t3.goosesurvivor.components.CollisionComponent;
import com.eng1c4t3.goosesurvivor.components.EnemyComponent;
import com.eng1c4t3.goosesurvivor.components.HealthComponent;
import com.eng1c4t3.goosesurvivor.components.HiddenEventComponent;
import com.eng1c4t3.goosesurvivor.components.InteractableComponent;
import com.eng1c4t3.goosesurvivor.components.PlayerComponent;
import com.eng1c4t3.goosesurvivor.components.TextureComponent;
import com.eng1c4t3.goosesurvivor.components.TransformComponent;
import com.eng1c4t3.goosesurvivor.components.VelocityComponent;
import com.eng1c4t3.goosesurvivor.entities.EntityFactory.EnemyStats;
import com.eng1c4t3.goosesurvivor.entities.EntityFactory.EnemyType;
import org.junit.jupiter.api.Test;

class EntityFactoryIntegrationTest {

  @Test
  void playerUsesProvidedTextureAndRetainsGameplayComponents() {
    final TextureRegion region = new TextureRegion();
    final Entity player = EntityFactory.createPlayer(25f, 35f, region);
    final TransformComponent transform = TransformComponent.MAPPER.get(player);
    final HealthComponent health = HealthComponent.MAPPER.get(player);

    assertSame(region, TextureComponent.MAPPER.get(player).region);
    assertNotNull(PlayerComponent.MAPPER.get(player));
    assertNotNull(CollisionComponent.MAPPER.get(player));
    assertNotNull(VelocityComponent.MAPPER.get(player));
    assertEquals(25f, transform.position.x, 0.001f);
    assertEquals(35f, transform.position.y, 0.001f);
    assertEquals(36f, transform.position.width, 0.001f);
    assertEquals(62f, transform.position.height, 0.001f);
    assertEquals(100f, health.health, 0.001f);
    assertEquals(EntityFactory.PLAYER_DAMAGE_COOLDOWN, health.damageCooldown, 0.001f);
  }

  @Test
  void everyEnemyTypeUsesProvidedTextureAndConfiguredStats() {
    final TextureRegion region = new TextureRegion();

    for (EnemyType type : EnemyType.values()) {
      final EnemyStats stats = EntityFactory.statsFor(type);
      final Entity enemy = EntityFactory.createEnemy(type, 40f, 60f, region);
      final TransformComponent transform = TransformComponent.MAPPER.get(enemy);

      assertSame(region, TextureComponent.MAPPER.get(enemy).region);
      assertNotNull(EnemyComponent.MAPPER.get(enemy));
      assertNotNull(CollisionComponent.MAPPER.get(enemy));
      assertEquals(40f, transform.position.x, 0.001f);
      assertEquals(60f, transform.position.y, 0.001f);
      assertEquals(stats.width(), transform.position.width, 0.001f);
      assertEquals(stats.height(), transform.position.height, 0.001f);
      assertEquals(stats.health(), HealthComponent.MAPPER.get(enemy).health, 0.001f);
      assertEquals(stats.speed(), VelocityComponent.MAPPER.get(enemy).maxSpeed, 0.001f);
    }
  }

  @Test
  void blockUsesProvidedTextureAndDimensions() {
    final TextureRegion region = new TextureRegion();
    final Entity block = EntityFactory.createBlock(10f, 20f, 50f, 60f, region);
    final TransformComponent transform = TransformComponent.MAPPER.get(block);

    assertSame(region, TextureComponent.MAPPER.get(block).region);
    assertNotNull(CollisionComponent.MAPPER.get(block));
    assertEquals(10f, transform.position.x, 0.001f);
    assertEquals(20f, transform.position.y, 0.001f);
    assertEquals(50f, transform.position.width, 0.001f);
    assertEquals(60f, transform.position.height, 0.001f);
  }

  @Test
  void fountainHasVisitEventAndRemainsInWorld() {
    final TextureRegion region = new TextureRegion();
    final Entity fountain = EntityFactory.createFountain(30f, 40f, region);
    final InteractableComponent interaction = InteractableComponent.MAPPER.get(fountain);
    final HiddenEventComponent event = HiddenEventComponent.MAPPER.get(fountain);

    assertSame(region, TextureComponent.MAPPER.get(fountain).region);
    assertNotNull(event);
    assertEquals(HiddenEventComponent.EventType.FOUNTAIN, event.type);
    assertEquals(EntityFactory.INTERACT_RANGE, interaction.range, 0.001f);
    assertEquals("Make a wish", interaction.promptText);
    assertFalse(interaction.singleUse);
    assertNotNull(CollisionComponent.MAPPER.get(fountain));
  }
}