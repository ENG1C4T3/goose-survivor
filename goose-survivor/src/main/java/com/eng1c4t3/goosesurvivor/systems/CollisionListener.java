package com.eng1c4t3.goosesurvivor.systems;

import com.badlogic.ashley.core.Entity;

/**
 * Receives a callback from the {@link CollisionSystem} whenever two entities overlap.
 */
public interface CollisionListener {

  /**
   * Called when a moving entity overlaps another collidable entity.
   *
   * <p>{@code entityA} is always the moving entity. If both entities are moving, the pair is
   * reported once from each side.
   *
   * @param entityA The moving entity.
   * @param entityB The entity it overlaps.
   */
  void onCollision(Entity entityA, Entity entityB);
}
