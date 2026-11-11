package com.eng1c4t3.goosesurvivor;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.eng1c4t3.goosesurvivor.entities.EntityFactory;
import com.eng1c4t3.goosesurvivor.items.ItemType;
import com.eng1c4t3.goosesurvivor.ui.InventoryHud;

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
  private final InventoryHud hud;

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

    session.configureShooting(
        textureManager.getTextureRegion(TextureManager.PROJECTILE_TEXTURE));

    renderer = new WorldRenderer(batch, camera);
    hud = new InventoryHud(batch, camera, WORLD_WIDTH, WORLD_HEIGHT);
    populateWorld();
  }

  /**
   * Populates the new session with initial gameplay entities.
   */
  private void populateWorld() {
    GameWorld world = session.getWorld();

    Entity player = EntityFactory.createPlayer(255f, 255f,
        textureManager.getTextureRegion(TextureManager.PLAYER_TEXTURE));
    world.addEntity(player);

    world.addEntity(EntityFactory.createBlock(100f, 100f, 50f, 50f,
        textureManager.getTextureRegion(TextureManager.BLOCK_TEXTURE)));

    addItemPickup(ItemType.COFFEE, 200f, 300f);
    addItemPickup(ItemType.TRUMPET, 500f, 150f);
    addItemPickup(ItemType.ENERGY_DRINK, 300f, 300f);

    addEnemy(EntityFactory.EnemyType.FAST_GOOSE, 700f, 350f);
    addEnemy(EntityFactory.EnemyType.LONG_BOI, 400f, 400f);
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
  private void addItemPickup(ItemType item, float x, float y) {
    session.getWorld().addEntity(EntityFactory.createItemPickup(item, x, y,
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
    hud.render(session);
  }

  @Override
  public void pause() {
    session.pause();
  }

  @Override
  public void dispose() {
    hud.dispose();
    batch.dispose();
    textureManager.dispose();
  }

}
