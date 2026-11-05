package com.eng1c4t3.goosesurvivor.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Rectangle;
import com.eng1c4t3.goosesurvivor.components.CollisionComponent;
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
 * </ul>
 */
public class CollisionSystem extends EntitySystem {

  private ImmutableArray<Entity> dynamicEntities;
  private ImmutableArray<Entity> collidableEntities;

  // Reusable rectangles to avoid garbage collection overhead inside the update loop
  private final Rectangle rectA = new Rectangle();
  private final Rectangle rectB = new Rectangle();

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

      rectA.set(transform.position.x, transform.position.y,
          transform.position.width, transform.position.height);

      for (int j = 0; j < collidableEntities.size(); ++j) {
        Entity other = collidableEntities.get(j);
        if (mover == other) {
          continue; // Skip self
        }

        TransformComponent otherTransform = TransformComponent.MAPPER.get(other);
        CollisionComponent otherCollision = CollisionComponent.MAPPER.get(other);

        rectB.set(otherTransform.position.x, otherTransform.position.y,
            otherTransform.position.width, otherTransform.position.height);

        if (rectA.overlaps(rectB)) {
          switch (otherCollision.type) {
            case SOLID:
              resolveSolidCollision(transform, velocity, otherTransform);
              break;
            default:
              break;
          }
        }
      }
    }
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
