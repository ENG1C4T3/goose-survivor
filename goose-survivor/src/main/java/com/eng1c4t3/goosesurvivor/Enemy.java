package com.eng1c4t3.goosesurvivor;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Enemy {
  private Vector2 position;
  private Vector2 direction;
  private Rectangle hitbox;
  private float height;
  private float width;
  private float speed;
  private float health;


  public Enemy(float x, float y, float speed, float width, float height, float health) {
    position = new Vector2(x, y);
    direction = new Vector2();
    this.speed = speed;

    if (health <= 0) {
      throw new IllegalArgumentException("Initial health should be positive");
    } else {
      this.health = health;
    }

    this.height = height;
    this.width = width;
    hitbox = new Rectangle(x, y, width, height);

  }



  public void update(float delta, Vector2 playerPosition) {
    direction.set(playerPosition).sub(position);

    if (!direction.isZero()) {
      direction.nor();
    }
    position.mulAdd(direction, speed * delta);
    hitbox.setPosition(position.x, position.y);
  }

  public void render(ShapeRenderer shapeRenderer) {
    shapeRenderer.rect(position.x, position.y, width, height);
  }

  public Vector2 getPosition() {
    return position;
  }

  public Rectangle getHitbox() {
    return hitbox;
  }

  public boolean isDead() {
    return health <= 0;
  }

  public void takeDamage(float amount) {
    if (amount < 0) {
      throw new IllegalArgumentException(
          "Damage should not be negative"
      );
    }
    if (amount == 0f || isDead()) {
      return;
    }
    health = Math.max(0f, health - amount);
    System.out.println("Enemy health: " + health);
  }

}
