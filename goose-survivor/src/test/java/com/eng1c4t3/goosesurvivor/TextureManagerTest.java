package com.eng1c4t3.goosesurvivor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.badlogic.gdx.assets.AssetManager;
import org.junit.jupiter.api.Test;

class TextureManagerTest {

  @Test
  void testTextureManagerInitialization() {
    AssetManager customAssetManager = new AssetManager();
    TextureManager manager = new TextureManager(customAssetManager);

    assertNotNull(manager.getAssetManager());
    assertEquals(customAssetManager, manager.getAssetManager());
    assertEquals("libgdx.png", TextureManager.PLAYER_TEXTURE);
  }

  @Test
  void testDefaultConstructor() {
    TextureManager manager = new TextureManager();

    assertNotNull(manager.getAssetManager());
  }
}
