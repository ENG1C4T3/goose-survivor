package com.eng1c4t3.goosesurvivor.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;

/**
 * Represents the player in the game.
 */
public class PlayerComponent implements Component {
  public static final ComponentMapper<PlayerComponent> MAPPER =
      ComponentMapper.getFor(PlayerComponent.class);

  /** Where the mouse is pointing, in world coordinates. Only valid while {@link #hasAim}. */
  public final Vector2 aim = new Vector2();
  /** Whether {@link #aim} has been set, e.g. false in tests with no mouse. */
  public boolean hasAim;
}
