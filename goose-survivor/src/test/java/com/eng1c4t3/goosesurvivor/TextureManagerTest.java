package com.eng1c4t3.goosesurvivor;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.badlogic.gdx.assets.AssetManager;
import org.junit.jupiter.api.Test;

class TextureManagerTest {

  @Test
  void usesProvidedAssetManager() {
    final AssetManager assets = new AssetManager();
    final TextureManager manager = new TextureManager(assets);

    try {
      assertSame(assets, manager.assetManager());
    } finally {
      manager.dispose();
    }
  }

  @Test
  void defaultConstructorCreatesAssetManager() {
    final TextureManager manager = new TextureManager();

    try {
      assertNotNull(manager.assetManager());
    } finally {
      manager.dispose();
    }
  }
}