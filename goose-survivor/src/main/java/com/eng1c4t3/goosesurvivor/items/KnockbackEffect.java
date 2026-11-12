package com.eng1c4t3.goosesurvivor.items;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.eng1c4t3.goosesurvivor.components.EnemyComponent;
import com.eng1c4t3.goosesurvivor.components.TransformComponent;
import com.eng1c4t3.goosesurvivor.components.VelocityComponent;

/**
 * Pushes every enemy whose centre is within a radius of the user's centre directly away from
 * the user. Deals no damage.
 *
 * <p>Uses the same knockback mechanism as enemy contact in the CollisionSystem: the enemy's
 * {@link EnemyComponent#knockbackTimer} is started and its velocity set so it covers
 * {@link #distance} over {@link EnemyComponent#knockbackDuration}. The EnemySystem leaves the
 * velocity alone until the knockback ends.
 */
public class KnockbackEffect implements ItemEffect {

  private static final Family ENEMIES = Family.all(
      EnemyComponent.class, TransformComponent.class, VelocityComponent.class).get();

  /** Distance from the user's centre that enemies are pushed within. */
  public final float radius;
  /** Distance each enemy is pushed. */
  public final float distance;

  private final Vector2 direction = new Vector2();

  /**
   * Constructs a new KnockbackEffect.
   *
   * @param radius Distance from the user's centre that enemies are pushed within.
   * @param distance Distance each enemy is pushed.
   */
  public KnockbackEffect(float radius, float distance) {
    this.radius = radius;
    this.distance = distance;
  }

  @Override
  public void apply(Entity user, Engine engine) {
    TransformComponent userTransform = TransformComponent.MAPPER.get(user);
    if (userTransform == null) {
      return;
    }
    Rectangle userBounds = userTransform.position;
    float cx = userBounds.x + userBounds.width / 2;
    float cy = userBounds.y + userBounds.height / 2;

    ImmutableArray<Entity> enemies = engine.getEntitiesFor(ENEMIES);
    for (int i = 0; i < enemies.size(); ++i) {
      Entity enemy = enemies.get(i);
      Rectangle bounds = TransformComponent.MAPPER.get(enemy).position;
      direction.set(bounds.x + bounds.width / 2 - cx, bounds.y + bounds.height / 2 - cy);
      if (direction.len2() > radius * radius) {
        continue;
      }
      if (direction.isZero()) {
        // Enemy exactly on top of the user: pick any direction
        direction.set(1, 0);
      }

      EnemyComponent enemyComponent = EnemyComponent.MAPPER.get(enemy);
      enemyComponent.knockbackTimer = enemyComponent.knockbackDuration;
      VelocityComponent.MAPPER.get(enemy).velocity.set(direction.nor())
          .scl(distance / enemyComponent.knockbackDuration);
    }
  }
}
