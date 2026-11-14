package com.eng1c4t3.goosesurvivor.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.eng1c4t3.goosesurvivor.entities.SpawnDrop;
import com.eng1c4t3.goosesurvivor.systems.HiddenEventSystem;
import com.eng1c4t3.goosesurvivor.util.WeightedTable;

/**
 * Settings for a {@link HiddenEventComponent.EventType#SPAWNER} event: an entity that spawns
 * a random item pickup or enemy below itself each time it is used.
 *
 * @see HiddenEventSystem
 */
public class SpawnerComponent implements Component {
  public static final ComponentMapper<SpawnerComponent> MAPPER =
      ComponentMapper.getFor(SpawnerComponent.class);

  /** What can be spawned and the weight of each. */
  public final WeightedTable<SpawnDrop> drops;

  /** Uses left before the spawner is empty. */
  public int remainingUses;

  /** Texture shown once the spawner is empty, or null to keep the current texture. */
  public TextureRegion emptyRegion;

  /**
   * Constructs a new SpawnerComponent.
   *
   * @param drops       What can be spawned and the weight of each.
   * @param uses        How many times the spawner can be used. Must be positive.
   * @param emptyRegion Texture shown once empty, or null to keep the current texture.
   * @throws IllegalArgumentException if the table is null or empty, or uses is not positive.
   */
  public SpawnerComponent(WeightedTable<SpawnDrop> drops, int uses, TextureRegion emptyRegion) {
    if (drops == null || drops.isEmpty()) {
      throw new IllegalArgumentException("drops must not be null or empty");
    }
    if (uses <= 0) {
      throw new IllegalArgumentException("uses must be positive");
    }
    this.drops = drops;
    this.remainingUses = uses;
    this.emptyRegion = emptyRegion;
  }
}
