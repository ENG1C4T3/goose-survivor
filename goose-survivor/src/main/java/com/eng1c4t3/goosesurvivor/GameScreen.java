package com.eng1c4t3.goosesurvivor;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.eng1c4t3.goosesurvivor.components.InventoryComponent;
import com.eng1c4t3.goosesurvivor.entities.EntityFactory;
import com.eng1c4t3.goosesurvivor.items.ItemType;
import com.eng1c4t3.goosesurvivor.ui.InventoryHud;
import com.eng1c4t3.goosesurvivor.ui.Menu;
import com.eng1c4t3.goosesurvivor.ui.MenuInput;
import com.eng1c4t3.goosesurvivor.ui.MenuRenderer;

/**
 * Handles gameplay presentation and game session controls.
 */
public class GameScreen extends ScreenAdapter {
  /** Actions offered by the pause and end-of-game menus. */
  private enum MenuAction {
    RESUME,
    RESTART,
    MAIN_MENU,
    QUIT
  }

  private final GameSession session;

  /** World width in pixels: the game always shows a 1280x720 (HD) area, scaled to fit. */
  static final float WORLD_WIDTH = 1280f;
  /** World height in pixels. */
  static final float WORLD_HEIGHT = 720f;

  private final SpriteBatch batch;
  private final OrthographicCamera camera;
  private final FitViewport viewport;
  private final TextureManager textureManager;
  private final WorldRenderer renderer;
  private final InventoryHud hud;
  private final Menu<MenuAction> pauseMenu;
  /** Win or game over menu, built when the session ends. */
  private Menu<MenuAction> endMenu = null;
  private final MenuRenderer menuRenderer;
  private final MenuInput menuInput;
  private final GooseSurvivor game;
  private boolean disposed = false;

  /**
   * Creates a gameplay screen for the given session, initializes its player.
   *
   * @param game    the game that owns this screen, used to restart
   * @param session the new session displayed by this screen
   *
   * @throws IllegalArgumentException if game or session is null or the
   *                                  session's world is not empty.
   */
  public GameScreen(GooseSurvivor game, GameSession session) {
    if (game == null) {
      throw new IllegalArgumentException(
          "Game must not be null");
    }
    if (session == null) {
      throw new IllegalArgumentException(
          "Session must not be null");
    }
    this.game = game;
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

    session.configureShooting(
        textureManager.getTextureRegion(TextureManager.PROJECTILE_TEXTURE),
        textureManager::getTextureRegion);

    session.configureDecoys(textureManager::getTextureRegion);

    session.configureMouseAim(viewport::unproject);

    session.configureHiddenEvents(
        textureManager.getTextureRegion(TextureManager.LONG_BOI_TEXTURE),
        textureManager::getTextureRegion);

    renderer = new WorldRenderer(batch, camera);
    renderer.setBackground(
        textureManager.getTextureRegion(TextureManager.BACKGROUND_TEXTURE),
        WORLD_WIDTH, WORLD_HEIGHT);
    hud = new InventoryHud(batch, camera, WORLD_WIDTH, WORLD_HEIGHT);
    menuRenderer = new MenuRenderer(batch, camera);
    menuInput = new MenuInput(viewport);
    pauseMenu = new Menu<MenuAction>("PAUSED", WORLD_WIDTH, WORLD_HEIGHT)
        .addItem("Resume", MenuAction.RESUME)
        .addItem("Restart", MenuAction.RESTART)
        .addItem("Main Menu", MenuAction.MAIN_MENU)
        .addItem("Quit to Desktop", MenuAction.QUIT)
        .addFooterLine("WASD / Arrows - Move     E - Interact     1-9 - Use item")
        .addFooterLine("Up/Down + Enter or mouse to choose     Esc / P - Resume");
    populateWorld();
  }

  /**
   * Populates the new session with initial gameplay entities.
   */
  private void populateWorld() {
    GameWorld world = session.getWorld();

    Entity player = EntityFactory.createPlayer(408f, 382f,
        textureManager.getTextureRegion(TextureManager.PLAYER_TEXTURE));
    InventoryComponent.MAPPER.get(player).addItem(ItemType.FOOD_STREAM_LAUNCHER);
    world.addEntity(player);

    world.addEntity(EntityFactory.createMusicStudent(
        800f, 225f,
        textureManager.getTextureRegion(TextureManager.MUSIC_STUDENT_TEXTURE)));

    world.addEntity(EntityFactory.createLongBoiStatue(
        640f, 450f,
        textureManager.getTextureRegion(TextureManager.LONG_BOI_STATUE_TEXTURE)));

    world.addEntity(EntityFactory.createCoffeeVendingMachine(480f, 450f,
        textureManager.getTextureRegion(TextureManager.COFFEE_VENDING_MACHINE_TEXTURE),
        textureManager.getTextureRegion(TextureManager.COFFEE_VENDING_MACHINE_EMPTY_TEXTURE)));

    world.addEntity(EntityFactory.createVendingMachine(320f, 450f,
        textureManager.getTextureRegion(TextureManager.ITEM_VENDING_MACHINE_TEXTURE),
        textureManager.getTextureRegion(TextureManager.ITEM_VENDING_MACHINE_EMPTY_TEXTURE)));

    addEnemy(EntityFactory.EnemyType.FAST_GOOSE, 1120f, 525f);
    addEnemy(EntityFactory.EnemyType.TANK_GOOSE, 1120f, 150f);

    world.addEntity(EntityFactory.createFox(960f, 600f,
        textureManager.getTextureRegion(TextureManager.FOX_TEXTURE)));
    world.addEntity(EntityFactory.createFountain(110f, 100f,
        textureManager.getTextureRegion(TextureManager.FOUNTAIN_TEXTURE)));
    // The gradient goose (which drops the key) spawns below the door when the player nears it.
    world.addEntity(EntityFactory.createDoor(WORLD_WIDTH - 97f, 300f,
        textureManager.getTextureRegion(TextureManager.DOOR_TEXTURE)));
  }

  /**
   * Adds a enemy using the textrue associated with enemy's type.
   *
   * @param type the type of enemy
   * @param x    the initial x-coordinate
   * @param y    the initial y-coordinate
   */
  private void addEnemy(EntityFactory.EnemyType type, float x, float y) {
    EntityFactory.EnemyStats stats = EntityFactory.statsFor(type);

    session.getWorld().addEntity(EntityFactory.createEnemy(type, x, y,
        textureManager.getTextureRegion(stats.texture())));
  }

  /**
   * Adds a world pickup for an item, using the item's texture.
   *
   * @param item the item given to the player on pickup
   * @param x    the x-coordinate
   * @param y    the y-coordinate
   */
  private void addItemPickup(ItemType item, float x, float y, int width, int height) {
    session.getWorld().addEntity(EntityFactory.createItemPickup(item, x, y, width, height,
        textureManager.getTextureRegion(item.texture)));
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

  private static boolean isPauseKeyJustPressed() {
    return Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)
        || Gdx.input.isKeyJustPressed(Input.Keys.P);
  }

  /**
   * Opens the pause menu when Esc or P is pressed during play.
   */
  private void handleRunningInput() {
    if (isPauseKeyJustPressed()) {
      openPauseMenu();
    }
  }

  private void openPauseMenu() {
    session.pause();
    if (session.getState() == SessionState.PAUSED) {
      pauseMenu.reset();
    }
  }

  /**
   * Handles input while the pause menu is open; Esc or P resumes.
   *
   * @return the action chosen this frame, or null if none
   */
  private MenuAction handlePausedInput() {
    if (isPauseKeyJustPressed()) {
      return MenuAction.RESUME;
    }
    return menuInput.poll(pauseMenu);
  }

  /**
   * Carries out a menu choice.
   *
   * @return true if this screen has been replaced or the app is exiting, so the
   *         frame must not continue
   */
  private boolean applyMenuChoice(MenuAction choice) {
    switch (choice) {
      case RESUME:
        session.resume();
        return false;
      case RESTART:
        game.startNewGame();
        return true;
      case MAIN_MENU:
        game.showStartScreen();
        return true;
      case QUIT:
        Gdx.app.exit();
        return true;
      default:
        return false;
    }
  }

  private static boolean isEnded(SessionState state) {
    return state == SessionState.WON || state == SessionState.LOST;
  }

  /**
   * Builds the win or game over menu for how the session ended.
   */
  private Menu<MenuAction> createEndMenu() {
    boolean won = session.getState() == SessionState.WON;
    Menu<MenuAction> menu = new Menu<>(won ? "YOU WIN!" : "GAME OVER",
        WORLD_WIDTH, WORLD_HEIGHT);
    menu.addItem("Play Again", MenuAction.RESTART)
        .addItem("Main Menu", MenuAction.MAIN_MENU)
        .addItem("Quit to Desktop", MenuAction.QUIT);

    if (won) {
      menu.addFooterLine("You survived the geese!");
    } else if (session.getRemainingTime() <= 0f) {
      menu.addFooterLine("Time ran out!");
    } else {
      menu.addFooterLine("The geese got you.");
    }
    menu.addFooterLine("Time played: " + formatTime(session.getElapsedTime()));
    return menu;
  }

  /**
   * Formats seconds as minutes and seconds, e.g. 83.4 becomes "1:23".
   */
  static String formatTime(float seconds) {
    int whole = (int) seconds;
    return String.format("%d:%02d", whole / 60, whole % 60);
  }

  /**
   * Handles screen controls and advance the session, draws the world.
   *
   * @param deltaTime elapsed time since the previous frame
   */
  @Override
  public void render(float deltaTime) {
    MenuAction choice = null;
    if (session.getState() == SessionState.RUNNING) {
      handleRunningInput();
    } else if (session.getState() == SessionState.PAUSED) {
      choice = handlePausedInput();
    } else if (endMenu != null) {
      choice = menuInput.poll(endMenu);
    }
    if (choice != null && applyMenuChoice(choice)) {
      return;
    }

    session.update(deltaTime);
    if (endMenu == null && isEnded(session.getState())) {
      endMenu = createEndMenu();
    }

    Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    viewport.apply();
    renderer.render(session.getWorld());
    hud.render(session);
    if (session.getState() == SessionState.PAUSED) {
      menuRenderer.render(pauseMenu);
    } else if (endMenu != null) {
      menuRenderer.render(endMenu);
    }
  }

  /**
   * Opens the pause menu when the window loses focus or is minimised.
   */
  @Override
  public void pause() {
    openPauseMenu();
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
    hud.dispose();
    batch.dispose();
    textureManager.dispose();
  }

}
