package com.eng1c4t3.goosesurvivor.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.eng1c4t3.goosesurvivor.components.PlayerComponent;
import com.eng1c4t3.goosesurvivor.components.ShooterComponent;
import com.eng1c4t3.goosesurvivor.components.TransformComponent;
import com.eng1c4t3.goosesurvivor.entities.EntityFactory;

/**
 * Makes every entity with a {@link ShooterComponent} fire a projectile towards the player once
 * per shot interval.
 *
 * @see ProjectileSystem
 */
public class ShooterSystem extends EntitySystem {

  /**
   * Creates projectile entities. Lets tests spawn projectiles without loading textures.
   */
  @FunctionalInterface
  public interface ProjectileFactory {
    /**
     * Creates a projectile.
     *
     * @param owner The entity firing the projectile.
     * @param centreX The x-coordinate of the projectile's centre.
     * @param centreY The y-coordinate of the projectile's centre.
     * @param velocity The projectile's initial velocity.
     * @param damage The damage the projectile deals.
     * @return The new projectile entity.
     */
    Entity create(Entity owner, float centreX, float centreY, Vector2 velocity, float damage);
  }

  private final ProjectileFactory projectileFactory;
  private final Vector2 direction = new Vector2();
  private ImmutableArray<Entity> players;
  private ImmutableArray<Entity> shooters;

  /**
   * Constructs a shooter system using the supplied projectile texture.
   *
   * @param region The texture region for projectile sprites.
   */
  public ShooterSystem(TextureRegion region) {
    this((owner, centreX, centreY, velocity, damage) ->
        EntityFactory.createProjectile(owner, centreX, centreY, velocity, damage, region));
  }

  /**
   * Constructs a ShooterSystem that creates projectiles with the given factory.
   *
   * @param projectileFactory The factory used to create projectiles.
   */
  public ShooterSystem(ProjectileFactory projectileFactory) {
    this.projectileFactory = projectileFactory;
  }

  /**
   * Caches the player and shooter entities.
   *
   * @param engine The {@link Engine} this system was added to.
   */
  @Override
  public void addedToEngine(Engine engine) {
    players = engine.getEntitiesFor(
        Family.all(PlayerComponent.class, TransformComponent.class).get());
    shooters = engine.getEntitiesFor(
        Family.all(ShooterComponent.class, TransformComponent.class).get());
  }

  /**
   * Ticks each shooter's timer and fires a projectile at the player when it runs out.
   *
   * @param deltaTime The time passed since the last frame.
   */
  @Override
  public void update(float deltaTime) {
    if (players.size() == 0) {
      return;
    }
    Rectangle target = TransformComponent.MAPPER.get(players.first()).position;

    for (int i = 0; i < shooters.size(); ++i) {
      Entity shooter = shooters.get(i);
      ShooterComponent shooterComponent = ShooterComponent.MAPPER.get(shooter);

      shooterComponent.shotTimer -= deltaTime;
      if (shooterComponent.shotTimer > 0) {
        continue;
      }
      shooterComponent.shotTimer += shooterComponent.shotInterval;

      Rectangle origin = TransformComponent.MAPPER.get(shooter).position;
      float originX = origin.x + origin.width / 2;
      float originY = origin.y + origin.height / 2;
      direction.set(target.x + target.width / 2 - originX,
          target.y + target.height / 2 - originY);
      if (direction.isZero()) {
        continue;
      }
      direction.nor().scl(shooterComponent.projectileSpeed);

      getEngine().addEntity(projectileFactory.create(
          shooter, originX, originY, direction, shooterComponent.projectileDamage));
    }
  }
}
