package com.eng1c4t3.goosesurvivor;

/**
 * Records item usage and hidden event progress for one play through.
 */
public class SessionStats {
  private int itemsUsed;
  private int eventsTriggered;
  private int eventsCompleted;

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
}
