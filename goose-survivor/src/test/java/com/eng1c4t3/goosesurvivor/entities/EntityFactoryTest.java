package com.eng1c4t3.goosesurvivor.entities;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.eng1c4t3.goosesurvivor.entities.EntityFactory.EnemyStats;
import com.eng1c4t3.goosesurvivor.entities.EntityFactory.EnemyType;
import org.junit.jupiter.api.Test;

class EntityFactoryTest {

  @Test
  void testEveryEnemyTypeHasValidStats() {
    for (EnemyType type : EnemyType.values()) {
      EnemyStats stats = EntityFactory.statsFor(type);
      assertNotNull(stats, type.name());
      assertTrue(stats.size() > 0, type.name());
      assertTrue(stats.health() > 0, type.name());
      assertTrue(stats.speed() > 0, type.name());
      assertTrue(stats.damage() > 0, type.name());
      assertNotNull(stats.texture(), type.name());
    }
  }
}
