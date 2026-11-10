package com.eng1c4t3.goosesurvivor.systems;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.eng1c4t3.goosesurvivor.components.HealOnInteractComponent;
import com.eng1c4t3.goosesurvivor.components.HealthComponent;
import com.eng1c4t3.goosesurvivor.components.InteractableComponent;
import com.eng1c4t3.goosesurvivor.components.PlayerComponent;
import com.eng1c4t3.goosesurvivor.components.TransformComponent;
import com.eng1c4t3.goosesurvivor.components.TriggerEventComponent;
import com.eng1c4t3.goosesurvivor.components.VelocityComponent;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the event-as-entity interaction flow: InputSystem creates the event,
 * InteractionSystem handles it and EventCleanupSystem destroys it.
 */
class InteractionFlowTest {

  private Engine engine;
  private Set<Integer> justPressedKeys;
  private List<TriggerEventComponent> handled;
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

    handled = new ArrayList<>();
    engine = new Engine();
    // Added out of order on purpose: EventCleanupSystem must still run last
    engine.addSystem(new EventCleanupSystem());
    engine.addSystem(new InputSystem());
    engine.addSystem(new InteractionSystem() {
      @Override
      protected void handleInteract(TriggerEventComponent event) {
        handled.add(event);
        super.handleInteract(event);
      }
    });
    events = engine.getEntitiesFor(Family.all(TriggerEventComponent.class).get());
  }

  @AfterEach
  void tearDown() {
    Gdx.input = null;
  }

  private Entity addPlayer(float x, float y) {
    Entity player = new Entity();
    player.add(new PlayerComponent());
    player.add(new VelocityComponent(100f));
    player.add(new TransformComponent(x, y, 32f, 32f));
    engine.addEntity(player);
    return player;
  }

  private Entity addInteractable(float x, float y, float range) {
    Entity interactable = new Entity();
    interactable.add(new TransformComponent(x, y, 32f, 32f));
    interactable.add(new InteractableComponent(range, "Press E"));
    engine.addEntity(interactable);
    return interactable;
  }

  @Test
  void testInteractInRangeIsHandledOnceAndCleanedUp() {
    final Entity player = addPlayer(0, 0);
    final Entity chest = addInteractable(40, 0, 50f);
    justPressedKeys.add(InputSystem.INTERACT_KEY);

    engine.update(0.016f);

    assertEquals(1, handled.size());
    assertSame(player, handled.get(0).sourcePlayer);
    assertSame(chest, handled.get(0).targetInteractable);
    assertEquals(TriggerEventComponent.ActionType.INTERACT, handled.get(0).action);
    assertEquals(0, events.size());
  }

  @Test
  void testInteractOutOfRangeCreatesNoEvent() {
    addPlayer(0, 0);
    addInteractable(200, 0, 50f);
    justPressedKeys.add(InputSystem.INTERACT_KEY);

    engine.update(0.016f);

    assertEquals(0, handled.size());
  }

  @Test
  void testNoKeyPressCreatesNoEvent() {
    addPlayer(0, 0);
    addInteractable(40, 0, 50f);

    engine.update(0.016f);

    assertEquals(0, handled.size());
  }

  @Test
  void testNearestInteractableIsTargeted() {
    addPlayer(0, 0);
    addInteractable(45, 0, 100f);
    final Entity nearer = addInteractable(20, 0, 100f);
    justPressedKeys.add(InputSystem.INTERACT_KEY);

    engine.update(0.016f);

    assertEquals(1, handled.size());
    assertSame(nearer, handled.get(0).targetInteractable);
  }

  private Entity addHealthPack(float x, float y, float amount, boolean singleUse) {
    Entity pack = new Entity();
    pack.add(new TransformComponent(x, y, 32f, 32f));
    pack.add(new InteractableComponent(50f, "Press E to heal", singleUse));
    pack.add(new HealOnInteractComponent(amount));
    engine.addEntity(pack);
    return pack;
  }

  private HealthComponent giveHealth(Entity player, float health, float maxHealth) {
    HealthComponent hp = new HealthComponent(maxHealth);
    hp.health = health;
    player.add(hp);
    return hp;
  }

  @Test
  void testHealthPackHealsPlayerAndIsRemoved() {
    Entity player = addPlayer(0, 0);
    HealthComponent hp = giveHealth(player, 50f, 100f);
    final Entity pack = addHealthPack(40, 0, 25f, true);
    justPressedKeys.add(InputSystem.INTERACT_KEY);

    engine.update(0.016f);

    assertEquals(75f, hp.health, 1e-5f);
    assertFalse(engine.getEntities().contains(pack, true));
  }

  @Test
  void testHealIsCappedAtMaxHealth() {
    Entity player = addPlayer(0, 0);
    final HealthComponent hp = giveHealth(player, 90f, 100f);
    addHealthPack(40, 0, 25f, true);
    justPressedKeys.add(InputSystem.INTERACT_KEY);

    engine.update(0.016f);

    assertEquals(100f, hp.health, 1e-5f);
  }

  @Test
  void testReusableInteractableIsNotRemoved() {
    Entity player = addPlayer(0, 0);
    final HealthComponent hp = giveHealth(player, 10f, 100f);
    final Entity fountain = addHealthPack(40, 0, 25f, false);
    justPressedKeys.add(InputSystem.INTERACT_KEY);

    engine.update(0.016f);
    engine.update(0.016f);

    assertEquals(60f, hp.health, 1e-5f);
    assertTrue(engine.getEntities().contains(fountain, true));
  }

  @Test
  void testSingleUseInteractableOnlyUsedOncePerFrame() {
    final HealthComponent hpA = giveHealth(addPlayer(0, 0), 50f, 100f);
    final HealthComponent hpB = giveHealth(addPlayer(10, 0), 50f, 100f);
    addHealthPack(40, 0, 25f, true);
    justPressedKeys.add(InputSystem.INTERACT_KEY);

    engine.update(0.016f);

    assertEquals(1, handled.size());
    assertEquals(125f, hpA.health + hpB.health, 1e-5f);
  }

  @Test
  void testEventForNonInteractableTargetIsIgnored() {
    Entity player = addPlayer(0, 0);
    Entity notInteractable = new Entity();
    Entity event = new Entity();
    event.add(new TriggerEventComponent(
        player, notInteractable, TriggerEventComponent.ActionType.INTERACT));
    engine.addEntity(event);

    engine.update(0.016f);

    assertEquals(0, handled.size());
    assertEquals(0, events.size());
  }
}
