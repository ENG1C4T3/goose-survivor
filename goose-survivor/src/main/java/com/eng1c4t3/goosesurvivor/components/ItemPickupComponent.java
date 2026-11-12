package com.eng1c4t3.goosesurvivor.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.eng1c4t3.goosesurvivor.items.ItemType;
import com.eng1c4t3.goosesurvivor.systems.InteractionSystem;

/**
 * Interaction effect: adds an item to the inventory of the player who interacts with this
 * entity.
 *
 * <p>Only has an effect on an entity that also has an {@link InteractableComponent}. If the
 * player's inventory is full the pickup stays in the world.
 *
 * @see InteractionSystem
 */
public class ItemPickupComponent implements Component {
  public static final ComponentMapper<ItemPickupComponent> MAPPER =
      ComponentMapper.getFor(ItemPickupComponent.class);

  /** The item given to the player. */
  public ItemType item;

  /**
   * Constructs a new ItemPickupComponent.
   *
   * @param item The item given to the player.
   */
  public ItemPickupComponent(ItemType item) {
    this.item = item;
  }
}
