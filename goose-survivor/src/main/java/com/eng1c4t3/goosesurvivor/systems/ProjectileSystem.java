package com.eng1c4t3.goosesurvivor.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.eng1c4t3.goosesurvivor.components.CollisionComponent;
import com.eng1c4t3.goosesurvivor.components.HealthComponent;
import com.eng1c4t3.goosesurvivor.components.ProjectileComponent;
import com.eng1c4t3.goosesurvivor.components.VelocityComponent;

/**
 * Slows projectiles down, despawns them once they are too slow, and handles what happens when
 * they hit something.
 *
 * <p>Must be registered as a {@link CollisionListener} on the {@link CollisionSystem} to
 * receive hits. When a projectile hits:
 * <ul>
 *   <li>its owner: nothing happens;</li>
 *   <li>an entity with health (player or enemy): it takes the projectile's damage and the
 *       projectile is destroyed;</li>
 *   <li>any other solid entity: the projectile is destroyed;</li>
 *   <li>anything else (e.g. a trigger or another projectile): nothing happens.</li>
 * </ul>
 *
 * @see ProjectileComponent
 */
public class ProjectileSystem extends IteratingSystem implements CollisionListener {

  /**
   * Constructs a ProjectileSystem that processes projectiles.
   */
  public ProjectileSystem() {
    super(Family.all(ProjectileComponent.class, VelocityComponent.class).get());
  }

  /**
   * Decelerates the projectile and removes it once it drops below its minimum speed.
   *
   * @param entity The projectile entity.
   * @param deltaTime The time passed since the last frame.
   */
  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    ProjectileComponent projectile = ProjectileComponent.MAPPER.get(entity);
    VelocityComponent velocity = VelocityComponent.MAPPER.get(entity);

    float speed = velocity.velocity.len() - projectile.deceleration * deltaTime;
    if (speed < projectile.minSpeed) {
      getEngine().removeEntity(entity);
      return;
    }
    velocity.velocity.setLength(speed);
  }

  /**
   * Handles a projectile hitting another entity.
   *
   * @param entityA The moving entity; only handled if it is a projectile.
   * @param entityB The entity it overlaps.
   */
  @Override
  public void onCollision(Entity entityA, Entity entityB) {
    ProjectileComponent projectile = ProjectileComponent.MAPPER.get(entityA);
    if (projectile == null || getEngine() == null) {
      return;
    }
    // Already used up this frame (e.g. it overlaps two entities at once), or the target is
    // already gone
    if (entityA.isScheduledForRemoval() || entityB.isScheduledForRemoval()) {
      return;
    }
    if (entityB == projectile.owner) {
      return;
    }

    HealthComponent health = HealthComponent.MAPPER.get(entityB);
    if (health != null) {
      health.takeDamage(projectile.baseDamage);
      getEngine().removeEntity(entityA);
      return;
    }

    CollisionComponent collision = CollisionComponent.MAPPER.get(entityB);
    if (collision != null && collision.type == CollisionComponent.Type.SOLID) {
      getEngine().removeEntity(entityA);
    }
  }
}
