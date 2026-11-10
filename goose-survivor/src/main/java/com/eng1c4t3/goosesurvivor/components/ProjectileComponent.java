package com.eng1c4t3.goosesurvivor.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.eng1c4t3.goosesurvivor.systems.ProjectileSystem;

/**
 * Marks an entity as a projectile.
 *
 * <p>A projectile is destroyed when it:
 * <ul>
 *   <li>hits an entity with health that is not its {@link #owner}, damaging that entity;</li>
 *   <li>hits a solid entity (e.g. a wall); or</li>
 *   <li>slows down below {@link #minSpeed} due to its {@link #deceleration}.</li>
 * </ul>
 *
 * @see ProjectileSystem
 */
public class ProjectileComponent implements Component {
  public static final ComponentMapper<ProjectileComponent> MAPPER =
      ComponentMapper.getFor(ProjectileComponent.class);

  /** The entity that fired the projectile; it can never be hit by its own projectile. */
  public Entity owner;
  /** Damage dealt to the entity the projectile hits. */
  public float baseDamage;
  /** Speed lost per second, in units per second squared. */
  public float deceleration;
  /** The projectile despawns once its speed drops below this value. */
  public float minSpeed;

  /**
   * Constructs a new ProjectileComponent.
   *
   * @param owner The entity that fired the projectile.
   * @param baseDamage Damage dealt to the entity the projectile hits.
   * @param deceleration Speed lost per second.
   * @param minSpeed The speed below which the projectile despawns.
   */
  public ProjectileComponent(Entity owner, float baseDamage, float deceleration,
                             float minSpeed) {
    this.owner = owner;
    this.baseDamage = baseDamage;
    this.deceleration = deceleration;
    this.minSpeed = minSpeed;
  }
}
