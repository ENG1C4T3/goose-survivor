package com.eng1c4t3.goosesurvivor.systems;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.eng1c4t3.goosesurvivor.components.CollisionComponent;
import com.eng1c4t3.goosesurvivor.components.CollisionComponent.Type;
import com.eng1c4t3.goosesurvivor.components.TransformComponent;
import com.eng1c4t3.goosesurvivor.components.VelocityComponent;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CollisionListenerTest {

  private Engine engine;
  private CollisionSystem collisionSystem;
  private List<Entity[]> collisions;
  private CollisionListener recorder;

  @BeforeEach
  void setUp() {
    engine = new Engine();
    collisionSystem = new CollisionSystem();
    engine.addSystem(collisionSystem);
    collisions = new ArrayList<>();
    recorder = (a, b) -> collisions.add(new Entity[]{a, b});
  }

  private Entity addMover(float x, float y) {
    Entity mover = new Entity();
    mover.add(new TransformComponent(x, y, 10f, 10f));
    mover.add(new VelocityComponent(100f));
    mover.add(new CollisionComponent(Type.SOLID));
    engine.addEntity(mover);
    return mover;
  }

  private Entity addStatic(float x, float y, Type type) {
    Entity entity = new Entity();
    entity.add(new TransformComponent(x, y, 10f, 10f));
    entity.add(new CollisionComponent(type));
    engine.addEntity(entity);
    return entity;
  }

  @Test
  void testListenerNotifiedOfSolidCollision() {
    collisionSystem.addCollisionListener(recorder);
    Entity mover = addMover(0, 0);
    final Entity wall = addStatic(5, 0, Type.SOLID);

    collisionSystem.update(0.016f);

    assertEquals(1, collisions.size());
    assertSame(mover, collisions.get(0)[0]);
    assertSame(wall, collisions.get(0)[1]);
  }

  @Test
  void testTriggerIsReportedButNotResolved() {
    collisionSystem.addCollisionListener(recorder);
    final Entity mover = addMover(0, 0);
    Entity trigger = addStatic(5, 0, Type.TRIGGER);

    collisionSystem.update(0.016f);

    assertEquals(1, collisions.size());
    assertSame(trigger, collisions.get(0)[1]);
    assertEquals(0f, TransformComponent.MAPPER.get(mover).position.x, 1e-5f);
  }

  @Test
  void testNoNotificationWithoutOverlap() {
    collisionSystem.addCollisionListener(recorder);
    addMover(0, 0);
    addStatic(50, 50, Type.SOLID);

    collisionSystem.update(0.016f);

    assertEquals(0, collisions.size());
  }

  @Test
  void testRemovedListenerNotNotified() {
    collisionSystem.addCollisionListener(recorder);
    collisionSystem.removeCollisionListener(recorder);
    addMover(0, 0);
    addStatic(5, 0, Type.SOLID);

    collisionSystem.update(0.016f);

    assertEquals(0, collisions.size());
  }

  @Test
  void testListenerCanRemoveItselfDuringNotification() {
    CollisionListener once = new CollisionListener() {
      @Override
      public void onCollision(Entity a, Entity b) {
        collisions.add(new Entity[]{a, b});
        collisionSystem.removeCollisionListener(this);
      }
    };
    collisionSystem.addCollisionListener(once);
    addMover(0, 0);
    addStatic(5, 0, Type.SOLID);
    addStatic(0, 5, Type.TRIGGER);

    collisionSystem.update(0.016f);

    assertEquals(1, collisions.size());
  }
}
