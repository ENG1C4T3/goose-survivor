package com.eng1c4t3.goosesurvivor;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

/**
 * Manages loading, retrieving, and disposing of game textures and assets.
 */
public class TextureManager implements Disposable {
  public static final String PLAYER_TEXTURE = "libgdx.png";

  private final AssetManager assetManager;

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
  public TextureManager(AssetManager assetManager) {
    this.assetManager = assetManager;
  }

  /**
   * Queues game textures for loading.
   */
  public void loadTextures() {
    assetManager.load(PLAYER_TEXTURE, Texture.class);
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
  public AssetManager getAssetManager() {
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
