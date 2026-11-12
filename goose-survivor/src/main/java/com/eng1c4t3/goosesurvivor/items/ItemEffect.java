package com.eng1c4t3.goosesurvivor.items;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

/**
 * What happens when an item is used.
 *
 * <p>Effects are stateless and shared by every copy of an item, so all per-copy state (uses
 * left, cooldown) lives in {@link InventorySlot} instead.
 */
@FunctionalInterface
public interface ItemEffect {

  /**
   * Applies the effect.
   *
   * @param user The entity using the item (normally the player).
   * @param engine The engine the user belongs to, for effects that touch other entities.
   */
  void apply(Entity user, Engine engine);
}
