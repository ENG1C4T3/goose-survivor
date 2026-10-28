package com.eng1c4t3.goosesurvivor;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.eng1c4t3.goosesurvivor.components.PlayerComponent;
import com.eng1c4t3.goosesurvivor.components.TextureComponent;
import com.eng1c4t3.goosesurvivor.components.TransformComponent;
import com.eng1c4t3.goosesurvivor.components.VelocityComponent;
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

  private AssetManager assetManager;

  /**
   * Initializes the game.
   */
  @Override
  public void create() {
    engine = new Engine();
    batch = new SpriteBatch();
    camera = new OrthographicCamera();
    camera.setToOrtho(false, 800, 480);

    assetManager = new AssetManager();
    assetManager.load("libgdx.png", Texture.class);

    // Forces the manager to finish loading everything before the game starts
    assetManager.finishLoading();

    Texture playerTexture = assetManager.get("libgdx.png", Texture.class);
    TextureRegion playerRegion = new TextureRegion(playerTexture);
    registerSystems();
    createPlayer(playerRegion);
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
   * Creates the player entity.
   *
   * @param region The texture region for the player.
   */
  private void createPlayer(TextureRegion region) {
    Entity player = new Entity();
    player.addComponent(new PlayerComponent());
    player.addComponent(new TransformComponent(10, 10));
    player.addComponent(new VelocityComponent(200f));
    player.addComponent(new TextureComponent(region));
    engine.addEntity(player);
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
    assetManager.dispose();
  }
}