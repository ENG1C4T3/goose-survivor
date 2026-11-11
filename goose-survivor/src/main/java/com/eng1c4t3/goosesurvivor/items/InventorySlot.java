package com.eng1c4t3.goosesurvivor.items;

/**
 * One copy of an item held in an inventory, with its own uses and cooldown.
 *
 * <p>Two copies of the same item in different slots have independent cooldowns.
 */
public class InventorySlot {

  /** The kind of item in this slot. */
  public final ItemType item;
  /** Uses left, or {@link ItemType#UNLIMITED_USES}. */
  public int usesRemaining;
  /** Seconds until the item can be used again; ready when zero. */
  public float cooldownTimer;

  /**
   * Constructs a slot holding a fresh copy of an item, with full uses and no cooldown.
   *
   * @param item The kind of item.
   * @throws IllegalArgumentException if item is null.
   */
  public InventorySlot(ItemType item) {
    if (item == null) {
      throw new IllegalArgumentException("Item must not be null");
    }
    this.item = item;
    this.usesRemaining = item.maxUses;
  }

  /**
   * Returns whether the item can be used forever.
   *
   * @return {@code true} if the item has unlimited uses.
   */
  public boolean isUnlimited() {
    return usesRemaining == ItemType.UNLIMITED_USES;
  }

  /**
   * Returns whether the item is still cooling down.
   *
   * @return {@code true} if the cooldown timer is positive.
   */
  public boolean isOnCooldown() {
    return cooldownTimer > 0;
  }

  /**
   * Returns whether the item has no uses left.
   *
   * @return {@code true} if a limited item has run out of uses.
   */
  public boolean isDepleted() {
    return !isUnlimited() && usesRemaining <= 0;
  }

  /**
   * Returns whether the item can be used right now.
   *
   * @return {@code true} if the item has uses left and is not cooling down.
   */
  public boolean canUse() {
    return !isDepleted() && !isOnCooldown();
  }

  /**
   * Spends one use (unless unlimited) and starts the cooldown. Does not apply the effect.
   *
   * @throws IllegalStateException if the item cannot be used right now.
   */
  public void consumeUse() {
    if (!canUse()) {
      throw new IllegalStateException("Item cannot be used right now");
    }
    if (!isUnlimited()) {
      --usesRemaining;
    }
    cooldownTimer = item.cooldown;
  }

  /**
   * Counts the cooldown down, stopping at zero.
   *
   * @param deltaTime Seconds passed since the last frame.
   */
  public void tick(float deltaTime) {
    if (cooldownTimer > 0) {
      cooldownTimer = Math.max(0f, cooldownTimer - deltaTime);
    }
  }

  /**
   * Returns how much of the cooldown is left, for display.
   *
   * @return A value from 0 (ready) to 1 (just used).
   */
  public float cooldownFraction() {
    if (item.cooldown <= 0) {
      return 0f;
    }
    return Math.min(1f, cooldownTimer / item.cooldown);
  }
}
