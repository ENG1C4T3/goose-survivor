package com.eng1c4t3.goosesurvivor.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.eng1c4t3.goosesurvivor.components.InventoryComponent;
import com.eng1c4t3.goosesurvivor.components.UseItemEventComponent;
import com.eng1c4t3.goosesurvivor.items.InventorySlot;

/**
 * Counts down every item's cooldown and handles requests to use items.
 *
 * <p>Must run after {@link InputSystem} (which creates the use events) and before
 * {@link EventCleanupSystem} (which destroys them).
 *
 * @see InventoryComponent
 * @see UseItemEventComponent
 */
public class InventorySystem extends EntitySystem {

  private ImmutableArray<Entity> inventories;
  private ImmutableArray<Entity> events;

  /**
   * Caches the entities with inventories and the use item events.
   *
   * @param engine The {@link Engine} this system was added to.
   */
  @Override
  public void addedToEngine(Engine engine) {
    inventories = engine.getEntitiesFor(Family.all(InventoryComponent.class).get());
    events = engine.getEntitiesFor(Family.all(UseItemEventComponent.class).get());
  }

  /**
   * Ticks cooldowns, then handles this frame's use item events.
   *
   * @param deltaTime The time passed since the last frame.
   */
  @Override
  public void update(float deltaTime) {
    for (int i = 0; i < inventories.size(); ++i) {
      for (InventorySlot slot : InventoryComponent.MAPPER.get(inventories.get(i)).slots) {
        if (slot != null) {
          slot.tick(deltaTime);
        }
      }
    }

    for (int i = 0; i < events.size(); ++i) {
      UseItemEventComponent event = UseItemEventComponent.MAPPER.get(events.get(i));
      useItem(event.user, event.slotIndex);
    }
  }

  /**
   * Uses the item in a slot of an entity's inventory if it is ready: applies its effect, spends
   * a use, starts its cooldown and empties the slot if it has run out of uses.
   *
   * @param user The entity whose inventory holds the item.
   * @param slotIndex Index of the inventory slot to use.
   * @return {@code true} if the item was used.
   */
  public boolean useItem(Entity user, int slotIndex) {
    if (user == null || user.isScheduledForRemoval()) {
      return false;
    }
    InventoryComponent inventory = InventoryComponent.MAPPER.get(user);
    if (inventory == null) {
      return false;
    }
    InventorySlot slot = inventory.getSlot(slotIndex);
    if (slot == null || !slot.canUse()) {
      return false;
    }

    slot.item.effect.apply(user, getEngine());
    slot.consumeUse();
    if (slot.isDepleted()) {
      inventory.clearSlot(slotIndex);
    }
    return true;
  }
}
