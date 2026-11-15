package com.eng1c4t3.goosesurvivor.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.eng1c4t3.goosesurvivor.systems.FoxSystem;

/**
 * A fox that runs away from the player until it is petted, then follows the player.
 *
 * @see FoxSystem
 */
public class FoxComponent implements Component {
  public static final ComponentMapper<FoxComponent> MAPPER =
      ComponentMapper.getFor(FoxComponent.class);

  /** Speed while running away; a little slower than the player so it can be caught. */
  public static final float FLEE_SPEED = 85f;
  /** Speed while following the player, fast enough to keep up. */
  public static final float FOLLOW_SPEED = 120f;
  /** Distance between centres within which a wild fox runs away from the player. */
  public static final float FLEE_RADIUS = 160f;
  /** Distance between centres a tame fox tries to stay within. */
  public static final float FOLLOW_DISTANCE = 60f;

  /** Whether the fox has been petted and now follows the player. */
  public boolean tamed;
}
