package com.eng1c4t3.goosesurvivor.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

/**
 * Represents a collision component for entities in the game.
 */
public class CollisionComponent implements Component {
  public static final ComponentMapper<CollisionComponent> MAPPER =
      ComponentMapper.getFor(CollisionComponent.class);

  /**
   * Represents the type of collision for an entity.
   */
  public enum Type {
    SOLID,
    TRIGGER,
  }

  public Type type;

  /**
   * Constructs a new CollisionComponent with the specified collision type.
   *
   * @param type The {@link Type} that defines the collision behaviour of the entity.
   */
  public CollisionComponent(Type type) {
    this.type = type;
  }
}
