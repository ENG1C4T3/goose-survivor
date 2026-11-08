package com.eng1c4t3.goosesurvivor;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.eng1c4t3.goosesurvivor.components.TextureComponent;
import com.eng1c4t3.goosesurvivor.components.TransformComponent;

/**
 * Draw world entities independently
 * WorldRenderer
 */
public class WorldRenderer {
    private final SpriteBatch batch;
    private final OrthographicCamera camera;

    /**
     * Creates a renderer
     * 
     * @param batch the batch used to draw textures
     * 
     * @param camera the camera used to project world coordinates
     * 
     * @throws IllegalArgumentException if camera or batch is null
     */
    public WorldRenderer(SpriteBatch batch, 
        OrthographicCamera camera){
        if (batch == null || camera == null){
            throw new IllegalArgumentException(
                "Batch and camera must not be null"
            );
        }
        this.batch = batch;
        this.camera = camera;
    }

    /**
     * Draws entities have position and texture
     * 
     * @param world the world to draw
     * 
     * @throws IllegalArgumentException if world is null
     */
    public void render(GameWorld world){
        if(world == null){
            throw new IllegalArgumentException(
                "World must not be null"
            );
        }
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for(Entity entity : world.getEntities()){
            TransformComponent transform = 
            TransformComponent.MAPPER.get(entity);

            TextureComponent texture = 
            TextureComponent.MAPPER.get(entity);

            if(transform != null && texture != null){
                batch.draw(texture.region, 
                    transform.position.x, transform.position.y);
            }
        }
        batch.end();
    }
}
