package com.eng1c4t3.goosesurvivor.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.eng1c4t3.goosesurvivor.items.ItemType;
import org.junit.jupiter.api.Test;

class InventoryComponentTest {

  @Test
  void testNewInventoryIsEmpty() {
    InventoryComponent inventory = new InventoryComponent(3);

    assertEquals(3, inventory.capacity());
    assertFalse(inventory.isFull());
    for (int i = 0; i < 3; ++i) {
      assertNull(inventory.getSlot(i));
    }
  }

  @Test
  void testCapacityMustBePositive() {
    assertThrows(IllegalArgumentException.class, () -> new InventoryComponent(0));
  }

  @Test
  void testAddItemFillsFirstEmptySlot() {
    InventoryComponent inventory = new InventoryComponent(3);

    assertEquals(0, inventory.addItem(ItemType.COFFEE));
    assertEquals(1, inventory.addItem(ItemType.TRUMPET));
    inventory.clearSlot(0);
    assertEquals(0, inventory.addItem(ItemType.COFFEE));

    assertEquals(ItemType.COFFEE, inventory.getSlot(0).item);
    assertEquals(ItemType.TRUMPET, inventory.getSlot(1).item);
  }

  @Test
  void testAddItemToFullInventoryFails() {
    InventoryComponent inventory = new InventoryComponent(1);
    inventory.addItem(ItemType.COFFEE);

    assertTrue(inventory.isFull());
    assertEquals(-1, inventory.addItem(ItemType.TRUMPET));
    assertEquals(ItemType.COFFEE, inventory.getSlot(0).item);
  }

  @Test
  void testSameItemInTwoSlotsHasSeparateState() {
    InventoryComponent inventory = new InventoryComponent(2);
    inventory.addItem(ItemType.COFFEE);
    inventory.addItem(ItemType.COFFEE);

    inventory.getSlot(0).consumeUse();

    assertTrue(inventory.getSlot(0).isOnCooldown());
    assertFalse(inventory.getSlot(1).isOnCooldown());
  }

  @Test
  void testOutOfRangeSlotIsNull() {
    InventoryComponent inventory = new InventoryComponent(2);

    assertNull(inventory.getSlot(-1));
    assertNull(inventory.getSlot(2));
    inventory.clearSlot(5);
  }
}
