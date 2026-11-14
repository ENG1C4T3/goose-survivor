package com.eng1c4t3.goosesurvivor.entities;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.eng1c4t3.goosesurvivor.TextureManager;
import com.eng1c4t3.goosesurvivor.components.CollisionComponent;
import com.eng1c4t3.goosesurvivor.components.DecoyComponent;
import com.eng1c4t3.goosesurvivor.components.DropOnDeathComponent;
import com.eng1c4t3.goosesurvivor.components.FoxComponent;
import com.eng1c4t3.goosesurvivor.components.EnemyComponent;
import com.eng1c4t3.goosesurvivor.components.HealthComponent;
import com.eng1c4t3.goosesurvivor.components.HiddenEventComponent;
import com.eng1c4t3.goosesurvivor.components.InteractableComponent;
import com.eng1c4t3.goosesurvivor.components.InventoryComponent;
import com.eng1c4t3.goosesurvivor.components.ItemPickupComponent;
import com.eng1c4t3.goosesurvivor.components.PlayerComponent;
import com.eng1c4t3.goosesurvivor.components.ProjectileComponent;
import com.eng1c4t3.goosesurvivor.components.ShooterComponent;
import com.eng1c4t3.goosesurvivor.components.SpawnOnApproachComponent;
import com.eng1c4t3.goosesurvivor.components.SpawnerComponent;
import com.eng1c4t3.goosesurvivor.components.TextureComponent;
import com.eng1c4t3.goosesurvivor.components.TransformComponent;
import com.eng1c4t3.goosesurvivor.components.VelocityComponent;
import com.eng1c4t3.goosesurvivor.items.ItemType;
import com.eng1c4t3.goosesurvivor.util.WeightedTable;

/**
 * Factory for creating game entities with configured components.
 */
public final class EntityFactory {

  /** Seconds the player is invulnerable for after being hit. */
  public static final float PLAYER_DAMAGE_COOLDOWN = 0.5f;

  /** Maximum health of the player, also used for decoys. */
  public static final float PLAYER_MAX_HEALTH = 100f;

  /** How close, edge to edge, the player must be to pick up an item or health pack. */
  public static final float PICKUP_RANGE = 16f;

  /** How close, edge to edge, the player must be to use an object or talk to someone. */
  /** How close the player gets to the door before the gradient goose guarding it spawns. */
  public static final float DOOR_GUARD_RANGE = 150f;
  public static final float INTERACT_RANGE = 24f;

  /** Width and height of a projectile. */
  public static final float PROJECTILE_SIZE = 5f;
  /** Speed a projectile loses per second. */
  public static final float PROJECTILE_DECELERATION = 100f;
  /** Speed below which a projectile despawns. */
  public static final float PROJECTILE_MIN_SPEED = 50f;

  /** Seconds between Long Boi's shots at the player. */
  public static final float LONG_BOI_SHOT_INTERVAL = 20f;
  /** Initial speed of Long Boi's projectiles. */
  public static final float LONG_BOI_PROJECTILE_SPEED = 300f;
  /** Damage dealt by Long Boi's projectiles. */
  public static final float LONG_BOI_PROJECTILE_DAMAGE = 15f;

  /**
   * The kinds of enemy that can be created.
   */
  public enum EnemyType {
    GOOSE,
    FAST_GOOSE,
    TANK_GOOSE,
    GRADIENT_GOOSE,
    LONG_BOI
  }

  /**
   * The stats that distinguish one enemy type from another.
   *
   * @param width     Width of the enemy.
   * @param height    Height of the enemy.
   * @param health    Maximum health.
   * @param speed     Maximum movement speed.
   * @param damage    Contact damage dealt to the player.
   * @param knockback Distance the enemy is knocked back after touching the
   *                  player.
   * @param texture   File name of the enemy's texture.
   * @param score     Points awarded for defeating the enemy.
   */
  public record EnemyStats(float width, float height, float health, float speed, float damage,
      float knockback, String texture, int score) {
  }

  private EntityFactory() {
  }

  /**
   * Returns the stats for an enemy type.
   *
   * @param type The enemy type.
   * @return The {@link EnemyStats} for that type.
   */
  public static EnemyStats statsFor(EnemyType type) {
    return switch (type) {
      case GOOSE ->
          new EnemyStats(41f, 39f, 100f, 70f, 10f, 80f, TextureManager.GOOSE_TEXTURE, 10);
      case FAST_GOOSE ->
          new EnemyStats(59f, 49f, 100f, 100f, 5f, 60f, TextureManager.FAST_GOOSE_TEXTURE, 15);
      case TANK_GOOSE ->
          new EnemyStats(45f, 61f, 300f, 40f, 15f, 40f, TextureManager.TANK_GOOSE_TEXTURE, 30);
      case GRADIENT_GOOSE ->
          new EnemyStats(99f, 110f, 150f, 80f, 10f, 80f,
              TextureManager.GRADIENT_GOOSE_TEXTURE, 20);
      case LONG_BOI ->
          new EnemyStats(38f, 72f, 500f, 100f, 20f, 100f, TextureManager.LONG_BOI_TEXTURE, 100);
    };
  }

  /**
   * Creates an enemy entity of the given type.
   *
   * @param type   The enemy type, which decides its stats and texture.
   * @param startX The initial x-coordinate.
   * @param startY The initial y-coordinate.
   * @param region The texture region for entity sprite
   *
   * @return A newly constructed enemy {@link Entity}.
   */
  public static Entity createEnemy(
      EnemyType type, float startX, float startY, TextureRegion region) {
    EnemyStats stats = statsFor(type);

    Entity enemy = new Entity();
    enemy.add(new TransformComponent(startX, startY, stats.width(), stats.height()));
    enemy.add(new HealthComponent(stats.health()));
    enemy.add(new VelocityComponent(stats.speed()));
    EnemyComponent enemyComponent = new EnemyComponent(stats.damage(), stats.knockback());
    enemyComponent.type = type;
    enemy.add(enemyComponent);
    enemy.add(new TextureComponent(region));
    enemy.add(new CollisionComponent(CollisionComponent.Type.SOLID));
    if (type == EnemyType.GRADIENT_GOOSE) {
      enemy.add(new DropOnDeathComponent(SpawnDrop.item(ItemType.KEY)));
    }
    if (type == EnemyType.LONG_BOI) {
      enemy.add(new ShooterComponent(
          LONG_BOI_SHOT_INTERVAL, LONG_BOI_PROJECTILE_SPEED, LONG_BOI_PROJECTILE_DAMAGE));
    }
    return enemy;
  }

  /**
   * Creates a projectile centred on the given point.
   *
   * <p>The projectile is a trigger, so it never pushes other entities around; the
   * {@link com.eng1c4t3.goosesurvivor.systems.ProjectileSystem} decides what it does on a hit.
   *
   * @param owner The entity that fired the projectile; it is never hit by it.
   * @param centreX The x-coordinate of the projectile's centre.
   * @param centreY The y-coordinate of the projectile's centre.
   * @param velocity The initial velocity of the projectile (copied).
   * @param damage The damage dealt to whatever the projectile hits.
   * @param region The texture region for the projectile.
   *
   * @return A newly constructed projectile {@link Entity}.
   */
  public static Entity createProjectile(
      Entity owner, float centreX, float centreY,
      Vector2 velocity, float damage, TextureRegion region) {
    Entity projectile = new Entity();
    projectile.add(new TransformComponent(centreX - PROJECTILE_SIZE / 2,
        centreY - PROJECTILE_SIZE / 2, PROJECTILE_SIZE, PROJECTILE_SIZE));
    VelocityComponent velocityComponent = new VelocityComponent(velocity.len());
    velocityComponent.velocity.set(velocity);
    projectile.add(velocityComponent);
    projectile.add(new ProjectileComponent(
        owner, damage, PROJECTILE_DECELERATION, PROJECTILE_MIN_SPEED));
    projectile.add(new CollisionComponent(CollisionComponent.Type.TRIGGER));
    projectile.add(new TextureComponent(region));
    return projectile;
  }

  /**
   * Creates a player entity with custom initial coordinates.
   *
   * @param x      The initial x-coordinate.
   * @param y      The initial y-coordinate.
   * @param region The texture region for entity sprite
   *
   * @return A newly constructed player {@link Entity}.
   */
  public static Entity createPlayer(float x, float y, TextureRegion region) {
    Entity player = new Entity();
    player.add(new PlayerComponent());
    player.add(new TransformComponent(x, y, 36f, 62f));
    player.add(new HealthComponent(PLAYER_MAX_HEALTH, PLAYER_DAMAGE_COOLDOWN));
    player.add(new VelocityComponent(100f));
    player.add(new TextureComponent(region));
    player.add(new CollisionComponent(CollisionComponent.Type.SOLID));
    player.add(new InventoryComponent(InventoryComponent.DEFAULT_CAPACITY));
    return player;
  }

  /**
   * Creates a stationary decoy that geese chase and attack instead of the player. It has the
   * same health and damage cooldown as the player and is removed when its health runs out.
   *
   * @param x      The x-coordinate.
   * @param y      The y-coordinate.
   * @param width  The width.
   * @param height The height.
   * @param region The decoy's texture region.
   * @return The new decoy entity.
   */
  public static Entity createDecoy(float x, float y, float width, float height,
      TextureRegion region) {
    Entity decoy = new Entity();
    decoy.add(new DecoyComponent());
    decoy.add(new TransformComponent(x, y, width, height));
    decoy.add(new HealthComponent(PLAYER_MAX_HEALTH, PLAYER_DAMAGE_COOLDOWN));
    decoy.add(new TextureComponent(region));
    decoy.add(new CollisionComponent(CollisionComponent.Type.SOLID));
    return decoy;
  }

  /**
   * Creates a block entity with custom initial coordinates, dimensions, and
   * texture region.
   *
   * @param x      The initial x-coordinate.
   * @param y      The initial y-coordinate.
   * @param width  The width of the block.
   * @param height The height of the block.
   * @param region The texture region for entity sprite
   *
   * @return A newly constructed block {@link Entity}.
   */
  public static Entity createBlock(
      float x, float y, float width, float height, TextureRegion region) {
    Entity block = new Entity();
    block.add(new TransformComponent(x, y, width, height));
    block.add(new TextureComponent(region));
    block.add(new CollisionComponent(CollisionComponent.Type.SOLID));
    return block;
  }

  /**
   * Creates an interactable statue that can start the Long Boi event.
   *
   * @param x      The initial x-coordinate.
   * @param y      The initial y-coordinate.
   * @param region The statue's texture region.
   * @return The new statue entity.
   */
  public static Entity createLongBoiStatue(float x, float y, TextureRegion region) {
    Entity statue = new Entity();
    statue.add(new TransformComponent(x, y, 36f, 69f));
    statue.add(new TextureComponent(region));
    statue.add(new CollisionComponent(CollisionComponent.Type.SOLID));
    statue.add(new InteractableComponent(INTERACT_RANGE, "Awaken Long Boi", false));
    statue.add(new HiddenEventComponent(
        HiddenEventComponent.EventType.LONG_BOI_STATUE));
    return statue;
  }

  /**
   * Creates a single-use item pickup sized to the item's own pickup width and height.
   *
   * @param item   The item given to the player.
   * @param x      The x-coordinate.
   * @param y      The y-coordinate.
   * @param region The pickup's texture region.
   * @return The new item pickup entity.
   * @see #createItemPickup(ItemType, float, float, int, int, TextureRegion)
   */
  public static Entity createItemPickup(ItemType item, float x, float y, TextureRegion region) {
    return createItemPickup(item, x, y, item.width, item.height, region);
  }

  /**
   * Creates a single-use item pickup that stays in the world if the inventory is
   * full.
   *
   * <p>The pickup has no collision, so the player can walk over it.
   *
   * @param item   The item given to the player.
   * @param x      The x-coordinate.
   * @param y      The y-coordinate.
   * @param width  The width of the pickup.
   * @param height The height of the pickup.
   * @param region The pickup's texture region.
   * @return The new item pickup entity.
   */
  public static Entity createItemPickup(
      ItemType item, float x, float y, int width, int height, TextureRegion region) {
    Entity pickup = new Entity();
    pickup.add(new TransformComponent(x, y, width, height));
    pickup.add(new TextureComponent(region));
    pickup.add(new InteractableComponent(PICKUP_RANGE, "Pick up " + item.displayName, true));
    pickup.add(new ItemPickupComponent(item));
    return pickup;
  }

  /**
   * Creates a stationary music student who gives the player a trumpet.
   *
   * @param x The initial x-coordinate.
   * @param y The initial y-coordinate.
   * @param region The student's texture region.
   * @return The new music student entity.
   */
  public static Entity createMusicStudent(float x, float y, TextureRegion region) {
    Entity student = new Entity();
    student.add(new TransformComponent(x, y, 51f, 70f));
    student.add(new TextureComponent(region));
    student.add(new CollisionComponent(CollisionComponent.Type.SOLID));
    student.add(new InteractableComponent(INTERACT_RANGE, "Talk to music student", false));
    student.add(new HiddenEventComponent(
        HiddenEventComponent.EventType.MUSIC_STUDENT));
    return student;
  }

  /**
   * Creates a solid entity that spawns a random item pickup or enemy below itself each time
   * it is used.
   *
   * @param x           The x-coordinate.
   * @param y           The y-coordinate.
   * @param width       The width.
   * @param height      The height.
   * @param region      The texture shown while the spawner has uses left.
   * @param emptyRegion The texture shown once empty, or null to keep {@code region}.
   * @param prompt      The interact prompt, e.g. "Use vending machine".
   * @param drops       What can be spawned and the weight of each.
   * @param uses        How many times the spawner can be used.
   * @return The new spawner entity.
   */
  public static Entity createSpawner(float x, float y, float width, float height,
      TextureRegion region, TextureRegion emptyRegion, String prompt,
      WeightedTable<SpawnDrop> drops, int uses) {
    Entity spawner = new Entity();
    spawner.add(new TransformComponent(x, y, width, height));
    spawner.add(new TextureComponent(region));
    spawner.add(new CollisionComponent(CollisionComponent.Type.SOLID));
    spawner.add(new InteractableComponent(INTERACT_RANGE, prompt, true));
    spawner.add(new HiddenEventComponent(HiddenEventComponent.EventType.SPAWNER));
    spawner.add(new SpawnerComponent(drops, uses, emptyRegion));
    return spawner;
  }

  /**
   * Creates a single-use coffee vending machine. It usually dispenses a coffee, but has a
   * {@link #VENDING_MACHINE_GOOSE_CHANCE} chance of releasing a goose instead. Either way it
   * shows as empty afterwards.
   *
   * @param x           The x-coordinate.
   * @param y           The y-coordinate.
   * @param region      The full vending machine texture.
   * @param emptyRegion The empty vending machine texture.
   * @return The new vending machine entity.
   */
  public static Entity createCoffeeVendingMachine(float x, float y, TextureRegion region,
      TextureRegion emptyRegion) {
    return createSpawner(x, y, 47, 82, region, emptyRegion,
        "Use coffee vending machine", coffeeVendingMachineDrops(), 1);
  }

  /** Chance, from 0 to 1, that the coffee vending machine spawns a goose instead of coffee. */
  public static final float VENDING_MACHINE_GOOSE_CHANCE = 0.1f;

  /**
   * Returns what the coffee vending machine can spawn.
   *
   * @return A table of coffee and goose, weighted by {@link #VENDING_MACHINE_GOOSE_CHANCE}.
   */
  public static WeightedTable<SpawnDrop> coffeeVendingMachineDrops() {
    return new WeightedTable<SpawnDrop>()
        .add(SpawnDrop.item(ItemType.COFFEE), 1f - VENDING_MACHINE_GOOSE_CHANCE)
        .add(SpawnDrop.enemy(EnemyType.GOOSE), VENDING_MACHINE_GOOSE_CHANCE);
  }

  /**
   * Creates a single-use snack vending machine. It dispenses a coffee, popcorn or energy drink,
   * or occasionally releases a goose, then shows as empty. See {@link #vendingMachineDrops()}.
   *
   * @param x           The x-coordinate.
   * @param y           The y-coordinate.
   * @param region      The full vending machine texture.
   * @param emptyRegion The empty vending machine texture.
   * @return The new vending machine entity.
   */
  public static Entity createVendingMachine(float x, float y, TextureRegion region,
      TextureRegion emptyRegion) {
    return createSpawner(x, y, 47, 82, region, emptyRegion,
        "Use vending machine", vendingMachineDrops(), 1);
  }

  /**
   * Returns what the snack vending machine can spawn: 30% coffee, 30% popcorn,
   * 30% energy drink and {@link #VENDING_MACHINE_GOOSE_CHANCE} (10%) goose.
   *
   * @return The vending machine's drop table.
   */
  public static WeightedTable<SpawnDrop> vendingMachineDrops() {
    return new WeightedTable<SpawnDrop>()
        .add(SpawnDrop.item(ItemType.COFFEE), 0.3f)
        .add(SpawnDrop.item(ItemType.POPCORN), 0.3f)
        .add(SpawnDrop.item(ItemType.ENERGY_DRINK), 0.3f)
        .add(SpawnDrop.enemy(EnemyType.GOOSE), VENDING_MACHINE_GOOSE_CHANCE);
  }

  /**
   * Creates a wild fox. It runs away from the player until petted, then follows them and
   * drops a {@link ItemType#FOOD_SCATTER_LAUNCHER}.
   *
   * @param x      The x-coordinate.
   * @param y      The y-coordinate.
   * @param region The fox's texture region.
   * @return The new fox entity.
   */
  public static Entity createFox(float x, float y, TextureRegion region) {
    Entity fox = new Entity();
    fox.add(new FoxComponent());
    fox.add(new TransformComponent(x, y, 37f, 32f));
    fox.add(new VelocityComponent(FoxComponent.FLEE_SPEED));
    fox.add(new TextureComponent(region));
    fox.add(new CollisionComponent(CollisionComponent.Type.SOLID));
    fox.add(new InteractableComponent(INTERACT_RANGE, "Pet fox", false));
    fox.add(new HiddenEventComponent(HiddenEventComponent.EventType.FOX));
    return fox;
  }

  /**
   * Creates a fountain. Visiting it does nothing except record the visit in the session stats.
   *
   * @param x      The x-coordinate.
   * @param y      The y-coordinate.
   * @param region The fountain's texture region.
   * @return The new fountain entity.
   */
  public static Entity createFountain(float x, float y, TextureRegion region) {
    Entity fountain = new Entity();
    fountain.add(new TransformComponent(x, y, 92f, 81f));
    fountain.add(new TextureComponent(region));
    fountain.add(new CollisionComponent(CollisionComponent.Type.SOLID));
    fountain.add(new InteractableComponent(INTERACT_RANGE, "Make a wish", false));
    fountain.add(new HiddenEventComponent(HiddenEventComponent.EventType.FOUNTAIN));
    return fountain;
  }

  /**
   * Creates a locked door that blocks movement until the player opens it with a
   * {@link ItemType#KEY}, which is used up.
   *
   * @param x      The x-coordinate.
   * @param y      The y-coordinate.
   * @param region The door's texture region.
   * @return The new door entity.
   */
  public static Entity createDoor(float x, float y, TextureRegion region) {
    Entity door = new Entity();
    door.add(new TransformComponent(x, y, 97f, 120f));
    door.add(new TextureComponent(region));
    door.add(new CollisionComponent(CollisionComponent.Type.SOLID));
    door.add(new InteractableComponent(INTERACT_RANGE, "Unlock door (needs key)", false));
    door.add(new HiddenEventComponent(HiddenEventComponent.EventType.DOOR));
    door.add(new SpawnOnApproachComponent(SpawnDrop.enemy(EnemyType.GRADIENT_GOOSE),
        DOOR_GUARD_RANGE));
    return door;
  }
}
