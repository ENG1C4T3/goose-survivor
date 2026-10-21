package com.ENG1C4T3.goose_survivor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Player {
    private Vector2 position;
    private Vector2 direction;
    private Vector2 velocity;
    private Rectangle hitbox;
    
    private float width;
    private float height;

    private float speed;
    private float health;
    private float block;
    private float maxHealth;
    private float maxBlock;

    private final Vector2 facing = new Vector2(1f, 0f);

    private static final float HURT_COOLDOWN = 1f;
    private float hurtCooldownRemaining = 0f;
    public Player(float x, float y, float speed, float width, float height,
        float maxHealth, float health, float maxBlock, float block
    ){
        position = new Vector2(x, y);
        direction = new Vector2();
        velocity = new Vector2();
        hitbox = new Rectangle(x, y, width, height);
        this.height = height;
        this.width = width;

        this.maxHealth = maxHealth;
        this.health = health;

        this.maxBlock = maxBlock;
        this.block = block;

        this.speed = speed;
        
    }



    public void update(float delta){
        hurtCooldownRemaining = Math.max(
            0f, (hurtCooldownRemaining - delta));
        direction.setZero();

        if(Gdx.input.isKeyPressed(Input.Keys.W)){
            direction.y += 1;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.S)){
            direction.y -= 1;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.A)){
            direction.x -= 1;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.D)){
            direction.x += 1;
        }
        if(!direction.isZero()){
            direction.nor();
            facing.set(direction);
        }

        velocity.set(direction).scl(speed);
        
        position.mulAdd(velocity, delta);
        hitbox.setPosition(position.x, position.y);
    }

    public void render (ShapeRenderer shapeRenderer){
        shapeRenderer.rect(position.x, position.y, width, height);
    }

    public Vector2 getPosition(){
        return position;
    }

    public Vector2 getFacing(){
        return facing;
    }

    public Rectangle getHitbox(){
        return hitbox;
    }

    //===health & buff system===

    public void gainBlock(float amount){
        if(amount < 0){
            throw new IllegalArgumentException(
                "Block gain should not be negative"
            );
        }
        block = Math.min(maxBlock, block + amount);
    }

    public void takeDamage(float amount){
        if(amount < 0){
            throw new IllegalArgumentException(
                "Damage should not be negative"
            );
        }
        if(amount == 0f || isDead() || hurtCooldownRemaining > 0f){
            return;
        }

        float absorbed = Math.min(block, amount);
        block -= absorbed;

        float remainingDamage = amount - absorbed;
        health = Math.max(0f, health - remainingDamage);

        hurtCooldownRemaining = HURT_COOLDOWN;

        System.out.println("Health: " + health + ", Block: " + block);
        if(isDead()){
            System.out.println("===Game Over===");
        }
    }
    
    public boolean isDead(){
        return health <= 0f;
    }
}
