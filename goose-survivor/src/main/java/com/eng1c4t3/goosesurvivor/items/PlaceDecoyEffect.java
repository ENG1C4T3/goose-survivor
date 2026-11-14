package com.eng1c4t3.goosesurvivor.items;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.eng1c4t3.goosesurvivor.systems.DecoySystem;

/**
 * Places a decoy below the user that enemies attack instead of the player until it is
 * destroyed.
 *
 * <p>Does nothing if the engine has no {@link DecoySystem}.
 */
public class PlaceDecoyEffect implements ItemEffect {

  /** File name of the placed decoy's texture. */
  public final String texture;
  /** Width of the placed decoy. */
  public final float width;
  /** Height of the placed decoy. */
  public final float height;

  /**
   * Constructs a new PlaceDecoyEffect.
   *
   * @param texture The file name of the placed decoy's texture.
   * @param width   The width of the placed decoy.
   * @param height  The height of the placed decoy.
   */
  public PlaceDecoyEffect(String texture, float width, float height) {
    this.texture = texture;
    this.width = width;
    this.height = height;
  }

  @Override
  public void apply(Entity user, Engine engine) {
    DecoySystem decoys = engine.getSystem(DecoySystem.class);
    if (decoys != null) {
      decoys.placeDecoy(user, texture, width, height);
    }
  }
}
