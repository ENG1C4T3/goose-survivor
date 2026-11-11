package com.eng1c4t3.goosesurvivor.items;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class InventorySlotTest {

  @Test
  void testNewSlotHasFullUsesAndIsReady() {
    InventorySlot slot = new InventorySlot(ItemType.COFFEE);

    assertEquals(ItemType.COFFEE.maxUses, slot.usesRemaining);
    assertFalse(slot.isOnCooldown());
    assertTrue(slot.canUse());
  }

  @Test
  void testNullItemIsRejected() {
    assertThrows(IllegalArgumentException.class, () -> new InventorySlot(null));
  }

  @Test
  void testConsumeUseSpendsUseAndStartsCooldown() {
    InventorySlot slot = new InventorySlot(ItemType.COFFEE);

    slot.consumeUse();

    assertEquals(ItemType.COFFEE.maxUses - 1, slot.usesRemaining);
    assertEquals(ItemType.COFFEE.cooldown, slot.cooldownTimer, 1e-5f);
    assertTrue(slot.isOnCooldown());
    assertFalse(slot.canUse());
  }

  @Test
  void testCannotConsumeWhileOnCooldown() {
    InventorySlot slot = new InventorySlot(ItemType.TRUMPET);
    slot.consumeUse();

    assertThrows(IllegalStateException.class, slot::consumeUse);
  }

  @Test
  void testTickCountsCooldownDownToZero() {
    InventorySlot slot = new InventorySlot(ItemType.TRUMPET);
    slot.consumeUse();

    slot.tick(1f);
    assertEquals(ItemType.TRUMPET.cooldown - 1f, slot.cooldownTimer, 1e-4f);

    slot.tick(1000f);
    assertEquals(0f, slot.cooldownTimer, 1e-5f);
    assertTrue(slot.canUse());
  }

  @Test
  void testSingleUseItemDepletesAfterOneUse() {
    InventorySlot slot = new InventorySlot(ItemType.COFFEE);

    slot.consumeUse();

    assertTrue(slot.isDepleted());
    assertFalse(slot.canUse());
  }

  @Test
  void testUnlimitedItemNeverDepletes() {
    InventorySlot slot = new InventorySlot(ItemType.TRUMPET);

    for (int i = 0; i < 100; ++i) {
      slot.consumeUse();
      slot.tick(ItemType.TRUMPET.cooldown);
    }

    assertTrue(slot.isUnlimited());
    assertFalse(slot.isDepleted());
    assertEquals(ItemType.UNLIMITED_USES, slot.usesRemaining);
  }

  @Test
  void testCooldownFraction() {
    InventorySlot slot = new InventorySlot(ItemType.TRUMPET);
    assertEquals(0f, slot.cooldownFraction(), 1e-5f);

    slot.consumeUse();
    assertEquals(1f, slot.cooldownFraction(), 1e-5f);

    slot.tick(ItemType.TRUMPET.cooldown / 2);
    assertEquals(0.5f, slot.cooldownFraction(), 1e-5f);
  }
}
