package com.eng1c4t3.goosesurvivor.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.eng1c4t3.goosesurvivor.entities.SpawnDrop;
import com.eng1c4t3.goosesurvivor.systems.HiddenEventSystem;
import java.util.Objects;

/**
 * Spawns something below an entity, once, the first time the player comes near it, e.g. the
 * gradient goose guarding the door.
 *
 * @see HiddenEventSystem
 */
public class SpawnOnApproachComponent implements Component {
  public static final ComponentMapper<SpawnOnApproachComponent> MAPPER =
      ComponentMapper.getFor(SpawnOnApproachComponent.class);

  /** What to spawn. */
  public final SpawnDrop drop;
  /** Edge-to-edge distance from the player at which the drop spawns. */
  public final float range;
  /** Whether the drop has already spawned. */
  public boolean spawned = false;

  /**
   * Constructs a new SpawnOnApproachComponent.
   *
   * @param drop  What to spawn.
   * @param range Edge-to-edge distance from the player at which the drop spawns.
   */
  public SpawnOnApproachComponent(SpawnDrop drop, float range) {
    this.drop = Objects.requireNonNull(drop, "drop must not be null");
    this.range = range;
  }
}
