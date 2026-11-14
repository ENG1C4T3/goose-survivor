package com.eng1c4t3.goosesurvivor;

import com.eng1c4t3.goosesurvivor.entities.EntityFactory;
import com.eng1c4t3.goosesurvivor.entities.EntityFactory.EnemyType;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * Records item usage, hidden event progress and enemies defeated for one play through.
 */
public class SessionStats {
  private int itemsUsed;
  private int eventsTriggered;
  private int eventsCompleted;
  private int fountainsVisited;
  private final Map<EnemyType, Integer> enemiesKilled = new EnumMap<>(EnemyType.class);

  /**
   * Records a successfully used item.
   */
  public void recordItemUsed() {
    itemsUsed++;
  }

  /**
   * Records one newly triggered hidden event.
   */
  public void recordEventTriggered() {
    eventsTriggered++;
  }

  /**
   * Records one completed hidden event.
   */
  public void recordEventCompleted() {
    eventsCompleted++;
  }

  /**
   * Retruns the number of successfully used items.
   *
   * @return the number of item usage.
   */
  public int getItemsUsed() {
    return itemsUsed;
  }

  /**
   * Returns the number of hidden events triggered.
   *
   * @return the number of triggered event.
   */
  public int getEventsTriggered() {
    return eventsTriggered;
  }

  /**
   * Return the number of hidden events completed.
   *
   * @return the number of events have been completed.
   */
  public int getEventsCompleted() {
    return eventsCompleted;
  }

  /**
   * Records one defeated enemy.
   *
   * @param type The type of enemy defeated.
   * @throws IllegalArgumentException if type is null.
   */
  public void recordEnemyKilled(EnemyType type) {
    if (type == null) {
      throw new IllegalArgumentException("Enemy type must not be null");
    }
    enemiesKilled.merge(type, 1, Integer::sum);
  }

  /**
   * Returns how many enemies of a type have been defeated.
   *
   * @param type The enemy type.
   * @return The number defeated.
   */
  public int getEnemiesKilled(EnemyType type) {
    return enemiesKilled.getOrDefault(type, 0);
  }

  /**
   * Returns how many enemies have been defeated in total.
   *
   * @return The number of enemies defeated, of every type.
   */
  public int getTotalEnemiesKilled() {
    int total = 0;
    for (int count : enemiesKilled.values()) {
      total += count;
    }
    return total;
  }

  /**
   * Returns the number of enemies defeated of each type. Types with no kills are left out.
   *
   * @return An unmodifiable view of the kill counts.
   */
  public Map<EnemyType, Integer> getEnemiesKilledByType() {
    return Collections.unmodifiableMap(enemiesKilled);
  }

  /**
   * Returns the points earned from defeated enemies, using each type's
   * {@link EntityFactory.EnemyStats#score()}.
   *
   * @return The total points from enemy kills.
   */
  public int getEnemyKillScore() {
    int score = 0;
    for (Map.Entry<EnemyType, Integer> entry : enemiesKilled.entrySet()) {
      score += EntityFactory.statsFor(entry.getKey()).score() * entry.getValue();
    }
    return score;
  }

  /**
   * Records a visit to a fountain.
   */
  public void recordFountainVisited() {
    fountainsVisited++;
  }

  /**
   * Returns the number of fountains visited.
   *
   * @return the number of fountain visits.
   */
  public int getFountainsVisited() {
    return fountainsVisited;
  }
}
