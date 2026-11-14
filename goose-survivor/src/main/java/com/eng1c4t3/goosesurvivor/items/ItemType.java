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
      TextureManager.COFFEE_TEXTURE, 18, 33, new HealEffect(100000f)),
  /** Unlimited uses; no damage, just knocks nearby geese away. */
  TRUMPET("Trumpet", "Trumpet", ItemType.UNLIMITED_USES, 90f,
      TextureManager.TRUMPET_TEXTURE, 37, 18, new KnockbackEffect(120f, 150f)),
  ENERGY_DRINK("Energy drink", "Energy drink", 1, 0.1f,
      TextureManager.ENERGY_DRINK_TEXTURE, 11, 21, new SpeedBoostEffect(1.1f, 45f)),
  /** Single use; placed as a decoy that geese attack instead of the player until it dies. */
  POPCORN("Popcorn", "Popcorn", 1, 0.1f,
      TextureManager.POPCORN_TEXTURE, 22, 22,
      new PlaceDecoyEffect(TextureManager.POPCORN_TEXTURE, 22f, 22f)),
  /** Unlimited uses; sprays a 1 second stream of food towards the mouse. Starting weapon. */
  FOOD_STREAM_LAUNCHER("Food Stream Launcher", "Launcher", ItemType.UNLIMITED_USES, 1.5f,
      TextureManager.FOOD_STREAM_LAUNCHER_TEXTURE, 34, 31,
      new FoodStreamEffect(15, 1f / 15f, 320f, 8f, 10f,
          TextureManager.SEED_PROJECTILE_TEXTURE)),
  /** Unlimited uses; fires a fan of 5 food shots towards the mouse at once. Dropped by the fox. */
  FOOD_SCATTER_LAUNCHER("Food Scatter Launcher", "Scatter", ItemType.UNLIMITED_USES, 1.5f,
      TextureManager.FOOD_SCATTER_LAUNCHER_TEXTURE, 54, 39, new ScatterShotEffect(5, 30f, 320f, 20f,
          TextureManager.NUT_PROJECTILE_TEXTURE)),
  /** Opens a locked door; using it from the hotbar does nothing. Dropped by the gradient goose. */
  KEY("Key", "Key", ItemType.UNLIMITED_USES, 0.1f,
      TextureManager.KEY_TEXTURE, 27, 23, (user, engine) -> { });

  /** Value of {@link #maxUses} for an item that can be used forever. */
  public static final int UNLIMITED_USES = -1;

  /** Width and height of a world pickup for an item that does not set its own size. */
  public static final int DEFAULT_PICKUP_SIZE = 24;

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
  /** Width of the item's world pickup, in pixels. */
  public final int width;
  /** Height of the item's world pickup, in pixels. */
  public final int height;
  /** What happens when the item is used. */
  public final ItemEffect effect;

  ItemType(String displayName, String shortName, int maxUses, float cooldown,
           String texture, ItemEffect effect) {
    this(displayName, shortName, maxUses, cooldown, texture,
        DEFAULT_PICKUP_SIZE, DEFAULT_PICKUP_SIZE, effect);
  }

  ItemType(String displayName, String shortName, int maxUses, float cooldown,
           String texture, int width, int height, ItemEffect effect) {
    if (width <= 0 || height <= 0) {
      throw new IllegalArgumentException("pickup width and height must be positive");
    }
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
    this.width = width;
    this.height = height;
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
