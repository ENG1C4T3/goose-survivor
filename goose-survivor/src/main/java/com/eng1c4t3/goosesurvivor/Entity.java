package com.eng1c4t3.goosesurvivor;

import com.eng1c4t3.goosesurvivor.components.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents an entity in the game.
 */
public class Entity {
  private final UUID id;
  private final Map<Class<? extends Component>, Component> components;

  /**
   * Constructs a new Entity instance.
   */
  public Entity() {
    this.id = UUID.randomUUID();
    this.components = new HashMap<>();
  }

  /**
   * Returns the unique identifier of this entity.
   *
   * @return The {@link UUID} of this entity.
   */
  public UUID getId() {
    return id;
  }

  /**
   * Adds a {@link Component} to this entity.
   *
   * @param component The {@link Component} to add.
   */
  public <T extends Component> void addComponent(T component) {
    components.put(component.getClass(), component);
  }

  /**
   * Removes a {@link Component} from this entity.
   *
   * @param componentClass The {@link Class} of the {@link Component} to remove.
   */
  public void removeComponent(Class<? extends Component> componentClass) {
    components.remove(componentClass);
  }

  /**
   * Retrieves a {@link Component} of the specified type from this entity.
   *
   * @param <T> The type of the {@link Component} to retrieve.
   * @param componentClass The {@link Class} object representing the type of the {@link Component}.
   * @return The {@link Component} instance of the specified type, or null if this entity does not
   *        contain a component of that type.
   */
  public <T extends Component> T getComponent(Class<T> componentClass) {
    return componentClass.cast(components.get(componentClass));
  }

  /**
   * Checks if this entity contains a {@link Component} of the specified type.
   *
   * @param componentClass The {@link Class} object representing the type of the
   *        {@link Component} to check for.
   * @return {@code true} if this entity contains a component of the specified
   *        type, {@code false} otherwise.
   */
  public boolean hasComponent(Class<? extends Component> componentClass) {
    return components.containsKey(componentClass);
  }
}
