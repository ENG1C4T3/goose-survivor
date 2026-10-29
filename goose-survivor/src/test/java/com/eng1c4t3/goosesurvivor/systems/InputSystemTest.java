package com.eng1c4t3.goosesurvivor.systems;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.eng1c4t3.goosesurvivor.components.PlayerComponent;
import com.eng1c4t3.goosesurvivor.components.VelocityComponent;
import com.eng1c4t3.goosesurvivor.ecs.Engine;
import com.eng1c4t3.goosesurvivor.ecs.Entity;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InputSystemTest {

  private Engine engine;
  private InputSystem inputSystem;
  private Set<Integer> pressedKeys;

  @BeforeEach
  void setUp() {
    engine = new Engine();
    inputSystem = new InputSystem();
    engine.addSystem(inputSystem);

    pressedKeys = new HashSet<>();
    Gdx.input = (Input) Proxy.newProxyInstance(
        Input.class.getClassLoader(),
        new Class<?>[]{Input.class},
        (proxy, method, args) -> {
          if ("isKeyPressed".equals(method.getName()) && args != null && args.length == 1) {
            return pressedKeys.contains((Integer) args[0]);
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
  }

  @AfterEach
  void tearDown() {
    Gdx.input = null;
  }

  private Entity createPlayerEntity(float maxSpeed) {
    Entity player = new Entity();
    player.addComponent(new PlayerComponent());
    player.addComponent(new VelocityComponent(maxSpeed));
    return player;
  }

  @Test
  void testNoKeyPressedResultsInZeroVelocity() {
    Entity player = createPlayerEntity(200f);
    player.getComponent(VelocityComponent.class).velocity.set(50f, 50f);
    engine.addEntity(player);

    inputSystem.update(0.016f);

    VelocityComponent vel = player.getComponent(VelocityComponent.class);
    assertEquals(0f, vel.velocity.x, 1e-5f);
    assertEquals(0f, vel.velocity.y, 1e-5f);
  }

  @Test
  void testMoveUpWithKeyW() {
    Entity player = createPlayerEntity(200f);
    engine.addEntity(player);
    pressedKeys.add(Input.Keys.W);

    inputSystem.update(0.016f);

    VelocityComponent vel = player.getComponent(VelocityComponent.class);
    assertEquals(0f, vel.velocity.x, 1e-5f);
    assertEquals(200f, vel.velocity.y, 1e-5f);
  }

  @Test
  void testMoveUpWithUpArrowKey() {
    Entity player = createPlayerEntity(200f);
    engine.addEntity(player);
    pressedKeys.add(Input.Keys.UP);

    inputSystem.update(0.016f);

    VelocityComponent vel = player.getComponent(VelocityComponent.class);
    assertEquals(0f, vel.velocity.x, 1e-5f);
    assertEquals(200f, vel.velocity.y, 1e-5f);
  }

  @Test
  void testMoveDownWithKeyS() {
    Entity player = createPlayerEntity(200f);
    engine.addEntity(player);
    pressedKeys.add(Input.Keys.S);

    inputSystem.update(0.016f);

    VelocityComponent vel = player.getComponent(VelocityComponent.class);
    assertEquals(0f, vel.velocity.x, 1e-5f);
    assertEquals(-200f, vel.velocity.y, 1e-5f);
  }

  @Test
  void testMoveDownWithDownArrowKey() {
    Entity player = createPlayerEntity(200f);
    engine.addEntity(player);
    pressedKeys.add(Input.Keys.DOWN);

    inputSystem.update(0.016f);

    VelocityComponent vel = player.getComponent(VelocityComponent.class);
    assertEquals(0f, vel.velocity.x, 1e-5f);
    assertEquals(-200f, vel.velocity.y, 1e-5f);
  }

  @Test
  void testMoveLeftWithKeyA() {
    Entity player = createPlayerEntity(200f);
    engine.addEntity(player);
    pressedKeys.add(Input.Keys.A);

    inputSystem.update(0.016f);

    VelocityComponent vel = player.getComponent(VelocityComponent.class);
    assertEquals(-200f, vel.velocity.x, 1e-5f);
    assertEquals(0f, vel.velocity.y, 1e-5f);
  }

  @Test
  void testMoveLeftWithLeftArrowKey() {
    Entity player = createPlayerEntity(200f);
    engine.addEntity(player);
    pressedKeys.add(Input.Keys.LEFT);

    inputSystem.update(0.016f);

    VelocityComponent vel = player.getComponent(VelocityComponent.class);
    assertEquals(-200f, vel.velocity.x, 1e-5f);
    assertEquals(0f, vel.velocity.y, 1e-5f);
  }

  @Test
  void testMoveRightWithKeyD() {
    Entity player = createPlayerEntity(200f);
    engine.addEntity(player);
    pressedKeys.add(Input.Keys.D);

    inputSystem.update(0.016f);

    VelocityComponent vel = player.getComponent(VelocityComponent.class);
    assertEquals(200f, vel.velocity.x, 1e-5f);
    assertEquals(0f, vel.velocity.y, 1e-5f);
  }

  @Test
  void testMoveRightWithRightArrowKey() {
    Entity player = createPlayerEntity(200f);
    engine.addEntity(player);
    pressedKeys.add(Input.Keys.RIGHT);

    inputSystem.update(0.016f);

    VelocityComponent vel = player.getComponent(VelocityComponent.class);
    assertEquals(200f, vel.velocity.x, 1e-5f);
    assertEquals(0f, vel.velocity.y, 1e-5f);
  }

  @Test
  void testDiagonalMovementNormalized() {
    Entity player = createPlayerEntity(200f);
    engine.addEntity(player);
    pressedKeys.add(Input.Keys.W);
    pressedKeys.add(Input.Keys.D);

    inputSystem.update(0.016f);

    VelocityComponent vel = player.getComponent(VelocityComponent.class);
    float expected = (float) (200.0 / Math.sqrt(2.0));
    assertEquals(expected, vel.velocity.x, 1e-4f);
    assertEquals(expected, vel.velocity.y, 1e-4f);
    assertEquals(200f, vel.velocity.len(), 1e-4f);
  }

  @Test
  void testOppositeKeysCancelOut() {
    Entity player = createPlayerEntity(200f);
    engine.addEntity(player);
    pressedKeys.add(Input.Keys.W);
    pressedKeys.add(Input.Keys.S);
    pressedKeys.add(Input.Keys.A);
    pressedKeys.add(Input.Keys.D);

    inputSystem.update(0.016f);

    VelocityComponent vel = player.getComponent(VelocityComponent.class);
    assertEquals(0f, vel.velocity.x, 1e-5f);
    assertEquals(0f, vel.velocity.y, 1e-5f);
  }

  @Test
  void testNonPlayerEntityNotUpdated() {
    Entity nonPlayer = new Entity();
    VelocityComponent velocity = new VelocityComponent(100f);
    velocity.velocity.set(50f, 50f);
    nonPlayer.addComponent(velocity);
    engine.addEntity(nonPlayer);

    pressedKeys.add(Input.Keys.W);
    inputSystem.update(0.016f);

    assertEquals(50f, velocity.velocity.x, 1e-5f);
    assertEquals(50f, velocity.velocity.y, 1e-5f);
  }

  @Test
  void testPlayerWithoutVelocityComponentIgnored() {
    Entity player = new Entity();
    player.addComponent(new PlayerComponent());
    engine.addEntity(player);

    pressedKeys.add(Input.Keys.W);
    inputSystem.update(0.016f);
    // Should not throw exception
  }
}
