package com.eng1c4t3.goosesurvivor;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.eng1c4t3.goosesurvivor.components.HealthComponent;
import com.eng1c4t3.goosesurvivor.components.PlayerComponent;
import com.eng1c4t3.goosesurvivor.components.TransformComponent;
import com.eng1c4t3.goosesurvivor.entities.EntityFactory;
import com.eng1c4t3.goosesurvivor.systems.CollisionSystem;
import com.eng1c4t3.goosesurvivor.systems.EnemySystem;
import com.eng1c4t3.goosesurvivor.systems.EventCleanupSystem;
import com.eng1c4t3.goosesurvivor.systems.HealthSystem;
import com.eng1c4t3.goosesurvivor.systems.HiddenEventSystem;
import com.eng1c4t3.goosesurvivor.systems.InputSystem;
import com.eng1c4t3.goosesurvivor.systems.InteractionSystem;
import com.eng1c4t3.goosesurvivor.systems.InventorySystem;
import com.eng1c4t3.goosesurvivor.systems.MovementSystem;
import com.eng1c4t3.goosesurvivor.systems.ProjectileSystem;
import com.eng1c4t3.goosesurvivor.systems.ShooterSystem;
import com.eng1c4t3.goosesurvivor.systems.SpeedBoostSystem;

/**
 * Manages a playthrough's world, timer, game state and gameplay updates.
 */
public class GameSession {
  private static final float TIME_LIMIT = 300f;
  private SessionState gameState = SessionState.RUNNING;
  private float elapsedTime = 0f;
  private final SessionStats stats = new SessionStats();

  private final Engine engine;
  private final GameWorld world;

  /**
   * Creates a session with its engine, world and gameplay systems.
   */
  public GameSession() {
    this.engine = new Engine();
    this.world = new GameWorld(engine);

    InputSystem inputSystem = new InputSystem();
    inputSystem.priority = 0;

    InteractionSystem interactionSystem = new InteractionSystem();
    interactionSystem.priority = 1;

    InventorySystem inventorySystem = new InventorySystem();
    inventorySystem.priority = 2;

    SpeedBoostSystem speedBoostSystem = new SpeedBoostSystem();
    speedBoostSystem.priority = 3;

    EnemySystem enemySystem = new EnemySystem();
    enemySystem.priority = 4;

    MovementSystem movementSystem = new MovementSystem();
    movementSystem.priority = 6;

    CollisionSystem collisionSystem = new CollisionSystem();
    collisionSystem.priority = 7;

    ProjectileSystem projectileSystem = new ProjectileSystem();
    projectileSystem.priority = 8;

    HealthSystem healthSystem = new HealthSystem();
    healthSystem.priority = 9;

    engine.addSystem(inputSystem);
    engine.addSystem(interactionSystem);
    engine.addSystem(inventorySystem);
    engine.addSystem(speedBoostSystem);
    engine.addSystem(enemySystem);
    engine.addSystem(movementSystem);
    engine.addSystem(collisionSystem);
    engine.addSystem(projectileSystem);
    engine.addSystem(healthSystem);
    engine.addSystem(new EventCleanupSystem());

    collisionSystem.addCollisionListener(projectileSystem);

    registerPlayerDeathListener();
  }

  /**
   * Enables shooting using a projectile texture loaded by the screen.
   *
   * @param projectileRegion The texture region for projectile sprites.
   * @throws IllegalArgumentException if the texture region is null.
   * @throws IllegalStateException    if shooting has already been configured.
   */
  public void configureShooting(TextureRegion projectileRegion) {
    if (projectileRegion == null) {
      throw new IllegalArgumentException(
          "Projectile texture must not be null");
    }
    if (engine.getSystem(ShooterSystem.class) != null) {
      throw new IllegalStateException("Shooting has already been configured");
    }

    ShooterSystem shooterSystem = new ShooterSystem(projectileRegion);
    shooterSystem.priority = 5;
    engine.addSystem(shooterSystem);
  }

  /**
   * Enables hidden events using the supplied Long Boi texture.
   *
   * @param bossRegion The texture region for Long Boi.
   * @throws IllegalStateException if hidden events are already configured
   */
  public void configureHiddenEvents(TextureRegion bossRegion) {
    if (engine.getSystem(HiddenEventSystem.class) != null) {
      throw new IllegalStateException("Hidden events have already been configured");
    }

    HiddenEventSystem hiddenEventSystem = new HiddenEventSystem(stats, statue -> {
      TransformComponent transform = TransformComponent.MAPPER.get(statue);
      return EntityFactory.createEnemy(
          EntityFactory.EnemyType.LONG_BOI,
          transform.position.x + transform.position.width + 16f,
          transform.position.y,
          bossRegion);
    });

    hiddenEventSystem.priority = 8;
    engine.addSystem(hiddenEventSystem);
  }

  public GameWorld getWorld() {
    return world;
  }

  /**
   * Return this session's gameplay statistics.
   *
   * @return The statistics for this playthrough
   */
  public SessionStats getStats() {
    return stats;
  }

  public SessionState getState() {
    return gameState;
  }

  public float getElapsedTime() {
    return elapsedTime;
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

  private void registerPlayerDeathListener() {
    Family playerFamily = Family.all(
        PlayerComponent.class, HealthComponent.class).get();
    EntityListener playerListener = new EntityListener() {
      @Override
      public void entityAdded(Entity entity) {

      }

      @Override
      public void entityRemoved(Entity entity) {
        HealthComponent health = HealthComponent.MAPPER.get(entity);
        if (health != null && health.isDead()) {
          lose();
        }
      }
    };

    engine.addEntityListener(playerFamily, playerListener);
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
