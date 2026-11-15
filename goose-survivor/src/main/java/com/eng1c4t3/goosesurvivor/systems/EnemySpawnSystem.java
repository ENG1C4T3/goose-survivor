package com.eng1c4t3.goosesurvivor.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.eng1c4t3.goosesurvivor.components.CollisionComponent;
import com.eng1c4t3.goosesurvivor.components.PlayerComponent;
import com.eng1c4t3.goosesurvivor.components.TransformComponent;
import com.eng1c4t3.goosesurvivor.entities.EntityFactory;
import com.eng1c4t3.goosesurvivor.entities.EntityFactory.EnemyType;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

/**
 * Spawns geese at the edges of the world over the course of a session.
 *
 * <p>Each goose type has a time window, measured from the start of the session, during which
 * one is spawned every {@link SpawnRule#interval()} seconds (see {@link #DEFAULT_RULES}):
 * basic geese for the first two minutes, fast geese from two to five minutes and tank geese
 * from three to five minutes.
 */
public class EnemySpawnSystem extends EntitySystem {

  /**
   * When and how often an enemy type spawns.
   *
   * @param type     The enemy type to spawn.
   * @param start    Session time in seconds the type starts spawning (inclusive).
   * @param end      Session time in seconds the type stops spawning (exclusive).
   * @param interval Seconds between spawns while the window is open.
   */
  public record SpawnRule(EnemyType type, float start, float end, float interval) {
    /**
     * Validates the rule.
     *
     * @throws IllegalArgumentException if the type is null, the window is empty or the
     *                                  interval is not positive.
     */
    public SpawnRule {
      if (type == null) {
        throw new IllegalArgumentException("type must not be null");
      }
      if (start < 0f || end <= start) {
        throw new IllegalArgumentException("spawn window must be non-empty");
      }
      if (interval <= 0f) {
        throw new IllegalArgumentException("interval must be positive");
      }
    }
  }

  /** The spawn schedule: basic 0-2 min, fast 2-5 min, tank 3-5 min. */
  public static final List<SpawnRule> DEFAULT_RULES = List.of(
      new SpawnRule(EnemyType.GOOSE, 0f, 120f, 5f),
      new SpawnRule(EnemyType.FAST_GOOSE, 120f, 300f, 6f),
      new SpawnRule(EnemyType.TANK_GOOSE, 180f, 300f, 12f));

  /** Minimum distance between a newly spawned goose's centre and the player's centre. */
  public static final float MIN_PLAYER_DISTANCE = 250f;

  /** How many random positions are tried before a spawn is skipped. */
  private static final int SPAWN_ATTEMPTS = 20;

  private final List<SpawnRule> rules;
  private final float[] timers;
  private final float worldWidth;
  private final float worldHeight;
  private final Function<String, TextureRegion> textures;
  private Random random = new Random();
  private float elapsed = 0f;

  private ImmutableArray<Entity> players;
  private ImmutableArray<Entity> solids;

  /**
   * Creates a spawn system using {@link #DEFAULT_RULES}.
   *
   * @param worldWidth  Width of the world geese spawn in.
   * @param worldHeight Height of the world geese spawn in.
   * @param textures    Returns the texture region for a texture file name.
   */
  public EnemySpawnSystem(float worldWidth, float worldHeight,
      Function<String, TextureRegion> textures) {
    this(DEFAULT_RULES, worldWidth, worldHeight, textures);
  }

  /**
   * Creates a spawn system with a custom schedule.
   *
   * @param rules       When and how often each enemy type spawns.
   * @param worldWidth  Width of the world geese spawn in.
   * @param worldHeight Height of the world geese spawn in.
   * @param textures    Returns the texture region for a texture file name.
   * @throws NullPointerException if rules or textures is null.
   */
  public EnemySpawnSystem(List<SpawnRule> rules, float worldWidth, float worldHeight,
      Function<String, TextureRegion> textures) {
    this.rules = List.copyOf(Objects.requireNonNull(rules, "rules must not be null"));
    this.textures = Objects.requireNonNull(textures, "textures must not be null");
    this.worldWidth = worldWidth;
    this.worldHeight = worldHeight;
    this.timers = new float[this.rules.size()];
  }

  /**
   * Sets the source of randomness used to pick spawn positions, e.g. a seeded one for tests.
   *
   * @param random The source of randomness.
   * @throws NullPointerException if random is null.
   */
  public void setRandom(Random random) {
    this.random = Objects.requireNonNull(random, "random must not be null");
  }

  @Override
  public void addedToEngine(Engine engine) {
    players = engine.getEntitiesFor(
        Family.all(PlayerComponent.class, TransformComponent.class).get());
    solids = engine.getEntitiesFor(
        Family.all(CollisionComponent.class, TransformComponent.class).get());
  }

  @Override
  public void update(float deltaTime) {
    float previous = elapsed;
    elapsed += deltaTime;
    for (int i = 0; i < rules.size(); ++i) {
      SpawnRule rule = rules.get(i);
      // Only count the part of this frame that falls inside the rule's window.
      float from = Math.max(previous, rule.start());
      float to = Math.min(elapsed, rule.end());
      if (to <= from) {
        continue;
      }
      if (previous <= rule.start()) {
        // The window has just opened: spawn straight away.
        timers[i] = rule.interval();
      }
      timers[i] += to - from;
      while (timers[i] >= rule.interval()) {
        timers[i] -= rule.interval();
        spawn(rule.type());
      }
    }
  }

  /**
   * Returns the session time this system has counted so far.
   *
   * @return Seconds since the system started updating.
   */
  public float getElapsed() {
    return elapsed;
  }

  /**
   * Spawns an enemy at a free spot along the world's edge, away from the player.
   *
   * @param type The enemy type.
   * @return The spawned enemy, or null if no free spot was found.
   */
  Entity spawn(EnemyType type) {
    EntityFactory.EnemyStats stats = EntityFactory.statsFor(type);
    Rectangle bounds = new Rectangle(0f, 0f, stats.width(), stats.height());
    for (int attempt = 0; attempt < SPAWN_ATTEMPTS; ++attempt) {
      pickEdgePosition(bounds);
      if (isFree(bounds)) {
        Entity enemy = EntityFactory.createEnemy(type, bounds.x, bounds.y,
            textures.apply(stats.texture()));
        getEngine().addEntity(enemy);
        return enemy;
      }
    }
    return null;
  }

  /**
   * Moves the bounds to a random position touching one of the world's four edges.
   */
  private void pickEdgePosition(Rectangle bounds) {
    float maxX = Math.max(0f, worldWidth - bounds.width);
    float maxY = Math.max(0f, worldHeight - bounds.height);
    switch (random.nextInt(4)) {
      case 0 -> bounds.setPosition(random.nextFloat() * maxX, 0f);
      case 1 -> bounds.setPosition(random.nextFloat() * maxX, maxY);
      case 2 -> bounds.setPosition(0f, random.nextFloat() * maxY);
      default -> bounds.setPosition(maxX, random.nextFloat() * maxY);
    }
  }

  /**
   * Returns whether the bounds overlap nothing solid and are far enough from every player.
   */
  private boolean isFree(Rectangle bounds) {
    float centreX = bounds.x + bounds.width / 2f;
    float centreY = bounds.y + bounds.height / 2f;
    for (Entity player : players) {
      Rectangle p = TransformComponent.MAPPER.get(player).position;
      float dx = p.x + p.width / 2f - centreX;
      float dy = p.y + p.height / 2f - centreY;
      if (dx * dx + dy * dy < MIN_PLAYER_DISTANCE * MIN_PLAYER_DISTANCE) {
        return false;
      }
    }
    for (Entity solid : solids) {
      if (CollisionComponent.MAPPER.get(solid).type != CollisionComponent.Type.SOLID) {
        continue;
      }
      if (TransformComponent.MAPPER.get(solid).position.overlaps(bounds)) {
        return false;
      }
    }
    return true;
  }
}
