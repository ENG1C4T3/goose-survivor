package com.eng1c4t3.goosesurvivor;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

/**
 * Manages loading, retrieving, and disposing of game textures and assets.
 */
public record TextureManager(AssetManager assetManager) implements Disposable {
  public static final String PLAYER_TEXTURE = "player.png";
  public static final String GOOSE_TEXTURE = "goose.png";
  public static final String LONG_BOI_TEXTURE = "long_boi.png";
  public static final String PROJECTILE_TEXTURE = "projectile.png";
  public static final String BLOCK_TEXTURE = "libgdx.png";
  public static final String TRUMPET_TEXTURE = "trumpet.png";
  public static final String COFFEE_TEXTURE = "coffee.png";
  public static final String ENERGY_DRINK_TEXTURE = "energy_drink.png";
  public static final String COFFEE_VENDING_MACHINE_TEXTURE = "coffee_vending_machine_full.png";
  public static final String COFFEE_VENDING_MACHINE_EMPTY_TEXTURE = "coffee_vending_machine_empty.png";
  public static final String POPCORN_TEXTURE = "popcorn.png";
  public static final String ITEM_VENDING_MACHINE_TEXTURE = "item_vending_machine_full.png";
  public static final String ITEM_VENDING_MACHINE_EMPTY_TEXTURE = "item_vending_machine_empty.png";
  public static final String FAST_GOOSE_TEXTURE = "fast_goose.png";
  public static final String TANK_GOOSE_TEXTURE = "tank_goose.png";
  public static final String MUSIC_STUDENT_TEXTURE = "music_student.png";
  public static final String LONG_BOI_STATUE_TEXTURE = "long_boi_statue.png";
  public static final String FOUNTAIN_TEXTURE = "fountain.png";
  public static final String GRADIENT_GOOSE_TEXTURE = "gradient_goose.png";
  public static final String FOX_TEXTURE = "fox.png";
  public static final String DOOR_TEXTURE = "door.png";
  public static final String KEY_TEXTURE = "key.png";
  /** Projectile fired by the Food Scatter Launcher. */
  public static final String NUT_PROJECTILE_TEXTURE = "nut.png";
  /** Projectile fired by the Food Stream Launcher. */
  public static final String SEED_PROJECTILE_TEXTURE = "seed.png";
  // No dedicated art yet; these borrow other images: swap the file names here (and in assets.txt).
  public static final String FOOD_STREAM_LAUNCHER_TEXTURE = "food_stream_launcher.png";
  public static final String FOOD_SCATTER_LAUNCHER_TEXTURE = "food_scatter_launcher.png";
  /** Drawn behind everything; the same size as the world (1280x720). */
  public static final String BACKGROUND_TEXTURE = "grass_background.png";

  /**
   * Constructs a new TextureManager with a default {@link AssetManager}.
   */
  public TextureManager() {
    this(new AssetManager());
  }

  /**
   * Constructs a new TextureManager with the specified {@link AssetManager}.
   *
   * @param assetManager The {@link AssetManager} instance to use.
   */
  public TextureManager {
  }

  /**
   * Queues game textures for loading.
   */
  public void loadTextures() {
    assetManager.load(PLAYER_TEXTURE, Texture.class);
    assetManager.load(GOOSE_TEXTURE, Texture.class);
    assetManager.load(LONG_BOI_TEXTURE, Texture.class);
    assetManager.load(PROJECTILE_TEXTURE, Texture.class);
    assetManager.load(BLOCK_TEXTURE, Texture.class);
    assetManager.load(TRUMPET_TEXTURE, Texture.class);
    assetManager.load(COFFEE_TEXTURE, Texture.class);
    assetManager.load(ENERGY_DRINK_TEXTURE, Texture.class);
    assetManager.load(COFFEE_VENDING_MACHINE_TEXTURE, Texture.class);
    assetManager.load(COFFEE_VENDING_MACHINE_EMPTY_TEXTURE, Texture.class);
    assetManager.load(POPCORN_TEXTURE, Texture.class);
    assetManager.load(ITEM_VENDING_MACHINE_TEXTURE, Texture.class);
    assetManager.load(ITEM_VENDING_MACHINE_EMPTY_TEXTURE, Texture.class);
    assetManager.load(FAST_GOOSE_TEXTURE, Texture.class);
    assetManager.load(TANK_GOOSE_TEXTURE, Texture.class);
    assetManager.load(MUSIC_STUDENT_TEXTURE, Texture.class);
    assetManager.load(LONG_BOI_STATUE_TEXTURE, Texture.class);
    assetManager.load(FOX_TEXTURE, Texture.class);
    assetManager.load(FOUNTAIN_TEXTURE, Texture.class);
    assetManager.load(DOOR_TEXTURE, Texture.class);
    assetManager.load(KEY_TEXTURE, Texture.class);
    assetManager.load(GRADIENT_GOOSE_TEXTURE, Texture.class);
    assetManager.load(FOOD_STREAM_LAUNCHER_TEXTURE, Texture.class);
    assetManager.load(FOOD_SCATTER_LAUNCHER_TEXTURE, Texture.class);
    assetManager.load(BACKGROUND_TEXTURE, Texture.class);
    assetManager.load(NUT_PROJECTILE_TEXTURE, Texture.class);
    assetManager.load(SEED_PROJECTILE_TEXTURE, Texture.class);
  }

  /**
   * Blocks until all queued assets have finished loading.
   */
  public void finishLoading() {
    assetManager.finishLoading();
  }

  /**
   * Retrieves a loaded texture by its file name.
   *
   * @param fileName The path or file name of the texture.
   * @return The {@link Texture} associated with the specified file name.
   */
  public Texture getTexture(String fileName) {
    return assetManager.get(fileName, Texture.class);
  }

  /**
   * Retrieves a {@link TextureRegion} for a loaded texture.
   *
   * @param fileName The path or file name of the texture.
   * @return A new {@link TextureRegion} wrapping the specified texture.
   */
  public TextureRegion getTextureRegion(String fileName) {
    return new TextureRegion(getTexture(fileName));
  }

  /**
   * Returns the underlying {@link AssetManager}.
   *
   * @return The {@link AssetManager} managed by this instance.
   */
  @Override public AssetManager assetManager() {
    return assetManager;
  }

  /**
   * Disposes of the managed assets and resources.
   */
  @Override
  public void dispose() {
    assetManager.dispose();
  }
}
