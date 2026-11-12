package com.eng1c4t3.goosesurvivor;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

/**
 * Start the application.
 * act as main class
 * manage active screen
 *
 */
public class GooseSurvivor extends Game {

  /**
   * Start application.
   *
   * @param args command-line arguments
   */
  public static void main(String[] args) {
    Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
    config.setTitle("Goose Survivor");
    config.setWindowedMode(800, 480);
    config.setForegroundFPS(60);
    config.useVsync(true);
    new Lwjgl3Application(new GooseSurvivor(), config);
  }

  /**
   * Creates new session, opens its gameplay screen.
   *
   */
  @Override
  public void create() {
    startNewGame();
  }

  /**
   * Replaces the current screen with a fresh session. The previous screen
   * releases its resources when hidden.
   */
  public void startNewGame() {
    setScreen(new GameScreen(this, new GameSession()));
  }

  /**
   * Hides and releses active screen.
   */
  @Override
  public void dispose() {
    Screen currentScreen = getScreen();
    setScreen(null);

    if (currentScreen != null) {
      currentScreen.dispose();
    }
  }
}
