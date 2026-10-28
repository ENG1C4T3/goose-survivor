package com.eng1c4t3.goosesurvivor.components;

import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.eng1c4t3.goosesurvivor.systems.RenderSystem;

/**
 * Represents the texture of an entity in the game.
 *
 * <p>Loading of the texture region is handled by the {@link TextureLoader} system
 * {@see GooseSurvivor}.
 *
 * @see RenderSystem
 */
public class TextureComponent implements Component {
  public TextureRegion region;

  /**
   * Constructs a new TextureComponent with the given texture region.
   *
   * @param region The {@link TextureRegion} to use for the entity.
   */
  public TextureComponent(TextureRegion region) {
    this.region = region;
  }
}
