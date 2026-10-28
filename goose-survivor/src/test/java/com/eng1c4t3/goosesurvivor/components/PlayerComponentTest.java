package com.eng1c4t3.goosesurvivor.components;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PlayerComponentTest {

  @Test
  void testPlayerComponentInstantiation() {
    PlayerComponent playerComponent = new PlayerComponent();
    assertNotNull(playerComponent);
    assertTrue(playerComponent instanceof Component);
  }
}
