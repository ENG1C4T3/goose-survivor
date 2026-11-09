package com.eng1c4t3.goosesurvivor;

import com.badlogic.ashley.core.Engine;
import com.eng1c4t3.goosesurvivor.systems.InputSystem;
import com.eng1c4t3.goosesurvivor.systems.MovementSystem;

/**
 * Manages a playthrough's world, timer, game state and gameplay updates.
 */
public class GameSession {
  private static final float TIME_LIMIT = 300f;
  private SessionState gameState = SessionState.RUNNING;
  private float elapsedTime = 0f;

  private final Engine engine;
  private final GameWorld world;

  /**
   * Creates a new session with its engine, world and gameplay system.
   * In order to make sure that the entity added by world could enter Session's
   * Engine
   */
  public GameSession() {
    this.engine = new Engine();
    this.world = new GameWorld(engine);

    InputSystem inputSystem = new InputSystem();
    inputSystem.priority = 0;

    MovementSystem movementSystem = new MovementSystem();
    movementSystem.priority = 1;

    engine.addSystem(inputSystem);
    engine.addSystem(movementSystem);
  }

  public GameWorld getWorld() {
    return world;
  }

  public SessionState getState() {
    return gameState;
  }

  public float getRemainingTime() {
    return Math.max(0f, TIME_LIMIT - elapsedTime);
  }

  /**
   * Pauses the session if session is running.
   */
  public void pause() {
    if (gameState == SessionState.RUNNING) {
      gameState = SessionState.PAUSED;
    }
  }

  /**
   * Resumes the session if session is paused.
   */
  public void resume() {
    if (gameState == SessionState.PAUSED) {
      gameState = SessionState.RUNNING;
    }
  }

  /**
   * Advance timer while game is running.
   *
   * @param deltaTime elapes time in seconds since previous update
   * @throws IllegalArgumentException if deltaTime is negative or not finite
   */
  public void update(float deltaTime) {
    if (!Float.isFinite(deltaTime) || deltaTime < 0f) {
      throw new IllegalArgumentException(
          "Delta time must be finite and non-negative");
    }
    if (gameState != SessionState.RUNNING) {
      return;
    }

    elapsedTime += deltaTime;

    if (elapsedTime >= TIME_LIMIT) {
      elapsedTime = TIME_LIMIT;
      lose();
      return;
    }

    engine.update(deltaTime);

  }

  /**
   * Marks the session as won if session running.
   */
  public void win() {
    if (gameState == SessionState.RUNNING) {
      gameState = SessionState.WON;
    }
  }

  /**
   * Marks the session as lose if session running.
   */
  public void lose() {
    if (gameState == SessionState.RUNNING) {
      gameState = SessionState.LOST;
    }
  }
}