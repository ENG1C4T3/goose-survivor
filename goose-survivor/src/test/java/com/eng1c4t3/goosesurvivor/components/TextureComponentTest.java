package com.eng1c4t3.goosesurvivor.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.eng1c4t3.goosesurvivor.ecs.Component;
import org.junit.jupiter.api.Test;

class TextureComponentTest {

  @Test
  void testTextureComponentWithNullRegion() {
    TextureComponent textureComponent = new TextureComponent(null);
    assertNotNull(textureComponent);
    assertTrue(textureComponent instanceof Component);
    assertNull(textureComponent.region);
  }

  @Test
  void testTextureComponentWithRegion() {
    TextureRegion region = new TextureRegion();
    TextureComponent textureComponent = new TextureComponent(region);
    assertNotNull(textureComponent);
    assertEquals(region, textureComponent.region);

    TextureRegion newRegion = new TextureRegion();
    textureComponent.region = newRegion;
    assertEquals(newRegion, textureComponent.region);
  }
}
