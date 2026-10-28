package com.eng1c4t3.goosesurvivor.systems;

import com.eng1c4t3.goosesurvivor.Engine;
import com.eng1c4t3.goosesurvivor.Entity;

/**
 * The EntitySystem class represents a system that operates on {@link Entity} in the game.
 *
 * @see Engine
 * @see Entity
 */
public abstract class EntitySystem {
  protected Engine engine;

  /**
   * Sets the engine for this system.
   *
   * @param engine The {@link Engine} to set for this system.
   */
  public void setEngine(Engine engine) {
    this.engine = engine;
  }

  /**
   * The core logic for this system.
   *
   * @param deltaTime The time passed since the last frame.
   */
  public abstract void update(float deltaTime);
}
