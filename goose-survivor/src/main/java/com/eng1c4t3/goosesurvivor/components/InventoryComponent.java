package com.eng1c4t3.goosesurvivor.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.eng1c4t3.goosesurvivor.items.InventorySlot;
import com.eng1c4t3.goosesurvivor.items.ItemType;
import com.eng1c4t3.goosesurvivor.systems.InventorySystem;

/**
 * A fixed-size hotbar of items.
 *
 * <p>Slots keep their position: when an item runs out of uses its slot becomes empty rather
 * than the later items shifting down, so each item keeps its key binding.
 *
 * @see InventorySystem
 */
public class InventoryComponent implements Component {
  public static final ComponentMapper<InventoryComponent> MAPPER =
      ComponentMapper.getFor(InventoryComponent.class);

  /** Number of slots the player's inventory has. */
  public static final int DEFAULT_CAPACITY = 5;

  /** The slots; an empty slot is {@code null}. */
  public final InventorySlot[] slots;

  /**
   * Constructs an empty inventory.
   *
   * @param capacity The number of slots.
   * @throws IllegalArgumentException if capacity is not positive.
   */
  public InventoryComponent(int capacity) {
    if (capacity <= 0) {
      throw new IllegalArgumentException("Capacity must be positive");
    }
    this.slots = new InventorySlot[capacity];
  }

  /**
   * Returns the number of slots.
   *
   * @return The capacity.
   */
  public int capacity() {
    return slots.length;
  }

  /**
   * Puts a fresh copy of an item into the first empty slot.
   *
   * @param item The kind of item to add.
   * @return The index of the slot it went into, or -1 if the inventory is full.
   */
  public int addItem(ItemType item) {
    for (int i = 0; i < slots.length; ++i) {
      if (slots[i] == null) {
        slots[i] = new InventorySlot(item);
        return i;
      }
    }
    return -1;
  }

  /**
   * Returns the slot at an index.
   *
   * @param index The slot index.
   * @return The slot, or {@code null} if it is empty or the index is out of range.
   */
  public InventorySlot getSlot(int index) {
    if (index < 0 || index >= slots.length) {
      return null;
    }
    return slots[index];
  }

  /**
   * Empties the slot at an index. Does nothing if the index is out of range.
   *
   * @param index The slot index.
   */
  public void clearSlot(int index) {
    if (index >= 0 && index < slots.length) {
      slots[index] = null;
    }
  }

  /**
   * Returns whether every slot holds an item.
   *
   * @return {@code true} if no more items can be added.
   */
  public boolean isFull() {
    for (InventorySlot slot : slots) {
      if (slot == null) {
        return false;
      }
    }
    return true;
  }
}
