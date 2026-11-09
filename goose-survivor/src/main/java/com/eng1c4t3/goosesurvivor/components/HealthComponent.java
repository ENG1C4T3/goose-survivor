package com.eng1c4t3.goosesurvivor.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

public class HealthComponent implements Component {
  public static final ComponentMapper<HealthComponent> MAPPER =
    ComponentMapper.getFor(HealthComponent.class);

  public float health;
  public float maxHealth;

  public HealthComponent(float maxHealth) {
    this.maxHealth = maxHealth;
    this.health = maxHealth;
  }

  public void takeDamage(float damage) {
    health -= damage;
    if (health < 0) {
      health = 0;
    }
  }

  public void heal(float amount) {
    health += amount;
    if (health > maxHealth) {
      health = maxHealth;
    }
  }

  public boolean isDead() {
    return health <= 0;
  }
}
