package com.eng1c4t3.goosesurvivor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.ashley.core.Entity;
import com.eng1c4t3.goosesurvivor.components.HealthComponent;
import com.eng1c4t3.goosesurvivor.components.PlayerComponent;
import com.eng1c4t3.goosesurvivor.components.TransformComponent;
import com.eng1c4t3.goosesurvivor.components.VelocityComponent;
import org.junit.jupiter.api.Test;

class GameSessionIntegrationTest {

  @Test
  void deadPlayerIsRemovedAndSessionIsLost() {
    final GameSession session = new GameSession();
    final Entity player = new Entity();
    final HealthComponent health = new HealthComponent(100f);
    player.add(new PlayerComponent());
    player.add(health);
    session.getWorld().addEntity(player);

    health.takeDamage(100f);
    session.update(0.016f);

    assertEquals(SessionState.LOST, session.getState());
    assertFalse(session.getWorld().getEntities().contains(player, true));

    final float remaining = session.getRemainingTime();
    session.resume();
    session.update(1f);

    assertEquals(SessionState.LOST, session.getState());
    assertEquals(remaining, session.getRemainingTime(), 0.001f);
  }

  @Test
  void deadNonPlayerIsRemovedWithoutEndingSession() {
    final GameSession session = new GameSession();
    final Entity entity = new Entity();
    final HealthComponent health = new HealthComponent(50f);
    entity.add(health);
    session.getWorld().addEntity(entity);

    health.takeDamage(50f);
    session.update(0.016f);

    assertFalse(session.getWorld().getEntities().contains(entity, true));
    assertEquals(SessionState.RUNNING, session.getState());
  }

  @Test
  void removingLivingPlayerDoesNotEndSession() {
    final GameSession session = new GameSession();
    final Entity player = new Entity();
    player.add(new PlayerComponent());
    player.add(new HealthComponent(100f));
    session.getWorld().addEntity(player);

    session.getWorld().removeEntity(player);

    assertEquals(SessionState.RUNNING, session.getState());
  }

  @Test
  void pauseStopsMovementAndTimerUntilResumed() {
    final GameSession session = new GameSession();
    final Entity mover = new Entity();
    final TransformComponent transform = new TransformComponent(10f, 20f, 8f, 8f);
    final VelocityComponent velocity = new VelocityComponent(10f);
    velocity.velocity.set(10f, 0f);
    mover.add(transform);
    mover.add(velocity);
    session.getWorld().addEntity(mover);

    final float remaining = session.getRemainingTime();
    session.pause();
    session.update(2f);

    assertEquals(SessionState.PAUSED, session.getState());
    assertEquals(10f, transform.position.x, 0.001f);
    assertEquals(remaining, session.getRemainingTime(), 0.001f);

    session.resume();
    session.update(1f);

    assertEquals(SessionState.RUNNING, session.getState());
    assertEquals(20f, transform.position.x, 0.001f);
    assertEquals(remaining - 1f, session.getRemainingTime(), 0.001f);
    assertTrue(session.getWorld().getEntities().contains(mover, true));
  }
}