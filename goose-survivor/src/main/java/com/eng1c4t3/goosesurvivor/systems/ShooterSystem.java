package com.eng1c4t3.goosesurvivor.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.eng1c4t3.goosesurvivor.components.EnemyComponent;
import com.eng1c4t3.goosesurvivor.components.PlayerComponent;
import com.eng1c4t3.goosesurvivor.components.ShooterComponent;
import com.eng1c4t3.goosesurvivor.components.StreamFireComponent;
import com.eng1c4t3.goosesurvivor.components.TextureComponent;
import com.eng1c4t3.goosesurvivor.components.TransformComponent;
import com.eng1c4t3.goosesurvivor.entities.EntityFactory;
import java.util.function.Function;

/**
 * Makes every entity with a {@link ShooterComponent} fire a projectile towards the player once
 * per shot interval.
 *
 * @see ProjectileSystem
 */
public class ShooterSystem extends EntitySystem {

  /**
   * Creates projectile entities. Lets tests spawn projectiles without loading textures.
   */
  @FunctionalInterface
  public interface ProjectileFactory {
    /**
     * Creates a projectile.
     *
     * @param owner The entity firing the projectile.
     * @param centreX The x-coordinate of the projectile's centre.
     * @param centreY The y-coordinate of the projectile's centre.
     * @param velocity The projectile's initial velocity.
     * @param damage The damage the projectile deals.
     * @return The new projectile entity.
     */
    Entity create(Entity owner, float centreX, float centreY, Vector2 velocity, float damage);
  }

  private final ProjectileFactory projectileFactory;
  private final Vector2 direction = new Vector2();
  private Function<String, TextureRegion> textures = null;
  private ImmutableArray<Entity> players;
  private ImmutableArray<Entity> shooters;
  private ImmutableArray<Entity> streams;
  private ImmutableArray<Entity> enemies;

  /**
   * Constructs a shooter system using the supplied projectile texture.
   *
   * @param region The texture region for projectile sprites.
   */
  public ShooterSystem(TextureRegion region) {
    this((owner, centreX, centreY, velocity, damage) ->
        EntityFactory.createProjectile(owner, centreX, centreY, velocity, damage, region));
  }

  /**
   * Constructs a ShooterSystem that creates projectiles with the given factory.
   *
   * @param projectileFactory The factory used to create projectiles.
   */
  public ShooterSystem(ProjectileFactory projectileFactory) {
    this.projectileFactory = projectileFactory;
  }

  /**
   * Sets how projectiles with their own texture (e.g. nuts and seeds) look up that texture.
   *
   * @param textures Returns the texture region for a texture file name.
   */
  public void setTextures(Function<String, TextureRegion> textures) {
    this.textures = textures;
  }

  /**
   * Fires one projectile with its own texture, sized to that texture. Falls back to the
   * default projectile if {@code texture} is null or no textures have been set.
   *
   * @param owner    The entity firing the projectile.
   * @param centreX  The x-coordinate of the projectile's centre.
   * @param centreY  The y-coordinate of the projectile's centre.
   * @param velocity The projectile's initial velocity.
   * @param damage   The damage the projectile deals.
   * @param texture  The projectile's texture file name, or null.
   */
  public void fire(Entity owner, float centreX, float centreY, Vector2 velocity, float damage,
      String texture) {
    Entity projectile = projectileFactory.create(owner, centreX, centreY, velocity, damage);
    TextureRegion region = texture == null || textures == null ? null : textures.apply(texture);
    if (region != null) {
      TextureComponent look = TextureComponent.MAPPER.get(projectile);
      if (look == null) {
        projectile.add(new TextureComponent(region));
      } else {
        look.region = region;
      }
      TransformComponent transform = TransformComponent.MAPPER.get(projectile);
      if (transform != null && region.getRegionWidth() > 0 && region.getRegionHeight() > 0) {
        transform.position.setSize(region.getRegionWidth(), region.getRegionHeight());
        transform.position.setCenter(centreX, centreY);
      }
    }
    getEngine().addEntity(projectile);
  }

  /**
   * Fires one projectile, e.g. for an item. It never hits {@code owner}.
   *
   * @param owner    The entity firing the projectile.
   * @param centreX  The x-coordinate of the projectile's centre.
   * @param centreY  The y-coordinate of the projectile's centre.
   * @param velocity The projectile's initial velocity.
   * @param damage   The damage the projectile deals.
   */
  public void fire(Entity owner, float centreX, float centreY, Vector2 velocity, float damage) {
    getEngine().addEntity(projectileFactory.create(owner, centreX, centreY, velocity, damage));
  }

  /**
   * Caches the player and shooter entities.
   *
   * @param engine The {@link Engine} this system was added to.
   */
  @Override
  public void addedToEngine(Engine engine) {
    players = engine.getEntitiesFor(
        Family.all(PlayerComponent.class, TransformComponent.class).get());
    shooters = engine.getEntitiesFor(
        Family.all(ShooterComponent.class, TransformComponent.class).get());
    streams = engine.getEntitiesFor(
        Family.all(StreamFireComponent.class, TransformComponent.class).get());
    enemies = engine.getEntitiesFor(
        Family.all(EnemyComponent.class, TransformComponent.class).get());
  }

  /**
   * Ticks each shooter's timer and fires a projectile at the player when it runs out.
   *
   * @param deltaTime The time passed since the last frame.
   */
  @Override
  public void update(float deltaTime) {
    updateStreams(deltaTime);
    if (players.size() == 0) {
      return;
    }
    Rectangle target = TransformComponent.MAPPER.get(players.first()).position;

    for (int i = 0; i < shooters.size(); ++i) {
      Entity shooter = shooters.get(i);
      ShooterComponent shooterComponent = ShooterComponent.MAPPER.get(shooter);

      shooterComponent.shotTimer -= deltaTime;
      if (shooterComponent.shotTimer > 0) {
        continue;
      }
      shooterComponent.shotTimer += shooterComponent.shotInterval;

      Rectangle origin = TransformComponent.MAPPER.get(shooter).position;
      float originX = origin.x + origin.width / 2;
      float originY = origin.y + origin.height / 2;
      direction.set(target.x + target.width / 2 - originX,
          target.y + target.height / 2 - originY);
      if (direction.isZero()) {
        continue;
      }
      direction.nor().scl(shooterComponent.projectileSpeed);

      getEngine().addEntity(projectileFactory.create(
          shooter, originX, originY, direction, shooterComponent.projectileDamage));
    }
  }

  /**
   * Fires the next shot of every active stream that is due, each aimed at where its owner is
   * aiming right now, and ends streams that have fired every shot.
   *
   * @param deltaTime The time passed since the last frame.
   */
  private void updateStreams(float deltaTime) {
    // Iterate backwards: finishing a stream removes its component, shrinking the array.
    for (int i = streams.size() - 1; i >= 0; --i) {
      Entity entity = streams.get(i);
      StreamFireComponent stream = StreamFireComponent.MAPPER.get(entity);
      stream.timer -= deltaTime;
      while (stream.timer <= 0f && stream.shotsRemaining > 0) {
        stream.timer += stream.interval;
        fireStreamShot(entity, stream);
        stream.shotsRemaining--;
        stream.shotsFired++;
      }
      if (stream.shotsRemaining <= 0) {
        entity.remove(StreamFireComponent.class);
      }
    }
  }

  /**
   * Fires one shot of a stream from the centre of its owner.
   */
  private void fireStreamShot(Entity owner, StreamFireComponent stream) {
    Rectangle origin = TransformComponent.MAPPER.get(owner).position;
    float originX = origin.x + origin.width / 2;
    float originY = origin.y + origin.height / 2;

    if (!aimDirection(owner, direction)) {
      return;
    }
    // Wobble each shot a little either side of the aim so it reads as a spray of food.
    float wobble = stream.wobbleDegrees / 2f * (float) Math.sin(stream.shotsFired * 1.7);
    direction.nor().scl(stream.speed).rotateDeg(wobble);
    fire(owner, originX, originY, new Vector2(direction), stream.damage,
        stream.projectileTexture);
  }

  /**
   * Works out which way an entity is aiming: from its centre towards the player's mouse aim,
   * or towards the nearest enemy if it has no mouse aim.
   *
   * @param owner The entity aiming, normally the player.
   * @param out   Set to the (unnormalised) aim direction.
   * @return False if there is nothing to aim at.
   */
  public boolean aimDirection(Entity owner, Vector2 out) {
    TransformComponent transform = TransformComponent.MAPPER.get(owner);
    if (transform == null) {
      return false;
    }
    Rectangle origin = transform.position;
    float originX = origin.x + origin.width / 2;
    float originY = origin.y + origin.height / 2;

    PlayerComponent player = PlayerComponent.MAPPER.get(owner);
    if (player != null && player.hasAim) {
      out.set(player.aim.x - originX, player.aim.y - originY);
    } else if (!aimAtNearestEnemy(originX, originY, out)) {
      return false;
    }
    return !out.isZero();
  }

  /**
   * Points {@code out} from a point at the centre of the nearest enemy.
   *
   * @return False if there are no enemies.
   */
  private boolean aimAtNearestEnemy(float originX, float originY, Vector2 out) {
    float nearest = Float.MAX_VALUE;
    boolean found = false;
    for (int i = 0; i < enemies.size(); ++i) {
      Rectangle to = TransformComponent.MAPPER.get(enemies.get(i)).position;
      float dx = to.x + to.width / 2 - originX;
      float dy = to.y + to.height / 2 - originY;
      float distance = dx * dx + dy * dy;
      if (distance < nearest && distance > 0f) {
        nearest = distance;
        out.set(dx, dy);
        found = true;
      }
    }
    return found;
  }
}
