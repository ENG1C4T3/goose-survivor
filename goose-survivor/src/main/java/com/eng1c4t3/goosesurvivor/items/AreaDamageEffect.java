package com.eng1c4t3.goosesurvivor.items;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Rectangle;
import com.eng1c4t3.goosesurvivor.components.EnemyComponent;
import com.eng1c4t3.goosesurvivor.components.HealthComponent;
import com.eng1c4t3.goosesurvivor.components.TransformComponent;

/**
 * Damages every enemy whose centre is within a radius of the user's centre.
 *
 * <p>Enemies killed this way are removed by the HealthSystem as usual.
 */
public class AreaDamageEffect implements ItemEffect {

  private static final Family ENEMIES = Family.all(
      EnemyComponent.class, HealthComponent.class, TransformComponent.class).get();

  /** Distance from the user's centre that enemies are hit within. */
  public final float radius;
  /** Damage dealt to each enemy hit. */
  public final float damage;

  /**
   * Constructs a new AreaDamageEffect.
   *
   * @param radius Distance from the user's centre that enemies are hit within.
   * @param damage Damage dealt to each enemy hit.
   */
  public AreaDamageEffect(float radius, float damage) {
    this.radius = radius;
    this.damage = damage;
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
      float dx = bounds.x + bounds.width / 2 - cx;
      float dy = bounds.y + bounds.height / 2 - cy;
      if (dx * dx + dy * dy <= radius * radius) {
        HealthComponent.MAPPER.get(enemy).takeDamage(damage);
      }
    }
  }
}
