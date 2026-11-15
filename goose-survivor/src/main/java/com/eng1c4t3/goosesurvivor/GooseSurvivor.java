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
    // On macOS, GLFW must run on the process's first thread, which needs the JVM flag
    // -XstartOnFirstThread. `./gradlew run` adds it, but double-clicking the JAR or
    // `java -jar` does not, so GLFW throws an IllegalStateException. Without the flag,
    // switch to LWJGL's glfw_async build, which works from any thread. Does nothing on
    // other operating systems.
    long pid = ProcessHandle.current().pid();
    if (!"1".equals(System.getenv("JAVA_STARTED_ON_FIRST_THREAD_" + pid))) {
      Lwjgl3ApplicationConfiguration.useGlfwAsync();
    }

    Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
    config.setTitle("Goose Survivor");
    // Always fullscreen at the monitor's native resolution. The game world is a fixed
    // 1280x720 (HD) area that each screen's FitViewport scales up to fill the display.
    config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
    config.setResizable(false);
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
    showStartScreen();
  }

  /**
   * Replaces the current screen with the title screen. The previous screen
   * releases its resources when hidden.
   */
  public void showStartScreen() {
    setScreen(new StartScreen(this));
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
