package com.eng1c4t3.goosesurvivor;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.eng1c4t3.goosesurvivor.entities.EntityFactory;
import com.eng1c4t3.goosesurvivor.systems.CollisionSystem;
import com.eng1c4t3.goosesurvivor.systems.EnemySystem;
import com.eng1c4t3.goosesurvivor.systems.EventCleanupSystem;
import com.eng1c4t3.goosesurvivor.systems.HealthSystem;
import com.eng1c4t3.goosesurvivor.systems.InputSystem;
import com.eng1c4t3.goosesurvivor.systems.InteractionSystem;
import com.eng1c4t3.goosesurvivor.systems.MovementSystem;
import com.eng1c4t3.goosesurvivor.systems.RenderSystem;

/**
 * Represents the main game class.
 */
public class GooseSurvivor extends ApplicationAdapter {

  /**
   * The entry point of the application. Configures and starts the game.
   *
   * @param args The command-line arguments passed to the application.
   */
  public static void main(String[] args) {
    Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
    config.setTitle("Goose Survivor");
    config.setWindowedMode(800, 480);
    config.setForegroundFPS(60);
    config.useVsync(true);
    new Lwjgl3Application(new GooseSurvivor(), config);
  }

  private Engine engine;
  private SpriteBatch batch;
  private OrthographicCamera camera;
  public static TextureManager textureManager = new TextureManager();
  private FPSLogger fpsloger;

  /**
   * Initialises the game.
   */
  @Override
  public void create() {
    engine = new Engine();
    batch = new SpriteBatch();
    camera = new OrthographicCamera();
    camera.setToOrtho(false, 800, 480);
    fpsloger = new FPSLogger();

    textureManager.loadTextures();
    textureManager.finishLoading();


    registerSystems();
    engine.addEntity(EntityFactory.createPlayer(255, 255));
    engine.addEntity(EntityFactory.createBlock(100, 100, 50, 50));
    engine.addEntity(EntityFactory.createHealthPack(300, 200, 25));
    engine.addEntity(EntityFactory.createEnemy(EntityFactory.EnemyType.FAST_GOOSE, 700, 700));
    engine.addEntity(EntityFactory.createEnemy(EntityFactory.EnemyType.LONG_BOI, 400, 400));
  }

  /**
   * Registers the game systems.
   */
  private void registerSystems() {
    engine.addSystem(new InputSystem());
    // Must come after InputSystem, which creates the trigger events it handles
    engine.addSystem(new InteractionSystem());
    engine.addSystem(new MovementSystem());
    engine.addSystem(new CollisionSystem());
    engine.addSystem(new RenderSystem(batch, camera));
    engine.addSystem(new HealthSystem());
    engine.addSystem(new EnemySystem());
    // Runs last regardless of order (highest priority value): destroys this frame's events
    engine.addSystem(new EventCleanupSystem());
  }

  /**
   * Updates the game state.
   */
  @Override
  public void render() {
    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    engine.update(Gdx.graphics.getDeltaTime());
    fpsloger.log();
  }

  /**
   * Disposes of the game resources.
   */
  @Override
  public void dispose() {
    batch.dispose();
    textureManager.dispose();
  }
}
