package com.eng1c4t3.goosesurvivor.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.eng1c4t3.goosesurvivor.SessionStats;
import com.eng1c4t3.goosesurvivor.components.DropOnDeathComponent;
import com.eng1c4t3.goosesurvivor.components.FoxComponent;
import com.eng1c4t3.goosesurvivor.components.HealthComponent;
import com.eng1c4t3.goosesurvivor.components.HiddenEventComponent;
import com.eng1c4t3.goosesurvivor.components.HiddenEventComponent.EventState;
import com.eng1c4t3.goosesurvivor.components.HiddenEventComponent.EventType;
import com.eng1c4t3.goosesurvivor.components.InteractableComponent;
import com.eng1c4t3.goosesurvivor.components.InventoryComponent;
import com.eng1c4t3.goosesurvivor.components.PlayerComponent;
import com.eng1c4t3.goosesurvivor.components.SpawnOnApproachComponent;
import com.eng1c4t3.goosesurvivor.components.SpawnerComponent;
import com.eng1c4t3.goosesurvivor.components.TextureComponent;
import com.eng1c4t3.goosesurvivor.components.TransformComponent;
import com.eng1c4t3.goosesurvivor.components.TriggerEventComponent;
import com.eng1c4t3.goosesurvivor.components.VelocityComponent;
import com.eng1c4t3.goosesurvivor.entities.SpawnDrop;
import com.eng1c4t3.goosesurvivor.items.InventorySlot;
import com.eng1c4t3.goosesurvivor.items.ItemType;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

/**
 * Manages hidden gameplay events and records their progress.
 */
public class HiddenEventSystem extends EntitySystem {
  private final SessionStats stats;
  private final Function<Entity, Entity> bossFactory;
  private ImmutableArray<Entity> triggerEvents;
  private ImmutableArray<Entity> hiddenEvents;
  private ImmutableArray<Entity> players;
  private ImmutableArray<Entity> approachSpawners;
  private Function<String, TextureRegion> textures = fileName -> null;
  private Random random = new Random();
  private Runnable onDoorOpened = () -> {};

  /** Vertical gap between a spawner and the entity it spawns. */
  public static final float SPAWN_GAP = 4f;

  /** Permanent speed reward multiplier for completing the Long Boi event. */
  public static final float LONG_BOI_SPEED_REWARD = 1.1f;

  /** Permanent extra maximum health for completing the Long Boi event. */
  public static final float LONG_BOI_PATIENCE_REWARD = 25f;

  private final EntityListener bossRemovalListener = new EntityListener() {
    @Override
    public void entityAdded(Entity entity) {
      // Adding an entity does not complete an event.
    }

    @Override
    public void entityRemoved(Entity entity) {
      completeEventForBoss(entity);
      dropOnDeath(entity);
    }
  };

  /**
   * Creates a hidden event system.
   *
   * @param stats       The statistic for the current session.
   * @param bossFactory bossFactory Creates a new boss for the supplied statue
   *                    entity.
   * @throws NullPointerException if either argument is null.
   */
  public HiddenEventSystem(SessionStats stats, Function<Entity, Entity> bossFactory) {
    this.stats = Objects.requireNonNull(stats, "Session statistic must not be null");
    this.bossFactory = Objects.requireNonNull(bossFactory,
        "bossFactory must not be null");
  }

  /**
   * Sets how spawners look up the texture for the entities they spawn.
   *
   * @param textures Returns the texture region for a texture file name.
   * @throws NullPointerException if textures is null.
   */
  public void setTextures(Function<String, TextureRegion> textures) {
    this.textures = Objects.requireNonNull(textures, "textures must not be null");
  }

  /**
   * Sets what happens when the player unlocks a door, e.g. winning the game.
   *
   * @param onDoorOpened Run each time a door is opened.
   * @throws NullPointerException if onDoorOpened is null.
   */
  public void setOnDoorOpened(Runnable onDoorOpened) {
    this.onDoorOpened = Objects.requireNonNull(onDoorOpened, "onDoorOpened must not be null");
  }

  /**
   * Sets the source of randomness used by spawners, e.g. a seeded one for tests.
   *
   * @param random The source of randomness.
   * @throws NullPointerException if random is null.
   */
  public void setRandom(Random random) {
    this.random = Objects.requireNonNull(random, "random must not be null");
  }

  /**
   * Tracks interaction events and registers the boss removal listener.
   *
   * @param engine The engine this system belongs to.
   */
  @Override
  public void addedToEngine(Engine engine) {
    triggerEvents = engine.getEntitiesFor(
        Family.all(TriggerEventComponent.class).get());
    hiddenEvents = engine.getEntitiesFor(
        Family.all(HiddenEventComponent.class).get());
    players = engine.getEntitiesFor(
        Family.all(PlayerComponent.class, VelocityComponent.class).get());
    approachSpawners = engine.getEntitiesFor(
        Family.all(SpawnOnApproachComponent.class, TransformComponent.class).get());
    engine.addEntityListener(bossRemovalListener);
  }

  /**
   * Unregisters the boss removal listener.
   *
   * @param engine The engine this system is leaving.
   */
  @Override
  public void removedFromEngine(Engine engine) {
    engine.removeEntityListener(bossRemovalListener);
  }

  @Override
  public void update(float deltaTime) {
    updateApproachSpawners();
    for (Entity notification : triggerEvents) {
      final TriggerEventComponent trigger = TriggerEventComponent.MAPPER.get(notification);

      if (trigger.action != TriggerEventComponent.ActionType.INTERACT) {
        continue;
      }

      final Entity target = trigger.targetInteractable;
      if (target == null || target.isScheduledForRemoval()
          || !InteractableComponent.MAPPER.has(target)) {
        continue;
      }
      HiddenEventComponent event = HiddenEventComponent.MAPPER.get(target);
      if (event == null) {
        continue;
      }

      switch (event.type) {
        case LONG_BOI_STATUE:
          triggerStatue(target);
          break;
        case MUSIC_STUDENT:
          interactWithMusicStudent(target, trigger.sourcePlayer);
          break;
        case SPAWNER:
          useSpawner(target);
          break;
        case FOX:
          petFox(target);
          break;
        case FOUNTAIN:
          visitFountain(target);
          break;
        case DOOR:
          openDoor(target, trigger.sourcePlayer);
          break;
        default:
          break;
      }
    }
  }

  /**
   * Spawns each approach spawner's drop below it the first time a player comes within range.
   */
  private void updateApproachSpawners() {
    for (int i = 0; i < approachSpawners.size(); ++i) {
      Entity source = approachSpawners.get(i);
      SpawnOnApproachComponent approach = SpawnOnApproachComponent.MAPPER.get(source);
      if (approach.spawned || source.isScheduledForRemoval()) {
        continue;
      }
      for (Entity player : players) {
        TransformComponent playerTransform = TransformComponent.MAPPER.get(player);
        if (playerTransform != null && edgeGap(playerTransform,
            TransformComponent.MAPPER.get(source)) <= approach.range) {
          approach.spawned = true;
          spawnBelow(source, approach.drop);
          break;
        }
      }
    }
  }

  /**
   * Returns the gap between the edges of two boxes, or 0 if they overlap.
   */
  private static float edgeGap(TransformComponent a, TransformComponent b) {
    float dx = Math.max(0f, Math.max(b.position.x - (a.position.x + a.position.width),
        a.position.x - (b.position.x + b.position.width)));
    float dy = Math.max(0f, Math.max(b.position.y - (a.position.y + a.position.height),
        a.position.y - (b.position.y + b.position.height)));
    return (float) Math.sqrt(dx * dx + dy * dy);
  }

  /**
   * Starts an avalable statue event and adds its boss to the engine.
   *
   * @param statue The statue entity belonging to this engine.
   * @return Trun if the event started, otherwise false.
   */
  public boolean triggerStatue(Entity statue) {
    final HiddenEventComponent event = HiddenEventComponent.MAPPER.get(statue);
    if (event == null || event.type != EventType.LONG_BOI_STATUE
        || event.state != EventState.AVAILABLE) {
      return false;
    }

    final Entity boss = bossFactory.apply(statue);
    event.eventBoss = boss;
    event.state = EventState.ACTIVE;
    getEngine().addEntity(boss);
    stats.recordEventTriggered();
    return true;
  }

  /**
   * Blesses the current player with Long Boi's permanent rewards: more speed and more
   * maximum health, healing them by the extra amount.
   */
  private void grantBlessing() {
    for (Entity player : players) {
      VelocityComponent velocity = VelocityComponent.MAPPER.get(player);
      velocity.maxSpeed *= LONG_BOI_SPEED_REWARD;
      HealthComponent health = HealthComponent.MAPPER.get(player);
      if (health != null) {
        health.maxHealth += LONG_BOI_PATIENCE_REWARD;
        health.heal(LONG_BOI_PATIENCE_REWARD);
      }
    }
  }

  /**
   * Completes the active statue event associated with a defeated boss.
   *
   * @param boss The entity removed from the engine.
   */
  private void completeEventForBoss(Entity boss) {
    HealthComponent health = HealthComponent.MAPPER.get(boss);
    if (health == null || !health.isDead()) {
      return;
    }

    for (Entity statue : hiddenEvents) {
      HiddenEventComponent event = HiddenEventComponent.MAPPER.get(statue);
      if (event.type != EventType.LONG_BOI_STATUE
          || event.state != EventState.ACTIVE
          || event.eventBoss != boss) {
        continue;
      }

      event.state = EventState.COMPLETED;
      event.eventBoss = null;
      statue.remove(InteractableComponent.class);
      stats.recordEventCompleted();
      stats.recordHiddenEventFound();
      grantBlessing();
    }
  }

  /**
   * Gives the player a trumpet and completes the music student event.
   *
   * @param student The music student being interacted with.
   * @param player The player receiving the trumpet.
   * @return True if the trumpet was received and the event completed.
   */
  public boolean interactWithMusicStudent(Entity student, Entity player) {
    HiddenEventComponent event = HiddenEventComponent.MAPPER.get(student);
    if (event == null || event.type != EventType.MUSIC_STUDENT
        || event.state != EventState.AVAILABLE) {
      return false;
    }

    if (player == null || player.isScheduledForRemoval()) {
      return false;
    }

    InventoryComponent inventory = InventoryComponent.MAPPER.get(player);
    if (inventory == null || inventory.addItem(ItemType.TRUMPET) < 0) {
      return false;
    }

    event.state = EventState.COMPLETED;
    student.remove(InteractableComponent.class);
    stats.recordEventTriggered();
    stats.recordEventCompleted();
    stats.recordHiddenEventFound();
    return true;
  }

  /**
   * Spawns a random drop from the spawner's table below it. Once the spawner has no uses
   * left it switches to its empty texture and can no longer be interacted with.
   *
   * @param spawner The spawner being used.
   * @return The spawned entity, or null if nothing was spawned.
   */
  public Entity useSpawner(Entity spawner) {
    HiddenEventComponent event = HiddenEventComponent.MAPPER.get(spawner);
    SpawnerComponent settings = SpawnerComponent.MAPPER.get(spawner);
    TransformComponent transform = TransformComponent.MAPPER.get(spawner);
    if (event == null || event.type != EventType.SPAWNER
        || event.state == EventState.COMPLETED
        || settings == null || settings.remainingUses <= 0 || transform == null) {
      return null;
    }

    final Entity spawned = spawnBelow(spawner, settings.drops.roll(random));
    stats.recordInteraction();

    if (event.state == EventState.AVAILABLE) {
      event.state = EventState.ACTIVE;
      stats.recordEventTriggered();
    }

    settings.remainingUses--;
    if (settings.remainingUses == 0) {
      TextureComponent texture = TextureComponent.MAPPER.get(spawner);
      if (texture != null && settings.emptyRegion != null) {
        texture.region = settings.emptyRegion;
      }
      event.state = EventState.COMPLETED;
      spawner.remove(InteractableComponent.class);
      stats.recordEventCompleted();
    }
    return spawned;
  }

  /**
   * Creates a drop and adds it centred just below an entity.
   *
   * @param source The entity the drop appears below.
   * @param drop   What to spawn.
   * @return The spawned entity.
   */
  private Entity spawnBelow(Entity source, SpawnDrop drop) {
    TransformComponent transform = TransformComponent.MAPPER.get(source);
    Entity spawned = drop.create(0f, 0f, textures.apply(drop.texture()));
    TransformComponent spawnedTransform = TransformComponent.MAPPER.get(spawned);
    spawnedTransform.position.setPosition(
        transform.position.x + (transform.position.width - spawnedTransform.position.width) / 2f,
        transform.position.y - spawnedTransform.position.height - SPAWN_GAP);
    getEngine().addEntity(spawned);
    return spawned;
  }

  /**
   * Spawns an entity's death drop, centred where it was, if it ran out of health.
   *
   * @param entity The entity removed from the engine.
   */
  private void dropOnDeath(Entity entity) {
    DropOnDeathComponent loot = DropOnDeathComponent.MAPPER.get(entity);
    HealthComponent health = HealthComponent.MAPPER.get(entity);
    TransformComponent transform = TransformComponent.MAPPER.get(entity);
    if (loot == null || health == null || !health.isDead() || transform == null
        || getEngine() == null) {
      return;
    }
    Entity spawned = loot.drop.create(0f, 0f, textures.apply(loot.drop.texture()));
    TransformComponent spawnedTransform = TransformComponent.MAPPER.get(spawned);
    spawnedTransform.position.setPosition(
        transform.position.x + (transform.position.width - spawnedTransform.position.width) / 2f,
        transform.position.y + (transform.position.height - spawnedTransform.position.height) / 2f);
    getEngine().addEntity(spawned);
  }

  /**
   * Tames a wild fox: it starts following the player and drops a Food Scatter Launcher.
   *
   * @param fox The fox being petted.
   * @return The dropped launcher pickup, or null if the fox could not be petted.
   */
  public Entity petFox(Entity fox) {
    HiddenEventComponent event = HiddenEventComponent.MAPPER.get(fox);
    FoxComponent foxComponent = FoxComponent.MAPPER.get(fox);
    if (event == null || event.type != EventType.FOX || event.state != EventState.AVAILABLE
        || foxComponent == null || !TransformComponent.MAPPER.has(fox)) {
      return null;
    }

    foxComponent.tamed = true;
    event.state = EventState.COMPLETED;
    fox.remove(InteractableComponent.class);
    stats.recordEventTriggered();
    stats.recordEventCompleted();
    stats.recordHiddenEventFound();
    return spawnBelow(fox, SpawnDrop.item(ItemType.FOOD_SCATTER_LAUNCHER));
  }

  /**
   * Records a visit to a fountain. Each fountain can only be visited once.
   *
   * @param fountain The fountain being visited.
   * @return True if the visit was recorded.
   */
  public boolean visitFountain(Entity fountain) {
    HiddenEventComponent event = HiddenEventComponent.MAPPER.get(fountain);
    if (event == null || event.type != EventType.FOUNTAIN
        || event.state != EventState.AVAILABLE) {
      return false;
    }

    event.state = EventState.COMPLETED;
    fountain.remove(InteractableComponent.class);
    stats.recordFountainVisited();
    stats.recordInteraction();
    stats.recordEventTriggered();
    stats.recordEventCompleted();
    return true;
  }

  /**
   * Opens a locked door if the player has a key, using the key up. The open door is removed
   * so the player can walk through.
   *
   * @param door   The door being opened.
   * @param player The player trying to open it.
   * @return True if the door was opened.
   */
  public boolean openDoor(Entity door, Entity player) {
    HiddenEventComponent event = HiddenEventComponent.MAPPER.get(door);
    if (event == null || event.type != EventType.DOOR || event.state != EventState.AVAILABLE
        || player == null) {
      return false;
    }
    InventoryComponent inventory = InventoryComponent.MAPPER.get(player);
    if (inventory == null) {
      return false;
    }
    int keySlot = -1;
    for (int i = 0; i < inventory.capacity(); ++i) {
      InventorySlot slot = inventory.getSlot(i);
      if (slot != null && slot.item == ItemType.KEY) {
        keySlot = i;
        break;
      }
    }
    if (keySlot < 0) {
      return false;
    }

    inventory.clearSlot(keySlot);
    event.state = EventState.COMPLETED;
    stats.recordEventTriggered();
    stats.recordEventCompleted();
    getEngine().removeEntity(door);
    onDoorOpened.run();
    return true;
  }
}
