package com.eng1c4t3.goosesurvivor;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.eng1c4t3.goosesurvivor.systems.InputSystem;
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
  private TextureManager textureManager;

  /**
   * Initializes the game.
   */
  @Override
  public void create() {
    engine = new Engine();
    batch = new SpriteBatch();
    camera = new OrthographicCamera();
    camera.setToOrtho(false, 800, 480);

    textureManager = new TextureManager();
    textureManager.loadTextures();
    textureManager.finishLoading();

    registerSystems();
    engine.addEntity(EntityFactory.createPlayer(
        textureManager.getTextureRegion(TextureManager.PLAYER_TEXTURE)));
  }

  /**
   * Registers the game systems.
   */
  private void registerSystems() {
    engine.addSystem(new InputSystem());
    engine.addSystem(new MovementSystem());
    engine.addSystem(new RenderSystem(batch, camera));
  }

  /**
   * Updates the game state.
   */
  @Override
  public void render() {
    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    engine.update(Gdx.graphics.getDeltaTime());
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