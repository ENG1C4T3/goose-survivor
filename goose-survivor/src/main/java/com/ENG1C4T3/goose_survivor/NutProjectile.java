package com.ENG1C4T3.goose_survivor;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class NutProjectile {
    private final Vector2 position;
    private final Vector2 velocity;
    private final float radius;

    public NutProjectile(float x, float y, 
        Vector2 direction, float speed, float radius){
            this.position = new Vector2(x, y);
            this.velocity = direction.cpy().nor().scl(speed);
            this.radius = radius;
    }
    
    public void update(float delta){
        position.mulAdd(velocity, delta);
    }

    public void render(ShapeRenderer sr){
        sr.circle(position.x, position.y, radius);
    }

}
