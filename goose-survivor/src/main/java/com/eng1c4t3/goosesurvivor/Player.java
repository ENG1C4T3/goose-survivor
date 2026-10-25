package com.eng1c4t3.goosesurvivor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.eng1c4t3.goosesurvivor.util.ValidatorUtil;
import jakarta.validation.constraints.Positive;

/*
 * This class represents the player in the game.
 */
public class Player {
  private Vector2 position;
  private Vector2 direction;
  private Vector2 velocity;
  private Rectangle hitbox;

  @Positive(message = "Speed should be positive")
  private float speed;
  @Positive(message = "Width should be positive")
  private final float width;
  @Positive(message = "Height should be positive")
  private final float height;
  private float health;
  @Positive(message = "maxHealth should be positive")
  private float maxHealth;

  private final Vector2 facing = new Vector2(1f, 0f);

  private static final float HURT_COOLDOWN = 1f;
  private float hurtCooldownRemaining = 0f;

  public Player(float x, float y, float speed, float width, float height, float maxHealth) {
    position = new Vector2(x, y);
    direction = new Vector2();
    velocity = new Vector2();
    hitbox = new Rectangle(x, y, width, height);

    this.width = width;
    this.height = height;
    this.maxHealth = maxHealth;
    this.health = maxHealth;
    this.speed = speed;

    ValidatorUtil.validate(this);
  }


  public void update(float delta) {
    hurtCooldownRemaining = Math.max(
        0f, (hurtCooldownRemaining - delta));
    direction.setZero();

    if (Gdx.input.isKeyPressed(Input.Keys.W)) {
      direction.y += 1;
    }
    if (Gdx.input.isKeyPressed(Input.Keys.S)) {
      direction.y -= 1;
    }
    if (Gdx.input.isKeyPressed(Input.Keys.A)) {
      direction.x -= 1;
    }
    if (Gdx.input.isKeyPressed(Input.Keys.D)) {
      direction.x += 1;
    }
    if (!direction.isZero()) {
      direction.nor();
      facing.set(direction);
    }

    velocity.set(direction).scl(speed);

    position.mulAdd(velocity, delta);
    hitbox.setPosition(position.x, position.y);
  }

  public void render(ShapeRenderer shapeRenderer) {
    shapeRenderer.rect(position.x, position.y, hitbox.width, hitbox.height);
  }

  public Vector2 getPosition() {
    return position;
  }

  public Vector2 getFacing() {
    return facing;
  }

  public Rectangle getHitbox() {
    return hitbox;
  }

  public void takeDamage(float amount) {
    if (amount <= 0f) {
      throw new IllegalArgumentException("Damage amount should be positive");
    }
    if (hurtCooldownRemaining > 0f || health <= 0) {
      return;
    }
    health = Math.max(0f, health - amount);
    hurtCooldownRemaining = HURT_COOLDOWN;

    if (health <= 0) {
      System.out.println("===Game Over===");
    }
  }
}
