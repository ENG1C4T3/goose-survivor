package com.eng1c4t3.goosesurvivor.systems;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.eng1c4t3.goosesurvivor.components.PlayerComponent;
import com.eng1c4t3.goosesurvivor.components.ProjectileComponent;
import com.eng1c4t3.goosesurvivor.components.ShooterComponent;
import com.eng1c4t3.goosesurvivor.components.TransformComponent;
import com.eng1c4t3.goosesurvivor.components.VelocityComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ShooterSystemTest {

  private Engine engine;
  private ImmutableArray<Entity> projectiles;

  /** Builds projectiles without a texture, so no graphics context is needed. */
  private static Entity createProjectile(Entity owner, float centreX, float centreY,
                                         Vector2 velocity, float damage) {
    Entity projectile = new Entity();
    projectile.add(new TransformComponent(centreX - 8f, centreY - 8f, 16f, 16f));
    VelocityComponent velocityComponent = new VelocityComponent(velocity.len());
    velocityComponent.velocity.set(velocity);
    projectile.add(velocityComponent);
    projectile.add(new ProjectileComponent(owner, damage, 0f, 0f));
    return projectile;
  }

  @BeforeEach
  void setUp() {
    engine = new Engine();
    engine.addSystem(new ShooterSystem(ShooterSystemTest::createProjectile));
    projectiles = engine.getEntitiesFor(Family.all(ProjectileComponent.class).get());
  }

  private Entity shooter(float x, float y) {
    Entity shooter = new Entity();
    shooter.add(new TransformComponent(x, y, 32f, 32f));
    shooter.add(new ShooterComponent(20f, 300f, 15f));
    engine.addEntity(shooter);
    return shooter;
  }

  private void player(float x, float y) {
    Entity player = new Entity();
    player.add(new PlayerComponent());
    player.add(new TransformComponent(x, y, 32f, 32f));
    engine.addEntity(player);
  }

  @Test
  void testFiresAtPlayerOncePerInterval() {
    final Entity shooter = shooter(0f, 0f);
    player(300f, 400f);

    engine.update(19.9f);
    assertEquals(0, projectiles.size());

    engine.update(0.2f);
    assertEquals(1, projectiles.size());

    Entity projectile = projectiles.first();
    ProjectileComponent component = ProjectileComponent.MAPPER.get(projectile);
    assertSame(shooter, component.owner);
    assertEquals(15f, component.baseDamage, 1e-5f);

    // Aimed from the shooter's centre to the player's centre: a 3-4-5 triangle
    Vector2 velocity = VelocityComponent.MAPPER.get(projectile).velocity;
    assertEquals(180f, velocity.x, 1e-3f);
    assertEquals(240f, velocity.y, 1e-3f);

    // Spawned centred on the shooter
    TransformComponent transform = TransformComponent.MAPPER.get(projectile);
    assertEquals(16f, transform.position.x + transform.position.width / 2, 1e-5f);
    assertEquals(16f, transform.position.y + transform.position.height / 2, 1e-5f);

    engine.update(19.7f);
    assertEquals(1, projectiles.size());
    engine.update(0.3f);
    assertEquals(2, projectiles.size());
  }

  @Test
  void testDoesNotFireWithoutPlayer() {
    shooter(0f, 0f);

    engine.update(25f);

    assertEquals(0, projectiles.size());
  }
}
