package com.eng1c4t3.goosesurvivor.items;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ItemTypeTest {

  @Test
  void testEveryItemIsUsable() {
    for (ItemType type : ItemType.values()) {
      assertNotNull(type.effect, type.name());
      assertTrue(type.isUnlimited() || type.maxUses > 0, type.name());
      assertTrue(new InventorySlot(type).canUse(), type.name());
    }
  }

  @Test
  void testEveryItemHasCooldown() {
    for (ItemType type : ItemType.values()) {
      assertTrue(type.cooldown > 0, type.name());
    }
  }

  @Test
  void testEveryItemHasNamesAndTexture() {
    for (ItemType type : ItemType.values()) {
      assertNotNull(type.displayName, type.name());
      assertNotNull(type.shortName, type.name());
      assertNotNull(type.texture, type.name());
    }
  }
}
