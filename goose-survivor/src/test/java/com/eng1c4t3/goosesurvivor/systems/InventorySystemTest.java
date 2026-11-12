package com.eng1c4t3.goosesurvivor.systems;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.eng1c4t3.goosesurvivor.components.HealthComponent;
import com.eng1c4t3.goosesurvivor.components.InventoryComponent;
import com.eng1c4t3.goosesurvivor.components.PlayerComponent;
import com.eng1c4t3.goosesurvivor.components.TransformComponent;
import com.eng1c4t3.goosesurvivor.components.UseItemEventComponent;
import com.eng1c4t3.goosesurvivor.components.VelocityComponent;
import com.eng1c4t3.goosesurvivor.items.InventorySlot;
import com.eng1c4t3.goosesurvivor.items.ItemType;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the use item flow: InputSystem creates the event, InventorySystem uses the item and
 * EventCleanupSystem destroys the event.
 */
class InventorySystemTest {

  private Engine engine;
  private InventorySystem inventorySystem;
  private Set<Integer> justPressedKeys;
  private ImmutableArray<Entity> events;

  @BeforeEach
  void setUp() {
    justPressedKeys = new HashSet<>();
    Gdx.input = (Input) Proxy.newProxyInstance(
        Input.class.getClassLoader(),
        new Class<?>[]{Input.class},
        (proxy, method, args) -> {
          if ("isKeyJustPressed".equals(method.getName())) {
            return justPressedKeys.contains((Integer) args[0]);
          }
          if (method.getReturnType().equals(boolean.class)) {
            return false;
          }
          if (method.getReturnType().equals(int.class)) {
            return 0;
          }
          if (method.getReturnType().equals(float.class)) {
            return 0f;
          }
          if (method.getReturnType().equals(long.class)) {
            return 0L;
          }
          return null;
        }
    );

    engine = new Engine();
    InputSystem input = new InputSystem();
    input.priority = 0;
    inventorySystem = new InventorySystem();
    inventorySystem.priority = 1;
    engine.addSystem(new EventCleanupSystem());
    engine.addSystem(inventorySystem);
    engine.addSystem(input);
    events = engine.getEntitiesFor(Family.all(UseItemEventComponent.class).get());
  }

  @AfterEach
  void tearDown() {
    Gdx.input = null;
  }

  private Entity addPlayer(float health, ItemType... items) {
    Entity player = new Entity();
    player.add(new PlayerComponent());
    player.add(new VelocityComponent(100f));
    player.add(new TransformComponent(0, 0, 32f, 32f));
    HealthComponent hp = new HealthComponent(100f);
    hp.health = health;
    player.add(hp);
    InventoryComponent inventory = new InventoryComponent(InventoryComponent.DEFAULT_CAPACITY);
    for (ItemType item : items) {
      inventory.addItem(item);
    }
    player.add(inventory);
    engine.addEntity(player);
    return player;
  }

  private static float health(Entity entity) {
    return HealthComponent.MAPPER.get(entity).health;
  }

  private static InventorySlot slot(Entity entity, int index) {
    return InventoryComponent.MAPPER.get(entity).getSlot(index);
  }

  @Test
  void testItemKeyMapping() {
    assertEquals(Input.Keys.NUM_1, InputSystem.itemKey(0));
    assertEquals(Input.Keys.NUM_9, InputSystem.itemKey(8));
    assertThrows(IllegalArgumentException.class, () -> InputSystem.itemKey(9));
  }

  @Test
  void testPressingKeyUsesItemAndEventIsCleanedUp() {
    final Entity player = addPlayer(10f, ItemType.COFFEE);
    justPressedKeys.add(InputSystem.itemKey(0));

    engine.update(0.016f);

    // Coffee heals to full and is single use
    assertEquals(100f, health(player), 1e-5f);
    assertNull(slot(player, 0));
    assertEquals(0, events.size());
  }

  @Test
  void testItemCannotBeUsedDuringCooldown() {
    final Entity player = addPlayer(100f, ItemType.TRUMPET);
    justPressedKeys.add(InputSystem.itemKey(0));

    engine.update(0.016f);
    final float afterFirstUse = slot(player, 0).cooldownTimer;
    engine.update(0.016f);

    // Second press was ignored, so the cooldown just kept counting down
    assertEquals(afterFirstUse - 0.016f, slot(player, 0).cooldownTimer, 1e-4f);
  }

  @Test
  void testItemCanBeUsedAgainAfterCooldown() {
    final Entity player = addPlayer(100f, ItemType.TRUMPET);
    justPressedKeys.add(InputSystem.itemKey(0));

    engine.update(0.016f);
    engine.update(ItemType.TRUMPET.cooldown);

    // Used again on the second frame, so the cooldown restarted
    assertEquals(ItemType.TRUMPET.cooldown, slot(player, 0).cooldownTimer, 1e-4f);
  }

  @Test
  void testSingleUseItemIsRemovedAfterUse() {
    Entity player = addPlayer(10f, ItemType.COFFEE);

    assertTrue(inventorySystem.useItem(player, 0));

    assertEquals(100f, health(player), 1e-5f);
    assertNull(slot(player, 0));
    assertFalse(inventorySystem.useItem(player, 0));
  }

  @Test
  void testUnlimitedItemStaysAfterManyUses() {
    Entity player = addPlayer(100f, ItemType.TRUMPET);

    for (int i = 0; i < 20; ++i) {
      assertTrue(inventorySystem.useItem(player, 0));
      engine.update(ItemType.TRUMPET.cooldown);
    }

    assertEquals(ItemType.TRUMPET, slot(player, 0).item);
  }

  @Test
  void testEachSlotHasItsOwnCooldown() {
    Entity player = addPlayer(10f, ItemType.TRUMPET, ItemType.TRUMPET, ItemType.COFFEE);

    assertTrue(inventorySystem.useItem(player, 0));
    assertFalse(inventorySystem.useItem(player, 0));
    assertTrue(inventorySystem.useItem(player, 1));
    assertTrue(inventorySystem.useItem(player, 2));
  }

  @Test
  void testUsingEmptySlotDoesNothing() {
    final Entity player = addPlayer(10f, ItemType.COFFEE);
    justPressedKeys.add(InputSystem.itemKey(3));

    engine.update(0.016f);

    assertEquals(10f, health(player), 1e-5f);
    assertEquals(0, events.size());
  }

  @Test
  void testPlayerWithoutInventoryCreatesNoEvent() {
    Entity player = new Entity();
    player.add(new PlayerComponent());
    player.add(new VelocityComponent(100f));
    engine.addEntity(player);
    justPressedKeys.add(InputSystem.itemKey(0));

    engine.update(0.016f);

    assertFalse(inventorySystem.useItem(player, 0));
  }
}
