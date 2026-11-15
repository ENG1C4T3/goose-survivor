package com.eng1c4t3.goosesurvivor;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.eng1c4t3.goosesurvivor.components.EnemyComponent;
import com.eng1c4t3.goosesurvivor.components.HealthComponent;
import com.eng1c4t3.goosesurvivor.components.PlayerComponent;
import com.eng1c4t3.goosesurvivor.components.TransformComponent;
import com.eng1c4t3.goosesurvivor.entities.EntityFactory;
import com.eng1c4t3.goosesurvivor.systems.CollisionSystem;
import com.eng1c4t3.goosesurvivor.systems.DecoySystem;
import com.eng1c4t3.goosesurvivor.systems.EnemySpawnSystem;
import com.eng1c4t3.goosesurvivor.systems.EnemySystem;
import com.eng1c4t3.goosesurvivor.systems.EventCleanupSystem;
import com.eng1c4t3.goosesurvivor.systems.FoxSystem;
import com.eng1c4t3.goosesurvivor.systems.HealthSystem;
import com.eng1c4t3.goosesurvivor.systems.HiddenEventSystem;
import com.eng1c4t3.goosesurvivor.systems.InputSystem;
import com.eng1c4t3.goosesurvivor.systems.InteractionSystem;
import com.eng1c4t3.goosesurvivor.systems.InventorySystem;
import com.eng1c4t3.goosesurvivor.systems.MovementSystem;
import com.eng1c4t3.goosesurvivor.systems.ProjectileSystem;
import com.eng1c4t3.goosesurvivor.systems.ShooterSystem;
import com.eng1c4t3.goosesurvivor.systems.SpeedBoostSystem;
import java.util.function.Function;
import java.util.function.UnaryOperator;

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

    InteractionSystem interactionSystem = new InteractionSystem(stats);
    interactionSystem.priority = 1;

    InventorySystem inventorySystem = new InventorySystem(stats);
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
    FoxSystem foxSystem = new FoxSystem(GameScreen.WORLD_WIDTH, GameScreen.WORLD_HEIGHT);
    foxSystem.priority = 4;
    engine.addSystem(foxSystem);
    engine.addSystem(movementSystem);
    engine.addSystem(collisionSystem);
    engine.addSystem(projectileSystem);
    engine.addSystem(healthSystem);
    engine.addSystem(new EventCleanupSystem());

    collisionSystem.addCollisionListener(projectileSystem);

    registerPlayerDeathListener();
    registerEnemyDeathListener();
  }

  /**
   * Enables placeable decoys, such as popcorn, using textures loaded by the screen.
   *
   * @param textures Returns the texture region for a texture file name.
   * @throws IllegalStateException if decoys have already been configured.
   */
  public void configureDecoys(Function<String, TextureRegion> textures) {
    if (engine.getSystem(DecoySystem.class) != null) {
      throw new IllegalStateException("Decoys have already been configured");
    }
    engine.addSystem(new DecoySystem(textures));
  }

  /**
   * Enables shooting using a projectile texture loaded by the screen.
   *
   * @param projectileRegion The texture region for projectile sprites.
   * @throws IllegalArgumentException if the texture region is null.
   * @throws IllegalStateException    if shooting has already been configured.
   */
  public void configureShooting(TextureRegion projectileRegion) {
    configureShooting(projectileRegion, null);
  }

  /**
   * Enables shooting, also letting projectiles with their own texture (e.g. the launchers'
   * nuts and seeds) look it up.
   *
   * @param projectileRegion The default projectile texture.
   * @param textures         Returns the texture region for a texture file name, or null.
   */
  public void configureShooting(TextureRegion projectileRegion,
      Function<String, TextureRegion> textures) {
    if (projectileRegion == null) {
      throw new IllegalArgumentException(
          "Projectile texture must not be null");
    }
    if (engine.getSystem(ShooterSystem.class) != null) {
      throw new IllegalStateException("Shooting has already been configured");
    }

    ShooterSystem shooterSystem = new ShooterSystem(projectileRegion);
    shooterSystem.setTextures(textures);
    shooterSystem.priority = 5;
    engine.addSystem(shooterSystem);
  }

  /**
   * Enables hidden events using the supplied Long Boi texture.
   *
   * @param bossRegion   The texture region for Long Boi.
   * @param textures     Returns the texture region for a texture file name; used for the
   *                     entities spawners create.
   * @throws IllegalStateException if hidden events are already configured
   */
  public void configureHiddenEvents(TextureRegion bossRegion,
      Function<String, TextureRegion> textures) {
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

    hiddenEventSystem.setTextures(textures);
    hiddenEventSystem.setOnDoorOpened(this::win);
    hiddenEventSystem.priority = 8;
    engine.addSystem(hiddenEventSystem);
  }

  /**
   * Enables timed goose spawning at the world's edges (see {@link EnemySpawnSystem}).
   *
   * @param textures Returns the texture region for a texture file name.
   * @throws IllegalStateException if enemy spawning has already been configured.
   */
  public void configureEnemySpawning(Function<String, TextureRegion> textures) {
    if (engine.getSystem(EnemySpawnSystem.class) != null) {
      throw new IllegalStateException("Enemy spawning has already been configured");
    }
    EnemySpawnSystem spawnSystem = new EnemySpawnSystem(
        GameScreen.WORLD_WIDTH, GameScreen.WORLD_HEIGHT, textures);
    spawnSystem.priority = 3;
    engine.addSystem(spawnSystem);
  }

  /**
   * Makes the player's aim follow the mouse.
   *
   * @param screenToWorld Converts a screen position to world coordinates in place, e.g.
   *                      {@code viewport::unproject}.
   */
  public void configureMouseAim(UnaryOperator<Vector2> screenToWorld) {
    engine.getSystem(InputSystem.class).setScreenToWorld(screenToWorld);
  }

  /**
   * Returns the session's engine, for tests in this package.
   *
   * @return The engine.
   */
  Engine getEngine() {
    return engine;
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

  /**
   * Returns this playthrough's score so far. The win and time bonuses only count once the
   * session has been won.
   *
   * @return The current score.
   */
  public Score getScore() {
    return Score.of(stats, gameState == SessionState.WON, getRemainingTime());
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

  /**
   * Records each enemy that is removed because it ran out of health.
   */
  private void registerEnemyDeathListener() {
    Family enemyFamily = Family.all(EnemyComponent.class, HealthComponent.class).get();
    engine.addEntityListener(enemyFamily, new EntityListener() {
      @Override
      public void entityAdded(Entity entity) {
        // Spawning an enemy does not change the stats.
      }

      @Override
      public void entityRemoved(Entity entity) {
        EnemyComponent enemy = EnemyComponent.MAPPER.get(entity);
        HealthComponent health = HealthComponent.MAPPER.get(entity);
        if (enemy.type != null && health.isDead()) {
          stats.recordEnemyKilled(enemy.type);
        }
      }
    });
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
