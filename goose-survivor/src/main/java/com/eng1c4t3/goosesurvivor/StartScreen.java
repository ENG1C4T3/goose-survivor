package com.eng1c4t3.goosesurvivor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.eng1c4t3.goosesurvivor.ui.Menu;
import com.eng1c4t3.goosesurvivor.ui.MenuInput;
import com.eng1c4t3.goosesurvivor.ui.MenuRenderer;

/**
 * Title screen shown when the game launches, offering to start a game or quit.
 */
public class StartScreen extends ScreenAdapter {
  /** Actions offered by the start menu. */
  private enum StartAction {
    PLAY,
    QUIT
  }

  private final GooseSurvivor game;
  private final SpriteBatch batch;
  private final FitViewport viewport;
  private final MenuRenderer menuRenderer;
  private final MenuInput menuInput;
  private final Menu<StartAction> menu;
  private boolean disposed = false;

  /**
   * Creates the start screen.
   *
   * @param game the game that owns this screen, used to start a session
   * @throws IllegalArgumentException if game is null
   */
  public StartScreen(GooseSurvivor game) {
    if (game == null) {
      throw new IllegalArgumentException("Game must not be null");
    }
    this.game = game;

    batch = new SpriteBatch();
    OrthographicCamera camera = new OrthographicCamera();
    viewport = new FitViewport(GameScreen.WORLD_WIDTH, GameScreen.WORLD_HEIGHT, camera);
    menuRenderer = new MenuRenderer(batch, camera);
    menuInput = new MenuInput(viewport);
    menu = new Menu<StartAction>("GOOSE SURVIVOR",
        GameScreen.WORLD_WIDTH, GameScreen.WORLD_HEIGHT)
        .addItem("Play", StartAction.PLAY)
        .addItem("Quit to Desktop", StartAction.QUIT)
        .addFooterLine("WASD / Arrows - Move     E - Interact     1-9 - Use item")
        .addFooterLine("Esc / P - Pause");
  }

  @Override
  public void resize(int width, int height) {
    viewport.update(width, height, true);
  }

  @Override
  public void render(float deltaTime) {
    StartAction choice = menuInput.poll(menu);
    if (choice == StartAction.PLAY) {
      game.startNewGame();
      return;
    }
    if (choice == StartAction.QUIT) {
      Gdx.app.exit();
      return;
    }

    Gdx.gl.glClearColor(0.05f, 0.08f, 0.05f, 1f);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    viewport.apply();
    menuRenderer.render(menu);
  }

  /**
   * Releases this screen's resources once it is no longer shown.
   */
  @Override
  public void hide() {
    dispose();
  }

  @Override
  public void dispose() {
    if (disposed) {
      return;
    }
    disposed = true;
    menuRenderer.dispose();
    batch.dispose();
  }
}
