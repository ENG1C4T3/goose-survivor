package com.eng1c4t3.goosesurvivor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.eng1c4t3.goosesurvivor.components.CollisionComponent;
import com.eng1c4t3.goosesurvivor.components.HealthComponent;
import com.eng1c4t3.goosesurvivor.components.PlayerComponent;
import com.eng1c4t3.goosesurvivor.components.ProjectileComponent;
import com.eng1c4t3.goosesurvivor.components.ShooterComponent;
import com.eng1c4t3.goosesurvivor.components.TextureComponent;
import com.eng1c4t3.goosesurvivor.components.TransformComponent;
import com.eng1c4t3.goosesurvivor.entities.EntityFactory;
import org.junit.jupiter.api.Test;

class GameSessionProjectileIntegrationTest {

  @Test
  void longBoiFiresUsingConfiguredTexture() {
    final GameSession session = new GameSession();
    final TextureRegion region = new TextureRegion();
    session.configureShooting(region);
    session.getWorld().addEntity(createTarget(500f, 0f, 100f));

    final Entity shooter = EntityFactory.createEnemy(
        EntityFactory.EnemyType.LONG_BOI, 0f, 0f, region);
    ShooterComponent.MAPPER.get(shooter).shotTimer = 0f;
    session.getWorld().addEntity(shooter);

    session.update(0.016f);

    final Entity projectile = findProjectile(session);
    assertNotNull(projectile);
    assertSame(shooter, ProjectileComponent.MAPPER.get(projectile).owner);
    assertSame(region, TextureComponent.MAPPER.get(projectile).region);
  }

  @Test
  void pauseFreezesShootingAndProjectileMovement() {
    final GameSession session = new GameSession();
    final TextureRegion region = new TextureRegion();
    session.configureShooting(region);
    session.getWorld().addEntity(createTarget(500f, 0f, 100f));

    final Entity shooter = EntityFactory.createEnemy(
        EntityFactory.EnemyType.LONG_BOI, 0f, 0f, region);
    final ShooterComponent shooting = ShooterComponent.MAPPER.get(shooter);
    shooting.shotTimer = 0f;
    session.getWorld().addEntity(shooter);
    session.update(0.016f);

    final Entity projectile = findProjectile(session);
    assertNotNull(projectile);
    final TransformComponent transform = TransformComponent.MAPPER.get(projectile);
    final float beforeX = transform.position.x;
    final float beforeY = transform.position.y;
    final float beforeTimer = shooting.shotTimer;

    session.pause();
    session.update(1f);

    assertEquals(SessionState.PAUSED, session.getState());
    assertEquals(beforeX, transform.position.x, 0.001f);
    assertEquals(beforeY, transform.position.y, 0.001f);
    assertEquals(beforeTimer, shooting.shotTimer, 0.001f);

    session.resume();
    session.update(0.016f);

    assertEquals(SessionState.RUNNING, session.getState());
    assertEquals(beforeTimer - 0.016f, shooting.shotTimer, 0.001f);
  }

  @Test
  void lethalProjectileHitRemovesPlayerAndEndsSession() {
    final GameSession session = new GameSession();
    final Entity player = createTarget(100f, 100f, 10f);
    session.getWorld().addEntity(player);

    final Entity projectile = EntityFactory.createProjectile(
        new Entity(), 116f, 116f, new Vector2(100f, 0f),
        15f, new TextureRegion());
    session.getWorld().addEntity(projectile);

    session.update(0.016f);

    assertEquals(SessionState.LOST, session.getState());
    assertFalse(session.getWorld().getEntities().contains(player, true));
    assertFalse(session.getWorld().getEntities().contains(projectile, true));
  }

  private static Entity createTarget(float x, float y, float health) {
    final Entity player = new Entity();
    player.add(new PlayerComponent());
    player.add(new TransformComponent(x, y, 32f, 32f));
    player.add(new HealthComponent(health));
    player.add(new CollisionComponent(CollisionComponent.Type.SOLID));
    return player;
  }

  private static Entity findProjectile(GameSession session) {
    for (Entity entity : session.getWorld().getEntities()) {
      if (ProjectileComponent.MAPPER.has(entity)) {
        return entity;
      }
    }
    return null;
  }
}