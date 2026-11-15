package com.eng1c4t3.goosesurvivor.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.eng1c4t3.goosesurvivor.items.ItemType;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;
import org.junit.jupiter.api.Test;

class WeightedTableTest {

  @Test
  void singleItemTableAlwaysRollsThatItem() {
    WeightedTable<ItemType> table = WeightedTable.of(ItemType.COFFEE);
    Random random = new Random(1);
    for (int i = 0; i < 100; i++) {
      assertEquals(ItemType.COFFEE, table.roll(random));
    }
  }

  @Test
  void pickMapsRollOntoWeightRanges() {
    WeightedTable<ItemType> table = new WeightedTable<ItemType>()
        .add(ItemType.COFFEE, 3f)
        .add(ItemType.ENERGY_DRINK, 1f);

    assertEquals(ItemType.COFFEE, table.pick(0f));
    assertEquals(ItemType.COFFEE, table.pick(0.74f));
    assertEquals(ItemType.ENERGY_DRINK, table.pick(0.75f));
    assertEquals(ItemType.ENERGY_DRINK, table.pick(0.9999f));
    assertEquals(0.75f, table.chanceOf(ItemType.COFFEE), 1e-6f);
    assertEquals(0f, table.chanceOf(ItemType.TRUMPET), 1e-6f);
  }

  @Test
  void rollsFollowWeightsOverManyRolls() {
    WeightedTable<ItemType> table = new WeightedTable<ItemType>()
        .add(ItemType.COFFEE, 3f)
        .add(ItemType.ENERGY_DRINK, 1f);
    Random random = new Random(42);
    Map<ItemType, Integer> counts = new EnumMap<>(ItemType.class);
    int rolls = 10000;
    for (int i = 0; i < rolls; i++) {
      counts.merge(table.roll(random), 1, Integer::sum);
    }

    float coffeeShare = counts.get(ItemType.COFFEE) / (float) rolls;
    assertTrue(Math.abs(coffeeShare - 0.75f) < 0.03f, "coffee share was " + coffeeShare);
  }

  @Test
  void rejectsInvalidEntriesAndEmptyRolls() {
    WeightedTable<ItemType> table = new WeightedTable<ItemType>();
    assertThrows(IllegalArgumentException.class, () -> table.add(null, 1f));
    assertThrows(IllegalArgumentException.class, () -> table.add(ItemType.COFFEE, 0f));
    assertThrows(IllegalArgumentException.class, () -> table.add(ItemType.COFFEE, -1f));
    assertThrows(IllegalArgumentException.class, () -> table.add(ItemType.COFFEE, Float.NaN));
    assertThrows(IllegalStateException.class, () -> table.pick(0.5f));
  }
}
