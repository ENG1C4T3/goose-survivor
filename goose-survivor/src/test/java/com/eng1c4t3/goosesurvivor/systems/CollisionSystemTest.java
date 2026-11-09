package com.eng1c4t3.goosesurvivor.systems;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.eng1c4t3.goosesurvivor.components.CollisionComponent;
import com.eng1c4t3.goosesurvivor.components.CollisionComponent.Type;
import com.eng1c4t3.goosesurvivor.components.EnemyComponent;
import com.eng1c4t3.goosesurvivor.components.HealthComponent;
import com.eng1c4t3.goosesurvivor.components.PlayerComponent;
import com.eng1c4t3.goosesurvivor.components.TransformComponent;
import com.eng1c4t3.goosesurvivor.components.VelocityComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CollisionSystemTest {

  private Engine engine;
  private CollisionSystem collisionSystem;

  @BeforeEach
  void setUp() {
    engine = new Engine();
    collisionSystem = new CollisionSystem();
    engine.addSystem(collisionSystem);
  }

  @Test
  void testNoCollisionWhenEntitiesDoNotOverlap() {
    Entity mover = new Entity();
    TransformComponent moverTransform = new TransformComponent(0f, 0f, 10f, 10f);
    VelocityComponent moverVelocity = new VelocityComponent(100f);
    moverVelocity.velocity.set(50f, 50f);
    CollisionComponent moverCollision = new CollisionComponent(Type.SOLID);
    mover.add(moverTransform);
    mover.add(moverVelocity);
    mover.add(moverCollision);

    Entity solid = new Entity();
    TransformComponent solidTransform = new TransformComponent(50f, 50f, 10f, 10f);
    CollisionComponent solidCollision = new CollisionComponent(Type.SOLID);
    solid.add(solidTransform);
    solid.add(solidCollision);

    engine.addEntity(mover);
    engine.addEntity(solid);

    collisionSystem.update(0.1f);

    assertEquals(0f, moverTransform.position.x, 1e-5f);
    assertEquals(0f, moverTransform.position.y, 1e-5f);
    assertEquals(50f, moverVelocity.velocity.x, 1e-5f);
    assertEquals(50f, moverVelocity.velocity.y, 1e-5f);
  }

  @Test
  void testSolidCollisionFromLeft() {
    Entity mover = new Entity();
    TransformComponent moverTransform = new TransformComponent(10f, 10f, 10f, 10f);
    VelocityComponent moverVelocity = new VelocityComponent(100f);
    moverVelocity.velocity.set(20f, 15f);
    CollisionComponent moverCollision = new CollisionComponent(Type.SOLID);
    mover.add(moverTransform);
    mover.add(moverVelocity);
    mover.add(moverCollision);

    Entity solid = new Entity();
    TransformComponent solidTransform = new TransformComponent(18f, 10f, 10f, 10f);
    CollisionComponent solidCollision = new CollisionComponent(Type.SOLID);
    solid.add(solidTransform);
    solid.add(solidCollision);

    engine.addEntity(mover);
    engine.addEntity(solid);

    collisionSystem.update(0.1f);

    // Overlap on X is 2 (from 18 to 20), on Y is 10. Minimal overlap is X.
    // Mover is to the left of solid, so mover.x becomes 10 - 2 = 8.
    assertEquals(8f, moverTransform.position.x, 1e-5f);
    assertEquals(10f, moverTransform.position.y, 1e-5f);
    assertEquals(0f, moverVelocity.velocity.x, 1e-5f);
    assertEquals(15f, moverVelocity.velocity.y, 1e-5f);
  }

  @Test
  void testSolidCollisionFromRight() {
    Entity mover = new Entity();
    TransformComponent moverTransform = new TransformComponent(25f, 10f, 10f, 10f);
    VelocityComponent moverVelocity = new VelocityComponent(100f);
    moverVelocity.velocity.set(-20f, 10f);
    CollisionComponent moverCollision = new CollisionComponent(Type.SOLID);
    mover.add(moverTransform);
    mover.add(moverVelocity);
    mover.add(moverCollision);

    Entity solid = new Entity();
    TransformComponent solidTransform = new TransformComponent(20f, 10f, 10f, 10f);
    CollisionComponent solidCollision = new CollisionComponent(Type.SOLID);
    solid.add(solidTransform);
    solid.add(solidCollision);

    engine.addEntity(mover);
    engine.addEntity(solid);

    collisionSystem.update(0.1f);

    // Overlap on X is (20 + 10) - 25 = 5. Overlap on Y is 10. Minimal overlap is X.
    // Mover is to the right of solid, so mover.x becomes 25 + 5 = 30.
    assertEquals(30f, moverTransform.position.x, 1e-5f);
    assertEquals(10f, moverTransform.position.y, 1e-5f);
    assertEquals(0f, moverVelocity.velocity.x, 1e-5f);
    assertEquals(10f, moverVelocity.velocity.y, 1e-5f);
  }

  @Test
  void testSolidCollisionFromBottom() {
    Entity mover = new Entity();
    TransformComponent moverTransform = new TransformComponent(10f, 10f, 10f, 10f);
    VelocityComponent moverVelocity = new VelocityComponent(100f);
    moverVelocity.velocity.set(15f, 20f);
    CollisionComponent moverCollision = new CollisionComponent(Type.SOLID);
    mover.add(moverTransform);
    mover.add(moverVelocity);
    mover.add(moverCollision);

    Entity solid = new Entity();
    TransformComponent solidTransform = new TransformComponent(10f, 18f, 10f, 10f);
    CollisionComponent solidCollision = new CollisionComponent(Type.SOLID);
    solid.add(solidTransform);
    solid.add(solidCollision);

    engine.addEntity(mover);
    engine.addEntity(solid);

    collisionSystem.update(0.1f);

    // Overlap on X is 10, overlap on Y is (10 + 10) - 18 = 2. Minimal overlap is Y.
    // Mover is below solid, so mover.y becomes 10 - 2 = 8.
    assertEquals(10f, moverTransform.position.x, 1e-5f);
    assertEquals(8f, moverTransform.position.y, 1e-5f);
    assertEquals(15f, moverVelocity.velocity.x, 1e-5f);
    assertEquals(0f, moverVelocity.velocity.y, 1e-5f);
  }

  @Test
  void testSolidCollisionFromTop() {
    Entity mover = new Entity();
    TransformComponent moverTransform = new TransformComponent(10f, 25f, 10f, 10f);
    VelocityComponent moverVelocity = new VelocityComponent(100f);
    moverVelocity.velocity.set(10f, -20f);
    CollisionComponent moverCollision = new CollisionComponent(Type.SOLID);
    mover.add(moverTransform);
    mover.add(moverVelocity);
    mover.add(moverCollision);

    Entity solid = new Entity();
    TransformComponent solidTransform = new TransformComponent(10f, 20f, 10f, 10f);
    CollisionComponent solidCollision = new CollisionComponent(Type.SOLID);
    solid.add(solidTransform);
    solid.add(solidCollision);

    engine.addEntity(mover);
    engine.addEntity(solid);

    collisionSystem.update(0.1f);

    // Overlap on X is 10, overlap on Y is (20 + 10) - 25 = 5. Minimal overlap is Y.
    // Mover is above solid, so mover.y becomes 25 + 5 = 30.
    assertEquals(10f, moverTransform.position.x, 1e-5f);
    assertEquals(30f, moverTransform.position.y, 1e-5f);
    assertEquals(10f, moverVelocity.velocity.x, 1e-5f);
    assertEquals(0f, moverVelocity.velocity.y, 1e-5f);
  }

  @Test
  void testNonSolidCollisionDoesNotAlterPositionOrVelocity() {
    for (Type type : new Type[]{
        Type.TRIGGER}) {
      Entity mover = new Entity();
      TransformComponent moverTransform = new TransformComponent(10f, 10f, 10f, 10f);
      VelocityComponent moverVelocity = new VelocityComponent(100f);
      moverVelocity.velocity.set(30f, 40f);
      CollisionComponent moverCollision = new CollisionComponent(Type.SOLID);
      mover.add(moverTransform);
      mover.add(moverVelocity);
      mover.add(moverCollision);

      Entity other = new Entity();
      TransformComponent otherTransform = new TransformComponent(15f, 15f, 10f, 10f);
      CollisionComponent otherCollision = new CollisionComponent(type);
      other.add(otherTransform);
      other.add(otherCollision);

      Engine localEngine = new Engine();
      CollisionSystem localSystem = new CollisionSystem();
      localEngine.addSystem(localSystem);
      localEngine.addEntity(mover);
      localEngine.addEntity(other);

      localSystem.update(0.1f);

      assertEquals(10f, moverTransform.position.x, 1e-5f);
      assertEquals(10f, moverTransform.position.y, 1e-5f);
      assertEquals(30f, moverVelocity.velocity.x, 1e-5f);
      assertEquals(40f, moverVelocity.velocity.y, 1e-5f);
    }
  }

  @Test
  void testSelfCollisionIsSkipped() {
    Entity entity = new Entity();
    TransformComponent transform = new TransformComponent(10f, 10f, 10f, 10f);
    VelocityComponent velocity = new VelocityComponent(100f);
    velocity.velocity.set(10f, 20f);
    CollisionComponent collision = new CollisionComponent(Type.SOLID);
    entity.add(transform);
    entity.add(velocity);
    entity.add(collision);

    engine.addEntity(entity);
    collisionSystem.update(0.1f);

    assertEquals(10f, transform.position.x, 1e-5f);
    assertEquals(10f, transform.position.y, 1e-5f);
    assertEquals(10f, velocity.velocity.x, 1e-5f);
    assertEquals(20f, velocity.velocity.y, 1e-5f);
  }

  @Test
  void testStaticEntityWithoutVelocityDoesNotMoveOnCollision() {
    Entity mover = new Entity();
    TransformComponent moverTransform = new TransformComponent(10f, 10f, 10f, 10f);
    VelocityComponent moverVelocity = new VelocityComponent(100f);
    moverVelocity.velocity.set(20f, 0f);
    CollisionComponent moverCollision = new CollisionComponent(Type.SOLID);
    mover.add(moverTransform);
    mover.add(moverVelocity);
    mover.add(moverCollision);

    Entity staticSolid = new Entity();
    TransformComponent solidTransform = new TransformComponent(18f, 10f, 10f, 10f);
    CollisionComponent solidCollision = new CollisionComponent(Type.SOLID);
    staticSolid.add(solidTransform);
    staticSolid.add(solidCollision);

    engine.addEntity(mover);
    engine.addEntity(staticSolid);

    collisionSystem.update(0.1f);

    // Static solid entity should remain at its position
    assertEquals(18f, solidTransform.position.x, 1e-5f);
    assertEquals(10f, solidTransform.position.y, 1e-5f);
    // Mover should be resolved
    assertEquals(8f, moverTransform.position.x, 1e-5f);
  }

  @Test
  void testEntityWithoutCollisionComponentIgnored() {
    Entity mover = new Entity();
    TransformComponent moverTransform = new TransformComponent(10f, 10f, 10f, 10f);
    VelocityComponent moverVelocity = new VelocityComponent(100f);
    moverVelocity.velocity.set(20f, 0f);
    mover.add(moverTransform);
    mover.add(moverVelocity);

    Entity solid = new Entity();
    TransformComponent solidTransform = new TransformComponent(18f, 10f, 10f, 10f);
    CollisionComponent solidCollision = new CollisionComponent(Type.SOLID);
    solid.add(solidTransform);
    solid.add(solidCollision);

    engine.addEntity(mover);
    engine.addEntity(solid);

    collisionSystem.update(0.1f);

    // Mover has no collision component, so it should not be processed
    assertEquals(10f, moverTransform.position.x, 1e-5f);
    assertEquals(20f, moverVelocity.velocity.x, 1e-5f);
  }

  @Test
  void testEnemyCollidingWithPlayerDoesNotMovePlayer() {
    Entity player = new Entity();
    TransformComponent playerTransform = new TransformComponent(10f, 10f, 10f, 10f);
    VelocityComponent playerVelocity = new VelocityComponent(100f);
    player.add(new PlayerComponent());
    player.add(playerTransform);
    player.add(playerVelocity);
    player.add(new CollisionComponent(Type.SOLID));

    Entity enemy = new Entity();
    TransformComponent enemyTransform = new TransformComponent(18f, 10f, 10f, 10f);
    VelocityComponent enemyVelocity = new VelocityComponent(100f);
    enemyVelocity.velocity.set(-50f, 0f);
    enemy.add(enemyTransform);
    enemy.add(enemyVelocity);
    enemy.add(new CollisionComponent(Type.SOLID));

    engine.addEntity(player);
    engine.addEntity(enemy);

    collisionSystem.update(0.1f);

    // Player stays put with no input; the enemy is pushed out instead
    assertEquals(10f, playerTransform.position.x, 1e-5f);
    assertEquals(10f, playerTransform.position.y, 1e-5f);
    assertEquals(20f, enemyTransform.position.x, 1e-5f);
    assertEquals(0f, enemyVelocity.velocity.x, 1e-5f);
  }

  @Test
  void testPlayerStillCollidesWithStaticSolid() {
    Entity player = new Entity();
    TransformComponent playerTransform = new TransformComponent(10f, 10f, 10f, 10f);
    player.add(new PlayerComponent());
    player.add(playerTransform);
    player.add(new VelocityComponent(100f));
    player.add(new CollisionComponent(Type.SOLID));

    Entity block = new Entity();
    block.add(new TransformComponent(18f, 10f, 10f, 10f));
    block.add(new CollisionComponent(Type.SOLID));

    engine.addEntity(player);
    engine.addEntity(block);

    collisionSystem.update(0.1f);

    assertEquals(8f, playerTransform.position.x, 1e-5f);
  }

  private Entity createPlayer(TransformComponent transform, HealthComponent health) {
    Entity player = new Entity();
    player.add(new PlayerComponent());
    player.add(transform);
    player.add(new VelocityComponent(100f));
    player.add(health);
    player.add(new CollisionComponent(Type.SOLID));
    return player;
  }

  private Entity createEnemy(TransformComponent transform, VelocityComponent velocity,
                             EnemyComponent enemyComponent) {
    Entity enemy = new Entity();
    enemy.add(enemyComponent);
    enemy.add(transform);
    enemy.add(velocity);
    enemy.add(new CollisionComponent(Type.SOLID));
    return enemy;
  }

  @Test
  void testEnemyContactDamagesPlayerAndKnocksEnemyBack() {
    HealthComponent playerHealth = new HealthComponent(100f);
    TransformComponent playerTransform = new TransformComponent(10f, 10f, 10f, 10f);
    engine.addEntity(createPlayer(playerTransform, playerHealth));

    EnemyComponent enemyComponent = new EnemyComponent(15f, 80f);
    VelocityComponent enemyVelocity = new VelocityComponent(100f);
    enemyVelocity.velocity.set(-100f, 0f); // Moving left into the player
    engine.addEntity(createEnemy(
        new TransformComponent(18f, 10f, 10f, 10f), enemyVelocity, enemyComponent));

    collisionSystem.update(0.1f);

    assertEquals(85f, playerHealth.health, 1e-5f);
    assertTrue(enemyComponent.isKnockedBack());
    // Knocked back to the right (opposite its velocity), covering 80 units over the duration
    float expectedSpeed = 80f / enemyComponent.knockbackDuration;
    assertEquals(expectedSpeed, enemyVelocity.velocity.x, 1e-3f);
    assertEquals(0f, enemyVelocity.velocity.y, 1e-5f);
    // Player is not moved
    assertEquals(10f, playerTransform.position.x, 1e-5f);
  }

  @Test
  void testEnemyDoesNotDamageAgainWhileKnockedBack() {
    HealthComponent playerHealth = new HealthComponent(100f);
    engine.addEntity(createPlayer(new TransformComponent(10f, 10f, 10f, 10f), playerHealth));

    EnemyComponent enemyComponent = new EnemyComponent(15f, 80f);
    VelocityComponent enemyVelocity = new VelocityComponent(100f);
    enemyVelocity.velocity.set(-100f, 0f);
    TransformComponent enemyTransform = new TransformComponent(18f, 10f, 10f, 10f);
    engine.addEntity(createEnemy(enemyTransform, enemyVelocity, enemyComponent));

    collisionSystem.update(0.1f);
    enemyTransform.position.x = 18f; // Force overlap again
    collisionSystem.update(0.1f);

    assertEquals(85f, playerHealth.health, 1e-5f);
  }

  @Test
  void testStationaryEnemyIsKnockedAwayFromPlayer() {
    HealthComponent playerHealth = new HealthComponent(100f);
    engine.addEntity(createPlayer(new TransformComponent(10f, 10f, 10f, 10f), playerHealth));

    EnemyComponent enemyComponent = new EnemyComponent(10f, 80f);
    VelocityComponent enemyVelocity = new VelocityComponent(100f); // Not moving
    engine.addEntity(createEnemy(
        new TransformComponent(10f, 18f, 10f, 10f), enemyVelocity, enemyComponent));

    collisionSystem.update(0.1f);

    assertEquals(90f, playerHealth.health, 1e-5f);
    // Enemy is above the player, so it is pushed upwards
    assertEquals(0f, enemyVelocity.velocity.x, 1e-5f);
    assertTrue(enemyVelocity.velocity.y > 0);
  }
}
