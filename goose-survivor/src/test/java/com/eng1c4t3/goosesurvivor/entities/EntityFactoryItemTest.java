package com.eng1c4t3.goosesurvivor.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.eng1c4t3.goosesurvivor.components.CollisionComponent;
import com.eng1c4t3.goosesurvivor.components.InteractableComponent;
import com.eng1c4t3.goosesurvivor.components.InventoryComponent;
import com.eng1c4t3.goosesurvivor.components.ItemPickupComponent;
import com.eng1c4t3.goosesurvivor.components.TextureComponent;
import com.eng1c4t3.goosesurvivor.items.ItemType;
import org.junit.jupiter.api.Test;

class EntityFactoryItemTest {

  @Test
  void playerHasEmptyDefaultInventory() {
    Entity player = EntityFactory.createPlayer(0f, 0f, new TextureRegion());
    InventoryComponent inventory = InventoryComponent.MAPPER.get(player);

    assertNotNull(inventory);
    assertEquals(InventoryComponent.DEFAULT_CAPACITY, inventory.capacity());
    assertFalse(inventory.isFull());
    assertNull(inventory.getSlot(0));
  }

  @Test
  void itemPickupIsSingleUseInteractableWithoutCollision() {
    TextureRegion region = new TextureRegion();
    Entity pickup = EntityFactory.createItemPickup(ItemType.COFFEE, 10f, 20f, region);

    assertSame(region, TextureComponent.MAPPER.get(pickup).region);
    assertEquals(ItemType.COFFEE, ItemPickupComponent.MAPPER.get(pickup).item);
    InteractableComponent interactable = InteractableComponent.MAPPER.get(pickup);
    assertNotNull(interactable);
    assertTrue(interactable.singleUse);
    assertTrue(interactable.promptText.contains(ItemType.COFFEE.displayName));
    assertNull(CollisionComponent.MAPPER.get(pickup));
  }
}
