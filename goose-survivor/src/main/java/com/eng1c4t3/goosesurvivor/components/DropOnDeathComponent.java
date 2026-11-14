package com.eng1c4t3.goosesurvivor.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.eng1c4t3.goosesurvivor.entities.SpawnDrop;
import com.eng1c4t3.goosesurvivor.systems.HiddenEventSystem;

/**
 * Spawns something where this entity was when it runs out of health, e.g. the key dropped by
 * the gradient goose.
 *
 * @see HiddenEventSystem
 */
public class DropOnDeathComponent implements Component {
  public static final ComponentMapper<DropOnDeathComponent> MAPPER =
      ComponentMapper.getFor(DropOnDeathComponent.class);

  /** What is spawned on death. */
  public final SpawnDrop drop;

  /**
   * Constructs a new DropOnDeathComponent.
   *
   * @param drop What is spawned on death.
   * @throws IllegalArgumentException if drop is null.
   */
  public DropOnDeathComponent(SpawnDrop drop) {
    if (drop == null) {
      throw new IllegalArgumentException("drop must not be null");
    }
    this.drop = drop;
  }
}
