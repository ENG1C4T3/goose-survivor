package com.eng1c4t3.goosesurvivor.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * A set of values, each with a relative weight, that can be rolled for a random value.
 *
 * <p>A value's chance of being rolled is its weight divided by the total weight of the table.
 * For example, a table of {@code A x9, B x1} rolls {@code A} 90% of the time.
 *
 * @param <T> The type of value in the table.
 */
public final class WeightedTable<T> {

  /**
   * One value in the table with its relative weight.
   *
   * @param <T>    The type of value.
   * @param value  The value that can be rolled.
   * @param weight Its relative weight.
   */
  public record Entry<T>(T value, float weight) {
  }

  private final List<Entry<T>> entries = new ArrayList<>();
  private float totalWeight = 0f;

  /**
   * Creates a table that always rolls the given value.
   *
   * @param value The only value in the table.
   * @param <T>   The type of value.
   * @return A new table containing just that value.
   */
  public static <T> WeightedTable<T> of(T value) {
    return new WeightedTable<T>().add(value, 1f);
  }

  /**
   * Adds a value to the table.
   *
   * @param value  The value that can be rolled.
   * @param weight The value's relative weight. Must be positive and finite.
   * @return This table, for chaining.
   * @throws IllegalArgumentException if the value is null or the weight is not positive.
   */
  public WeightedTable<T> add(T value, float weight) {
    if (value == null) {
      throw new IllegalArgumentException("value must not be null");
    }
    if (!(weight > 0f) || Float.isInfinite(weight)) {
      throw new IllegalArgumentException("weight must be positive and finite");
    }
    entries.add(new Entry<>(value, weight));
    totalWeight += weight;
    return this;
  }

  /**
   * Picks a random value, weighted by each entry's weight.
   *
   * @param random The source of randomness.
   * @return The rolled value.
   * @throws IllegalStateException if the table is empty.
   */
  public T roll(Random random) {
    return pick(random.nextFloat());
  }

  /**
   * Picks the value at a point in the table's weight range.
   *
   * @param roll A value in {@code [0, 1)}; 0 picks the first entry.
   * @return The value covering that point.
   * @throws IllegalStateException if the table is empty.
   */
  public T pick(float roll) {
    if (entries.isEmpty()) {
      throw new IllegalStateException("Cannot roll an empty table");
    }
    float target = roll * totalWeight;
    for (Entry<T> entry : entries) {
      target -= entry.weight();
      if (target < 0f) {
        return entry.value();
      }
    }
    // Floating-point rounding can leave a tiny remainder; fall back to the last entry.
    return entries.get(entries.size() - 1).value();
  }

  /**
   * Returns the chance of rolling a value, from 0 to 1.
   *
   * @param value The value to check.
   * @return The value's share of the total weight.
   */
  public float chanceOf(T value) {
    if (totalWeight == 0f) {
      return 0f;
    }
    float weight = 0f;
    for (Entry<T> entry : entries) {
      if (Objects.equals(entry.value(), value)) {
        weight += entry.weight();
      }
    }
    return weight / totalWeight;
  }

  /**
   * Returns the entries in the table.
   *
   * @return An unmodifiable view of the entries.
   */
  public List<Entry<T>> entries() {
    return Collections.unmodifiableList(entries);
  }

  /**
   * Returns whether the table has no values.
   *
   * @return {@code true} if nothing can be rolled.
   */
  public boolean isEmpty() {
    return entries.isEmpty();
  }
}
