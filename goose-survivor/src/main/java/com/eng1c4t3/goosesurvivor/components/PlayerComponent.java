package com.eng1c4t3.goosesurvivor.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

/**
 * Represents the player in the game.
 */
public class PlayerComponent implements Component {
  public static final ComponentMapper<PlayerComponent> MAPPER =
      ComponentMapper.getFor(PlayerComponent.class);
}
