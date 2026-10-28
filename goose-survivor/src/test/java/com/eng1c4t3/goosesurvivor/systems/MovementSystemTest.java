package com.eng1c4t3.goosesurvivor.systems;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.eng1c4t3.goosesurvivor.Engine;
import com.eng1c4t3.goosesurvivor.Entity;
import com.eng1c4t3.goosesurvivor.components.TransformComponent;
import com.eng1c4t3.goosesurvivor.components.VelocityComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MovementSystemTest {

  private Engine engine;
  private MovementSystem movementSystem;

  @BeforeEach
  void setUp() {
    engine = new Engine();
    movementSystem = new MovementSystem();
    engine.addSystem(movementSystem);
  }

  @Test
  void testEntityMovement() {
    Entity entity = new Entity();
    TransformComponent transform = new TransformComponent(10f, 20f);
    VelocityComponent velocity = new VelocityComponent(100f);
    velocity.velocity.set(50f, -30f);

    entity.addComponent(transform);
    entity.addComponent(velocity);
    engine.addEntity(entity);

    movementSystem.update(0.5f);

    assertEquals(35f, transform.position.x, 1e-5f);
    assertEquals(5f, transform.position.y, 1e-5f);
  }

  @Test
  void testStationaryEntityDoesNotMove() {
    Entity entity = new Entity();
    TransformComponent transform = new TransformComponent(100f, 200f);
    VelocityComponent velocity = new VelocityComponent(100f);
    velocity.velocity.set(0f, 0f);

    entity.addComponent(transform);
    entity.addComponent(velocity);
    engine.addEntity(entity);

    movementSystem.update(1.0f);

    assertEquals(100f, transform.position.x, 1e-5f);
    assertEquals(200f, transform.position.y, 1e-5f);
  }

  @Test
  void testEntityWithoutVelocityComponentNotModified() {
    Entity entity = new Entity();
    TransformComponent transform = new TransformComponent(50f, 60f);
    entity.addComponent(transform);
    engine.addEntity(entity);

    movementSystem.update(1.0f);

    assertEquals(50f, transform.position.x, 1e-5f);
    assertEquals(60f, transform.position.y, 1e-5f);
  }

  @Test
  void testEntityWithoutTransformComponentIgnored() {
    Entity entity = new Entity();
    VelocityComponent velocity = new VelocityComponent(100f);
    velocity.velocity.set(20f, 30f);
    entity.addComponent(velocity);
    engine.addEntity(entity);

    movementSystem.update(1.0f);

    assertEquals(20f, velocity.velocity.x, 1e-5f);
    assertEquals(30f, velocity.velocity.y, 1e-5f);
  }

  @Test
  void testMultipleEntitiesMovement() {
    Entity entity1 = new Entity();
    TransformComponent transform1 = new TransformComponent(0f, 0f);
    VelocityComponent velocity1 = new VelocityComponent(100f);
    velocity1.velocity.set(10f, 20f);
    entity1.addComponent(transform1);
    entity1.addComponent(velocity1);

    Entity entity2 = new Entity();
    TransformComponent transform2 = new TransformComponent(50f, 50f);
    VelocityComponent velocity2 = new VelocityComponent(100f);
    velocity2.velocity.set(-5f, 15f);
    entity2.addComponent(transform2);
    entity2.addComponent(velocity2);

    Entity entity3 = new Entity();
    TransformComponent transform3 = new TransformComponent(100f, 100f);
    entity3.addComponent(transform3);

    engine.addEntity(entity1);
    engine.addEntity(entity2);
    engine.addEntity(entity3);

    movementSystem.update(2.0f);

    assertEquals(20f, transform1.position.x, 1e-5f);
    assertEquals(40f, transform1.position.y, 1e-5f);

    assertEquals(40f, transform2.position.x, 1e-5f);
    assertEquals(80f, transform2.position.y, 1e-5f);

    assertEquals(100f, transform3.position.x, 1e-5f);
    assertEquals(100f, transform3.position.y, 1e-5f);
  }
}
