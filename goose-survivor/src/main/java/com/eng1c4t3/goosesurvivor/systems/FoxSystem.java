package com.eng1c4t3.goosesurvivor.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.eng1c4t3.goosesurvivor.components.FoxComponent;
import com.eng1c4t3.goosesurvivor.components.PlayerComponent;
import com.eng1c4t3.goosesurvivor.components.TransformComponent;
import com.eng1c4t3.goosesurvivor.components.VelocityComponent;

/**
 * Steers foxes: a wild fox runs away from the player when they get close, and a tame fox
 * follows the player. Foxes are kept inside the world bounds, so a wild fox can be cornered.
 *
 * <p>Must run before {@link MovementSystem}.
 *
 * @see FoxComponent
 */
public class FoxSystem extends EntitySystem {
  private final float worldWidth;
  private final float worldHeight;
  private final Vector2 direction = new Vector2();
  private ImmutableArray<Entity> players;
  private ImmutableArray<Entity> foxes;

  /**
   * Creates a fox system.
   *
   * @param worldWidth  Width of the area foxes are kept inside.
   * @param worldHeight Height of the area foxes are kept inside.
   */
  public FoxSystem(float worldWidth, float worldHeight) {
    this.worldWidth = worldWidth;
    this.worldHeight = worldHeight;
  }

  @Override
  public void addedToEngine(Engine engine) {
    players = engine.getEntitiesFor(
        Family.all(PlayerComponent.class, TransformComponent.class).get());
    foxes = engine.getEntitiesFor(Family.all(
        FoxComponent.class, TransformComponent.class, VelocityComponent.class).get());
  }

  @Override
  public void update(float deltaTime) {
    Rectangle player = players.size() > 0
        ? TransformComponent.MAPPER.get(players.first()).position : null;

    for (int i = 0; i < foxes.size(); ++i) {
      Entity fox = foxes.get(i);
      FoxComponent foxComponent = FoxComponent.MAPPER.get(fox);
      Rectangle bounds = TransformComponent.MAPPER.get(fox).position;
      VelocityComponent velocity = VelocityComponent.MAPPER.get(fox);

      direction.setZero();
      if (player != null) {
        direction.set(
            player.x + player.width / 2 - (bounds.x + bounds.width / 2),
            player.y + player.height / 2 - (bounds.y + bounds.height / 2));
        float distance = direction.len();
        if (foxComponent.tamed) {
          velocity.maxSpeed = FoxComponent.FOLLOW_SPEED;
          if (distance <= FoxComponent.FOLLOW_DISTANCE) {
            direction.setZero();
          }
        } else {
          velocity.maxSpeed = FoxComponent.FLEE_SPEED;
          if (distance >= FoxComponent.FLEE_RADIUS) {
            direction.setZero();
          } else {
            direction.scl(-1f);
            if (direction.isZero()) {
              direction.set(1f, 0f);
            }
          }
        }
      }
      if (!direction.isZero()) {
        direction.nor().scl(velocity.effectiveMaxSpeed());
      }
      velocity.velocity.set(direction);
      keepInsideWorld(bounds, velocity);
    }
  }

  /**
   * Clamps the fox to the world and stops any movement that would take it outside.
   */
  private void keepInsideWorld(Rectangle bounds, VelocityComponent velocity) {
    float maxX = Math.max(0f, worldWidth - bounds.width);
    float maxY = Math.max(0f, worldHeight - bounds.height);
    bounds.x = Math.min(Math.max(bounds.x, 0f), maxX);
    bounds.y = Math.min(Math.max(bounds.y, 0f), maxY);
    if ((bounds.x <= 0f && velocity.velocity.x < 0f)
        || (bounds.x >= maxX && velocity.velocity.x > 0f)) {
      velocity.velocity.x = 0f;
    }
    if ((bounds.y <= 0f && velocity.velocity.y < 0f)
        || (bounds.y >= maxY && velocity.velocity.y > 0f)) {
      velocity.velocity.y = 0f;
    }
  }
}
