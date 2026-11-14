package com.eng1c4t3.goosesurvivor.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.eng1c4t3.goosesurvivor.systems.CollisionSystem;
import com.eng1c4t3.goosesurvivor.systems.EnemySystem;

/**
 * Marks an entity that enemies target instead of the player, such as placed popcorn.
 *
 * <p>While any decoy exists, each enemy chases the nearest one and deals its contact damage to
 * it as it would to the player. Once a decoy runs out of health it is removed and enemies move
 * on to the next decoy, or back to the player.
 *
 * @see EnemySystem
 * @see CollisionSystem
 */
public class DecoyComponent implements Component {
  public static final ComponentMapper<DecoyComponent> MAPPER =
      ComponentMapper.getFor(DecoyComponent.class);
}
