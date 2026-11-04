package com.eng1c4t3.goosesurvivor.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.eng1c4t3.goosesurvivor.components.TextureComponent;
import com.eng1c4t3.goosesurvivor.components.TransformComponent;

/**
 * Renders entities with textures and positions.
 *
 * @see TextureComponent
 */
public class RenderSystem extends IteratingSystem {
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
    super(Family.all(TransformComponent.class, TextureComponent.class).get());
    this.batch = batch;
    this.camera = camera;
  }

  /**
   * Updates camera, sets projection matrix, and renders all matching entities within the batch.
   *
   * @param deltaTime The time passed since the last frame.
   */
  @Override
  public void update(float deltaTime) {
    camera.update();
    batch.setProjectionMatrix(camera.combined);
    batch.begin();
    super.update(deltaTime);
    batch.end();
  }

  /**
   * Renders a single entity with its texture and position.
   *
   * @param entity The entity to render.
   * @param deltaTime The time passed since the last frame.
   */
  @Override
  protected void processEntity(Entity entity, float deltaTime) {
    TransformComponent transform = TransformComponent.MAPPER.get(entity);
    TextureComponent textureComp = TextureComponent.MAPPER.get(entity);

    batch.draw(textureComp.region, transform.position.x, transform.position.y);
  }
}
