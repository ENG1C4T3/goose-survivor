package com.eng1c4t3.goosesurvivor.systems;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.eng1c4t3.goosesurvivor.components.TextureComponent;
import com.eng1c4t3.goosesurvivor.components.TransformComponent;
import com.eng1c4t3.goosesurvivor.ecs.Entity;
import com.eng1c4t3.goosesurvivor.ecs.EntitySystem;

/**
 * Renders entities with textures and positions.
 *
 * @see TextureComponent
 */
public class RenderSystem extends EntitySystem {
  private final SpriteBatch batch;
  private final OrthographicCamera camera;

  /**
   * Constructs a RenderSystem that uses a SpriteBatch and an OrthographicCamera
   * for rendering entities.
   *
   * @param batch The {@link SpriteBatch} used to draw textures for entities.
   * @param camera The {@link OrthographicCamera} that defines the viewpoint and
   *               projection for rendering.
   */
  public RenderSystem(SpriteBatch batch, OrthographicCamera camera) {
    this.batch = batch;
    this.camera = camera;
  }

  /**
   * Renders entities with textures and positions.
   *
   * @param deltaTime The time passed since the last frame.
   */
  @Override
  public void update(float deltaTime) {
    camera.update();
    batch.setProjectionMatrix(camera.combined);
    batch.begin();

    for (Entity entity : engine.getEntities()) {
      if (entity.hasComponent(TransformComponent.class)
          && entity.hasComponent(TextureComponent.class)) {
        TransformComponent transform = entity.getComponent(TransformComponent.class);
        TextureComponent textureComp = entity.getComponent(TextureComponent.class);

        batch.draw(textureComp.region, transform.position.x, transform.position.y);
      }
    }
    batch.end();
  }
}
