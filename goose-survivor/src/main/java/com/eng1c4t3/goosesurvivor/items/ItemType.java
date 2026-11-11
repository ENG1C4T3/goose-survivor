package com.eng1c4t3.goosesurvivor.items;

import com.eng1c4t3.goosesurvivor.TextureManager;

/**
 * Every kind of item in the game, with the stats shared by all copies of it.
 *
 * <p>Every item can be used. An item either has a fixed number of uses, after which it is
 * removed from the inventory, or {@link #UNLIMITED_USES}. Every item also has its own cooldown,
 * tracked separately for each inventory slot.
 *
 * <p>To add an item, add a constant here; nothing else needs to change.
 */
public enum ItemType {
  /** Single use; heals to full (the heal is capped at max health). */
  COFFEE("Coffee", "Coffee", 1, 0.1f,
      TextureManager.COFFEE_TEXTURE, new HealEffect(100000f)),
  /** Unlimited uses; no damage, just knocks nearby geese away. */
  TRUMPET("Trumpet", "Trumpet", ItemType.UNLIMITED_USES, 90f,
      TextureManager.TRUMPET_TEXTURE, new KnockbackEffect(120f, 150f)),
  ENERGY_DRINK("Energy drink", "Energy drink", 1, 0.1f,
      TextureManager.ENERGY_DRINK_TEXTURE, new SpeedBoostEffect(1.1f, 10f));

  /** Value of {@link #maxUses} for an item that can be used forever. */
  public static final int UNLIMITED_USES = -1;

  /** Full name, shown in pickup prompts. */
  public final String displayName;
  /** Short name that fits in a hotbar slot. */
  public final String shortName;
  /** Uses a freshly picked up item has, or {@link #UNLIMITED_USES}. */
  public final int maxUses;
  /** Seconds before the item can be used again after a use. */
  public final float cooldown;
  /** File name of the texture used for the item's world pickup. */
  public final String texture;
  /** What happens when the item is used. */
  public final ItemEffect effect;

  ItemType(String displayName, String shortName, int maxUses, float cooldown,
           String texture, ItemEffect effect) {
    if (maxUses <= 0 && maxUses != UNLIMITED_USES) {
      throw new IllegalArgumentException("maxUses must be positive or UNLIMITED_USES");
    }
    if (cooldown < 0) {
      throw new IllegalArgumentException("cooldown must not be negative");
    }
    this.displayName = displayName;
    this.shortName = shortName;
    this.maxUses = maxUses;
    this.cooldown = cooldown;
    this.texture = texture;
    this.effect = effect;
  }

  /**
   * Returns whether the item can be used forever.
   *
   * @return {@code true} if the item has unlimited uses.
   */
  public boolean isUnlimited() {
    return maxUses == UNLIMITED_USES;
  }
}
