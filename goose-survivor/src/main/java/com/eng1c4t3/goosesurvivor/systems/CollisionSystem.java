package com.eng1c4t3.goosesurvivor.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.SnapshotArray;
import com.eng1c4t3.goosesurvivor.components.CollisionComponent;
import com.eng1c4t3.goosesurvivor.components.EnemyComponent;
import com.eng1c4t3.goosesurvivor.components.HealthComponent;
import com.eng1c4t3.goosesurvivor.components.PlayerComponent;
import com.eng1c4t3.goosesurvivor.components.TransformComponent;
import com.eng1c4t3.goosesurvivor.components.VelocityComponent;

/**
 * A system that handles collision detection and resolution in a game world.
 *
 * <p>This system processes entities that contain the necessary components for collision:
 * TransformComponent, VelocityComponent (for dynamic entities), and CollisionComponent.
 * It detects overlaps between entities and resolves collisions based on the type of the
 * collidable entity.
 *
 * <p>Resolved collision types include:
 * <ul>
 *   <li>SOLID: Handles solid collisions by halting movement and preventing overlap.</li>
 *   <li>TRIGGER: Not resolved; the overlap is only reported to listeners.</li>
 * </ul>
 *
 * <p>Every overlap, of either type, is reported to the registered {@link CollisionListener}s.
 */
public class CollisionSystem extends EntitySystem {

  private ImmutableArray<Entity> dynamicEntities;
  private ImmutableArray<Entity> collidableEntities;

  // Reusable rectangles to avoid garbage collection overhead inside the update loop
  private final Rectangle rectA = new Rectangle();
  private final Rectangle rectB = new Rectangle();
  private final Vector2 knockbackDirection = new Vector2();

  private final SnapshotArray<CollisionListener> listeners =
      new SnapshotArray<>(true, 4, CollisionListener.class);

  /**
   * Caches all entities required for collision detection.
   *
   * @param engine The {@link Engine} this system was added to.
   */
  @Override
  public void addedToEngine(Engine engine) {
    dynamicEntities = engine.getEntitiesFor(
        Family.all(TransformComponent.class, VelocityComponent.class,
            CollisionComponent.class).get()
    );

    collidableEntities = engine.getEntitiesFor(
        Family.all(TransformComponent.class, CollisionComponent.class).get()
    );
  }

  /**
   * Updates the dynamic entities in the game and resolves collisions between them
   * and other collidable entities. This method checks for overlaps between the bounding
   * rectangles of entities and triggers the necessary collision resolution logic.
   *
   * @param deltaTime The time difference between the current and previous frame.
   */
  @Override
  public void update(float deltaTime) {
    for (int i = 0; i < dynamicEntities.size(); ++i) {
      Entity mover = dynamicEntities.get(i);
      TransformComponent transform = TransformComponent.MAPPER.get(mover);
      VelocityComponent velocity = VelocityComponent.MAPPER.get(mover);

      boolean moverIsPlayer = PlayerComponent.MAPPER.has(mover);

      for (int j = 0; j < collidableEntities.size(); ++j) {
        Entity other = collidableEntities.get(j);
        if (mover == other) {
          continue; // Skip self
        }

        // The player must only ever be displaced by static geometry, never by other moving
        // entities (e.g. enemies walking into them). Otherwise, enemies would shove the player
        // around without any input. The other entity is pushed out of the player instead.
        if (moverIsPlayer && VelocityComponent.MAPPER.has(other)) {
          continue;
        }

        // Refresh each iteration, since a previous resolution may have moved the mover
        rectA.set(transform.position.x, transform.position.y,
            transform.position.width, transform.position.height);

        TransformComponent otherTransform = TransformComponent.MAPPER.get(other);
        CollisionComponent otherCollision = CollisionComponent.MAPPER.get(other);

        rectB.set(otherTransform.position.x, otherTransform.position.y,
            otherTransform.position.width, otherTransform.position.height);

        if (rectA.overlaps(rectB)) {
          if (EnemyComponent.MAPPER.has(mover) && PlayerComponent.MAPPER.has(other)) {
            handleEnemyPlayerContact(mover, other);
          } else if (otherCollision.type == CollisionComponent.Type.SOLID) {
            resolveSolidCollision(transform, velocity, otherTransform);
          }
          notifyCollision(mover, other);
        }
      }
    }
  }

  /**
   * Registers a listener to be notified of every collision.
   *
   * @param listener The listener to add.
   */
  public void addCollisionListener(CollisionListener listener) {
    listeners.add(listener);
  }

  /**
   * Stops a listener from being notified of collisions.
   *
   * @param listener The listener to remove.
   */
  public void removeCollisionListener(CollisionListener listener) {
    listeners.removeValue(listener, true);
  }

  /**
   * Notifies every registered listener of a collision.
   *
   * <p>Listeners may safely add or remove listeners while being notified.
   *
   * @param entityA The moving entity.
   * @param entityB The entity it overlaps.
   */
  private void notifyCollision(Entity entityA, Entity entityB) {
    CollisionListener[] snapshot = listeners.begin();
    for (int i = 0, n = listeners.size; i < n; ++i) {
      snapshot[i].onCollision(entityA, entityB);
    }
    listeners.end();
  }

  /**
   * Handles an enemy touching the player: the player takes the enemy's contact damage and the
   * enemy is knocked back in the direction opposite to its current velocity.
   *
   * <p>Damage is only dealt once per knockback, so an enemy can't hit the player every frame
   * while the two still overlap.
   *
   * @param enemy The enemy entity.
   * @param player The player entity.
   */
  private void handleEnemyPlayerContact(Entity enemy, Entity player) {
    TransformComponent enemyTransform = TransformComponent.MAPPER.get(enemy);
    VelocityComponent enemyVelocity = VelocityComponent.MAPPER.get(enemy);
    TransformComponent playerTransform = TransformComponent.MAPPER.get(player);

    // Work out the knockback direction before resolving the overlap, since resolution zeroes
    // a component of the velocity.
    knockbackDirection.set(enemyVelocity.velocity).scl(-1);
    if (knockbackDirection.isZero()) {
      // Enemy isn't moving (e.g. the player walked into it): push it away from the player
      knockbackDirection.set(
          enemyTransform.position.x + enemyTransform.position.width / 2
              - (playerTransform.position.x + playerTransform.position.width / 2),
          enemyTransform.position.y + enemyTransform.position.height / 2
              - (playerTransform.position.y + playerTransform.position.height / 2));
      if (knockbackDirection.isZero()) {
        knockbackDirection.set(1, 0);
      }
    }
    knockbackDirection.nor();

    // Keep the enemy from overlapping the player this frame
    resolveSolidCollision(enemyTransform, enemyVelocity, playerTransform);

    EnemyComponent enemyComponent = EnemyComponent.MAPPER.get(enemy);
    if (enemyComponent.isKnockedBack()) {
      return;
    }

    HealthComponent playerHealth = HealthComponent.MAPPER.get(player);
    if (playerHealth != null) {
      playerHealth.takeDamage(enemyComponent.damage);
    }

    // Cover knockbackDistance over knockbackDuration; EnemySystem leaves the velocity alone
    // until the timer runs out.
    enemyComponent.knockbackTimer = enemyComponent.knockbackDuration;
    enemyVelocity.velocity.set(knockbackDirection)
        .scl(enemyComponent.knockbackDistance / enemyComponent.knockbackDuration);
  }

  /**
   * Resolves collision between a moving entity and a solid entity.
   *
   * <p>Uses axis-aligned bounding boxes (AABBs) to determine the overlap along the X and Y axes.
   *
   * @param moverTransform The transform component of the moving entity.
   * @param moverVelocity The velocity component of the moving entity.
   * @param solidTransform The transform component of the solid entity.
   */
  private void resolveSolidCollision(TransformComponent moverTransform,
                                     VelocityComponent moverVelocity,
                                     TransformComponent solidTransform) {
    // Calculate overlap along X and Y axes
    float overlapX = (moverTransform.position.x < solidTransform.position.x)
        ? (moverTransform.position.x + moverTransform.position.width) - solidTransform.position.x
        : (solidTransform.position.x + solidTransform.position.width) - moverTransform.position.x;

    float overlapY = (moverTransform.position.y < solidTransform.position.y)
        ? (moverTransform.position.y + moverTransform.position.height) - solidTransform.position.y
        : (solidTransform.position.y + solidTransform.position.height) - moverTransform.position.y;

    // Resolve along the axis with the minimal penetration depth
    if (overlapX < overlapY) {
      if (moverTransform.position.x < solidTransform.position.x) {
        moverTransform.position.x -= overlapX;
      } else {
        moverTransform.position.x += overlapX;
      }
      moverVelocity.velocity.x = 0; // Stop horizontal momentum
    } else {
      if (moverTransform.position.y < solidTransform.position.y) {
        moverTransform.position.y -= overlapY;
      } else {
        moverTransform.position.y += overlapY;
      }
      moverVelocity.velocity.y = 0; // Stop vertical momentum
    }
  }
}
