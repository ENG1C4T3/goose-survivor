package com.eng1c4t3.goosesurvivor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests session timing and state transitions.
 */
class GameSessionTest {

  @Test
  void pausedUpdatesDoNotConsumeTime() {
    GameSession session = new GameSession();

    session.update(20f);
    final float remainingBeforePause = session.getRemainingTime();

    session.pause();
    session.update(100f);

    assertEquals(SessionState.PAUSED, session.getState());
    assertEquals(
        remainingBeforePause,
        session.getRemainingTime(),
        0.001f);

    session.resume();
    session.update(5f);

    assertEquals(SessionState.RUNNING, session.getState());
    assertEquals(
        remainingBeforePause - 5f,
        session.getRemainingTime(),
        0.001f);
  }

  @Test
  void exceedingTimeLimitLosesAndClampsRemainingTimeToZero() {
    GameSession session = new GameSession();

    session.update(session.getRemainingTime() + 1f);

    assertEquals(SessionState.LOST, session.getState());
    assertEquals(0f, session.getRemainingTime(), 0.001f);
  }

  @Test
  void lostSessionCannotBeResumed() {
    GameSession session = new GameSession();
    session.update(session.getRemainingTime());

    session.resume();
    session.update(5f);

    assertEquals(SessionState.LOST, session.getState());
    assertEquals(0f, session.getRemainingTime(), 0.001f);
  }
}