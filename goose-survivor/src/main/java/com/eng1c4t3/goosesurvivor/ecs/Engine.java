package com.eng1c4t3.goosesurvivor.ecs;

import java.util.ArrayList;
import java.util.List;

/**
 * The Engine class manages entities and systems in the game.
 *
 * @see EntitySystem
 * @see Entity
 */
public class Engine {
  private final List<Entity> entities;
  private final List<EntitySystem> systems;

  /**
   * Constructs a new Engine instance.
   */
  public Engine() {
    this.entities = new ArrayList<>();
    this.systems = new ArrayList<>();
  }

  /**
   * Used to add an {@link Entity} to the engine.
   *
   * @param entity The {@link Entity} to add.
   */
  public void addEntity(Entity entity) {
    entities.add(entity);
  }

  /**
   * Used to remove an {@link Entity} from the engine.
   *
   * @param entity The {@link Entity} to remove.
   */
  public void removeEntity(Entity entity) {
    entities.remove(entity);
  }

  /**
   *  Used by {@link EntitySystem} to grab all {@link Entity}s and filter the ones they want.
   *
   * @return List of {@link Entity}s owned by the engine.
   */
  public List<Entity> getEntities() {
    return entities;
  }

  /**
   * Adds a {@link EntitySystem} to the engine's system list and sets the engine for the system.
   *
   * @param system The {@link EntitySystem} to add to the engine.
   */
  public void addSystem(EntitySystem system) {
    systems.add(system);
    system.setEngine(this);
  }

  /**
   * Updates all registered {@link EntitySystem} within the engine.
   * Each system's update method is invoked with the provided delta time.
   *
   * @param deltaTime The elapsed time since the last update.
   */
  public void update(float deltaTime) {
    for (EntitySystem system : systems) {
      system.update(deltaTime);
    }
  }
}
