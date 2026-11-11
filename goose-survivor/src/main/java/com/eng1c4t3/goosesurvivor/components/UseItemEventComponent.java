package com.eng1c4t3.goosesurvivor.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.eng1c4t3.goosesurvivor.systems.EventCleanupSystem;
import com.eng1c4t3.goosesurvivor.systems.InputSystem;
import com.eng1c4t3.goosesurvivor.systems.InventorySystem;

/**
 * A request to use the item in one inventory slot, using the event-as-entity pattern.
 *
 * <ol>
 *   <li>{@link InputSystem} creates the event entity when a hotbar key is pressed.</li>
 *   <li>{@link InventorySystem} uses the item if it is ready.</li>
 *   <li>{@link EventCleanupSystem} destroys the event entity at the end of the frame.</li>
 * </ol>
 */
public class UseItemEventComponent implements Component {
  public static final ComponentMapper<UseItemEventComponent> MAPPER =
      ComponentMapper.getFor(UseItemEventComponent.class);

  /** The entity whose inventory holds the item. */
  public Entity user;
  /** Index of the inventory slot to use. */
  public int slotIndex;

  /**
   * Constructs a new UseItemEventComponent.
   *
   * @param user The entity whose inventory holds the item.
   * @param slotIndex Index of the inventory slot to use.
   */
  public UseItemEventComponent(Entity user, int slotIndex) {
    this.user = user;
    this.slotIndex = slotIndex;
  }
}
