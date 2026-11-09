package com.eng1c4t3.goosesurvivor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.eng1c4t3.goosesurvivor.entities.EntityFactory;

/**
 * Handles gameplay presentation and game session controls.
 */
public class GameScreen extends ScreenAdapter {
  private final GameSession session;

  private static final float WORLD_WIDTH = 800f;
  private static final float WORLD_HEIGHT = 480f;

  private final SpriteBatch batch;
  private final OrthographicCamera camera;
  private final FitViewport viewport;
  private final TextureManager textureManager;
  private final WorldRenderer renderer;

  /**
   * Creates a gameplay screen for the given session, initializes its player.
   *
   * @param session the new session displayed by this screen
   *
   * @throws IllegalArgumentException if session is null or its world is not
   *                                  empty.
   */
  public GameScreen(GameSession session) {
    if (session == null) {
      throw new IllegalArgumentException(
          "Session must not be null");
    }
    if (session.getWorld().getEntities().size() != 0) {
      throw new IllegalArgumentException(
          "New game screen requires empty world");
    }
    this.session = session;

    batch = new SpriteBatch();
    camera = new OrthographicCamera();
    viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

    textureManager = new TextureManager();
    textureManager.loadTextures();
    textureManager.finishLoading();

    renderer = new WorldRenderer(batch, camera);
    session.getWorld().addEntity(
        (EntityFactory.createPlayer(
            textureManager.getTextureRegion(
                TextureManager.PLAYER_TEXTURE))));
  }

  /**
   * Update the viewport to fit the window.
   *
   * @param width  window width
   * @param height window height
   * @see com.badlogic.gdx.ScreenAdapter#resize(int, int)
   */
  @Override
  public void resize(int width, int height) {
    viewport.update(width, height, true);
  }

  /**
   * Handles pause and resume.
   * press "P" to pause or resume.
   */
  private void handleSessionInput() {
    if (!Gdx.input.isKeyJustPressed(Input.Keys.P)) {
      return;
    }

    if (session.getState() == SessionState.RUNNING) {
      session.pause();
    } else if (session.getState() == SessionState.PAUSED) {
      session.resume();
    }
  }

  /**
   * Handles screen controls and advance the session, draws the world.
   *
   * @param deltaTime elapsed time since the previous frame
   */
  @Override
  public void render(float deltaTime) {
    handleSessionInput();
    session.update(deltaTime);

    Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    viewport.apply();
    renderer.render(session.getWorld());
  }

  @Override
  public void pause() {
    session.pause();
  }

  @Override
  public void dispose() {
    batch.dispose();
    textureManager.dispose();
  }

}
