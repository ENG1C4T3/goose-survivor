package com.eng1c4t3.goosesurvivor.entities;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.eng1c4t3.goosesurvivor.items.ItemType;

/**
 * Something a spawner can produce: an item pickup or an enemy.
 *
 * <p>Use {@link #item(ItemType)} or {@link #enemy(EntityFactory.EnemyType)} to create one.
 */
public interface SpawnDrop {

  /**
   * Returns the file name of the texture the spawned entity uses.
   *
   * @return The texture file name.
   */
  String texture();

  /**
   * Creates the entity for this drop.
   *
   * @param x      The x-coordinate.
   * @param y      The y-coordinate.
   * @param region The entity's texture region.
   * @return The new entity.
   */
  Entity create(float x, float y, TextureRegion region);

  /**
   * Returns a drop that spawns a pickup for an item.
   *
   * @param item The item to spawn.
   * @return The drop.
   */
  static SpawnDrop item(ItemType item) {
    return new ItemDrop(item);
  }

  /**
   * Returns a drop that spawns an enemy.
   *
   * @param type The enemy to spawn.
   * @return The drop.
   */
  static SpawnDrop enemy(EntityFactory.EnemyType type) {
    return new EnemyDrop(type);
  }

  /**
   * Spawns a world pickup for an item.
   *
   * @param item The item given to the player on pickup.
   */
  record ItemDrop(ItemType item) implements SpawnDrop {
    @Override
    public String texture() {
      return item.texture;
    }

    @Override
    public Entity create(float x, float y, TextureRegion region) {
      return EntityFactory.createItemPickup(item, x, y, item.width, item.height, region);
    }
  }

  /**
   * Spawns an enemy.
   *
   * @param type The type of enemy.
   */
  record EnemyDrop(EntityFactory.EnemyType type) implements SpawnDrop {
    @Override
    public String texture() {
      return EntityFactory.statsFor(type).texture();
    }

    @Override
    public Entity create(float x, float y, TextureRegion region) {
      return EntityFactory.createEnemy(type, x, y, region);
    }
  }
}
