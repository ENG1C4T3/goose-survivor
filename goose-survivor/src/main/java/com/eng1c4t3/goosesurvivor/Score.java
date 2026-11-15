package com.eng1c4t3.goosesurvivor;

/**
 * A playthrough's score, broken down by where the points came from.
 *
 * <p>Winning and the time bonus are worth the most, hidden events a large amount, and pacifying
 * geese, interacting with objects and using items a limited amount. Interactions and item uses
 * stop scoring after {@link #MAX_SCORED_INTERACTIONS} and {@link #MAX_SCORED_ITEM_USES} so they
 * cannot be farmed.
 *
 * @param win          Points for winning the game.
 * @param timeBonus    Points for the time left when the game was won.
 * @param hiddenEvents Points for hidden events found.
 * @param geese        Points for geese pacified, using each type's EXP.
 * @param interactions Points for interacting with objects on the map.
 * @param itemsUsed    Points for using items.
 */
public record Score(int win, int timeBonus, int hiddenEvents, int geese, int interactions,
    int itemsUsed) {

  /** Points for winning the game. */
  public static final int WIN_POINTS = 1000;
  /** Points per whole second left on the timer when the game is won. */
  public static final int POINTS_PER_SECOND_LEFT = 10;
  /** Points per hidden event found. */
  public static final int HIDDEN_EVENT_POINTS = 250;
  /** Points per interaction with an object on the map. */
  public static final int INTERACTION_POINTS = 15;
  /** Interactions after this many no longer score. */
  public static final int MAX_SCORED_INTERACTIONS = 10;
  /** Points per item used. */
  public static final int ITEM_USE_POINTS = 5;
  /** Item uses after this many no longer score. */
  public static final int MAX_SCORED_ITEM_USES = 20;

  /**
   * Works out the score for a playthrough.
   *
   * @param stats         The playthrough's statistics.
   * @param won           Whether the game was won; the win and time bonuses need this.
   * @param remainingTime Seconds left on the timer.
   * @return The score.
   * @throws IllegalArgumentException if stats is null.
   */
  public static Score of(SessionStats stats, boolean won, float remainingTime) {
    if (stats == null) {
      throw new IllegalArgumentException("Stats must not be null");
    }
    int secondsLeft = (int) Math.max(0f, remainingTime);
    return new Score(
        won ? WIN_POINTS : 0,
        won ? secondsLeft * POINTS_PER_SECOND_LEFT : 0,
        stats.getHiddenEventsFound() * HIDDEN_EVENT_POINTS,
        stats.getEnemyKillScore(),
        Math.min(stats.getInteractions(), MAX_SCORED_INTERACTIONS) * INTERACTION_POINTS,
        Math.min(stats.getItemsUsed(), MAX_SCORED_ITEM_USES) * ITEM_USE_POINTS);
  }

  /**
   * Returns the total score.
   *
   * @return The sum of every part of the score.
   */
  public int total() {
    return win + timeBonus + hiddenEvents + geese + interactions + itemsUsed;
  }
}
