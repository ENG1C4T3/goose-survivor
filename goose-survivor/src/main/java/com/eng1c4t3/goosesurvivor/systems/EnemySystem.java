package com.eng1c4t3.goosesurvivor.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.eng1c4t3.goosesurvivor.components.DecoyComponent;
import com.eng1c4t3.goosesurvivor.components.EnemyComponent;
import com.eng1c4t3.goosesurvivor.components.PlayerComponent;
import com.eng1c4t3.goosesurvivor.components.TransformComponent;
import com.eng1c4t3.goosesurvivor.components.VelocityComponent;

/**
 * System that manages enemy behavior and directs enemies to follow the player.
 *
 * <p>While any decoys exist, each enemy follows the nearest decoy instead.
 *
 * @see DecoyComponent
 */
public class EnemySystem extends EntitySystem {
  private ImmutableArray<Entity> players;
  private ImmutableArray<Entity> decoys;
  private ImmutableArray<Entity> enemies;

  @Override
  public void addedToEngine(Engine engine) {
    players = engine.getEntitiesFor(
        Family.all(PlayerComponent.class, TransformComponent.class).get()
    );

    decoys = engine.getEntitiesFor(
        Family.all(DecoyComponent.class, TransformComponent.class).get()
    );

    enemies = engine.getEntitiesFor(
        Family.all(EnemyComponent.class, TransformComponent.class, VelocityComponent.class).get()
    );
  }

  @Override
  public void update(float deltaTime) {
    if (players.size() == 0 && decoys.size() == 0) {
      return;
    }

    TransformComponent playerTransform = players.size() > 0
        ? TransformComponent.MAPPER.get(players.first()) : null;

    for (int i = 0; i < enemies.size(); ++i) {
      Entity enemy = enemies.get(i);

      // While being knocked back, keep the knockback velocity set by CollisionSystem
      EnemyComponent enemyComponent = EnemyComponent.MAPPER.get(enemy);
      if (enemyComponent.isKnockedBack()) {
        enemyComponent.knockbackTimer -= deltaTime;
        continue;
      }

      TransformComponent enemyTransform = TransformComponent.MAPPER.get(enemy);
      VelocityComponent enemyVelocity = VelocityComponent.MAPPER.get(enemy);

      TransformComponent targetTransform = decoys.size() > 0
          ? nearestDecoy(enemyTransform) : playerTransform;

      float dirX = targetTransform.position.x - enemyTransform.position.x;
      float dirY = targetTransform.position.y - enemyTransform.position.y;

      Vector2 direction = new Vector2(dirX, dirY);
      if (!direction.isZero()) {
        direction.nor().scl(enemyVelocity.effectiveMaxSpeed());
      }
      enemyVelocity.velocity.set(direction.x, direction.y);
    }
  }

  /**
   * Returns the decoy closest to an enemy.
   *
   * @param enemyTransform The enemy's transform.
   * @return The transform of the nearest decoy. Only call while decoys exist.
   */
  private TransformComponent nearestDecoy(TransformComponent enemyTransform) {
    TransformComponent nearest = null;
    float nearestDistance = Float.MAX_VALUE;
    for (int i = 0; i < decoys.size(); ++i) {
      TransformComponent decoy = TransformComponent.MAPPER.get(decoys.get(i));
      float dx = decoy.position.x - enemyTransform.position.x;
      float dy = decoy.position.y - enemyTransform.position.y;
      float distance = dx * dx + dy * dy;
      if (distance < nearestDistance) {
        nearestDistance = distance;
        nearest = decoy;
      }
    }
    return nearest;
  }
}
