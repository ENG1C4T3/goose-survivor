package com.eng1c4t3.goosesurvivor.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.eng1c4t3.goosesurvivor.components.TransformComponent;
import com.eng1c4t3.goosesurvivor.entities.EntityFactory;
import java.util.Objects;
import java.util.function.Function;

/**
 * Places decoys into the world on behalf of item effects, which have no access to textures.
 *
 * @see com.eng1c4t3.goosesurvivor.items.PlaceDecoyEffect
 */
public class DecoySystem extends EntitySystem {

  /** Vertical gap between the user and the decoy placed below them. */
  public static final float PLACE_GAP = 2f;

  private final Function<String, TextureRegion> textures;

  /**
   * Creates a decoy system.
   *
   * @param textures Returns the texture region for a texture file name.
   * @throws NullPointerException if textures is null.
   */
  public DecoySystem(Function<String, TextureRegion> textures) {
    this.textures = Objects.requireNonNull(textures, "textures must not be null");
    setProcessing(false);
  }

  /**
   * Places a decoy centred just below the user.
   *
   * @param user    The entity placing the decoy, normally the player.
   * @param texture The file name of the decoy's texture.
   * @param width   The decoy's width.
   * @param height  The decoy's height.
   * @return The placed decoy, or null if the user has no position.
   */
  public Entity placeDecoy(Entity user, String texture, float width, float height) {
    TransformComponent userTransform = TransformComponent.MAPPER.get(user);
    if (userTransform == null || getEngine() == null) {
      return null;
    }
    float x = userTransform.position.x + (userTransform.position.width - width) / 2f;
    float y = userTransform.position.y - height - PLACE_GAP;
    Entity decoy = EntityFactory.createDecoy(x, y, width, height, textures.apply(texture));
    getEngine().addEntity(decoy);
    return decoy;
  }
}
