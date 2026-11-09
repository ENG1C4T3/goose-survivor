package com.eng1c4t3.goosesurvivor.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.eng1c4t3.goosesurvivor.components.EnemyComponent;
import com.eng1c4t3.goosesurvivor.components.PlayerComponent;
import com.eng1c4t3.goosesurvivor.components.TransformComponent;
import com.eng1c4t3.goosesurvivor.components.VelocityComponent;

/**
 * System that manages enemy behavior and directs enemies to follow the player.
 */
public class EnemySystem extends EntitySystem {
  private ImmutableArray<Entity> players;
  private ImmutableArray<Entity> enemies;

  @Override
  public void addedToEngine(Engine engine) {
    players = engine.getEntitiesFor(
        Family.all(PlayerComponent.class, TransformComponent.class).get()
    );

    enemies = engine.getEntitiesFor(
        Family.all(EnemyComponent.class, TransformComponent.class, VelocityComponent.class).get()
    );
  }

  @Override
  public void update(float deltaTime) {
    if (players.size() == 0) {
      return;
    }

    Entity player = players.first();
    TransformComponent playerTransform = TransformComponent.MAPPER.get(player);

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

      float dirX = playerTransform.position.x - enemyTransform.position.x;
      float dirY = playerTransform.position.y - enemyTransform.position.y;

      Vector2 direction = new Vector2(dirX, dirY);
      if (!direction.isZero()) {
        direction.nor().scl(enemyVelocity.maxSpeed);
      }
      enemyVelocity.velocity.set(direction.x, direction.y);
    }
  }
}
