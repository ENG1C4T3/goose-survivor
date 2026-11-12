package com.eng1c4t3.goosesurvivor.systems;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.eng1c4t3.goosesurvivor.components.InteractableComponent;
import com.eng1c4t3.goosesurvivor.components.TransformComponent;
import com.eng1c4t3.goosesurvivor.entities.EntityFactory;
import com.eng1c4t3.goosesurvivor.items.ItemType;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests the lookup and text behind the HUD's interact prompt popup.
 */
class InteractPromptTest {

  private static Entity player(float x, float y) {
    Entity player = new Entity();
    player.add(new TransformComponent(x, y, 32f, 32f));
    return player;
  }

  private static Entity interactable(float x, float y, float range) {
    Entity entity = new Entity();
    entity.add(new TransformComponent(x, y, 32f, 32f));
    entity.add(new InteractableComponent(range, "Open"));
    return entity;
  }

  @Test
  void testFindsNearestInRange() {
    Entity far = interactable(45, 0, 100f);
    Entity near = interactable(20, 0, 100f);

    assertSame(near, InputSystem.findNearestInteractable(player(0, 0), List.of(far, near)));
  }

  @Test
  void testNothingInRangeReturnsNull() {
    Entity chest = interactable(200, 0, 50f);

    assertNull(InputSystem.findNearestInteractable(player(0, 0), List.of(chest)));
  }

  @Test
  void testSkipsEntitiesThatAreNotInteractable() {
    Entity rock = new Entity();
    rock.add(new TransformComponent(10, 0, 32f, 32f));
    Entity chest = interactable(40, 0, 50f);

    assertSame(chest, InputSystem.findNearestInteractable(player(0, 0), List.of(rock, chest)));
  }

  @Test
  void testPlayerWithoutPositionReturnsNull() {
    Entity chest = interactable(0, 0, 50f);

    assertNull(InputSystem.findNearestInteractable(new Entity(), List.of(chest)));
  }

  @Test
  void testPromptShowsKeyAndAction() {
    assertEquals("[E] Open", InputSystem.interactPrompt(new InteractableComponent(10f, "Open")));
  }

  @Test
  void testFactoryPrompts() {
    Entity pickup = EntityFactory.createItemPickup(ItemType.COFFEE, 0, 0, new TextureRegion());
    Entity pack = EntityFactory.createHealthPack(0, 0, 25f, new TextureRegion());

    assertEquals("[E] Pick up Coffee",
        InputSystem.interactPrompt(InteractableComponent.MAPPER.get(pickup)));
    assertEquals("[E] Heal",
        InputSystem.interactPrompt(InteractableComponent.MAPPER.get(pack)));
  }
}
