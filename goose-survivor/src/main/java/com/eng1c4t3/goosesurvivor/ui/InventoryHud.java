package com.eng1c4t3.goosesurvivor.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import com.eng1c4t3.goosesurvivor.GameSession;
import com.eng1c4t3.goosesurvivor.SessionState;
import com.eng1c4t3.goosesurvivor.components.HealthComponent;
import com.eng1c4t3.goosesurvivor.components.InteractableComponent;
import com.eng1c4t3.goosesurvivor.components.InventoryComponent;
import com.eng1c4t3.goosesurvivor.components.ItemPickupComponent;
import com.eng1c4t3.goosesurvivor.components.PlayerComponent;
import com.eng1c4t3.goosesurvivor.components.TransformComponent;
import com.eng1c4t3.goosesurvivor.items.InventorySlot;
import com.eng1c4t3.goosesurvivor.systems.InputSystem;

/**
 * Temporary debug HUD: draws the player's health, an inventory hotbar with uses and cooldowns,
 * labels above item pickups, an interact prompt popup (e.g. "[E] Pick up Coffee") above
 * the interactable the player is in range of, and a pause banner.
 *
 * <p>Uses the libGDX default font and plain shapes so it needs no assets. Draws in world
 * coordinates, so it relies on the camera not moving.
 */
public class InventoryHud implements Disposable {
  private static final float SLOT_SIZE = 64f;
  private static final float SLOT_GAP = 8f;
  private static final float BOTTOM_MARGIN = 10f;

  private static final Color SLOT_FILL = new Color(0.15f, 0.15f, 0.2f, 0.85f);
  private static final Color SLOT_READY_BORDER = new Color(0.4f, 0.9f, 0.4f, 1f);
  private static final Color SLOT_BORDER = new Color(0.6f, 0.6f, 0.6f, 1f);
  private static final Color COOLDOWN_FILL = new Color(0f, 0f, 0f, 0.65f);

  private static final float PROMPT_PADDING = 6f;
  private static final float PROMPT_OFFSET = 8f;
  private static final float PROMPT_EDGE_MARGIN = 4f;
  private static final Color PROMPT_FILL = new Color(0f, 0f, 0f, 0.75f);
  private static final Color PROMPT_BORDER = new Color(1f, 0.85f, 0.3f, 1f);

  private final SpriteBatch batch;
  private final OrthographicCamera camera;
  private final float worldWidth;
  private final float worldHeight;
  private final ShapeRenderer shapes = new ShapeRenderer();
  private final BitmapFont font = new BitmapFont();
  private final GlyphLayout layout = new GlyphLayout();
  private final Rectangle promptBox = new Rectangle();

  /**
   * Creates the HUD.
   *
   * @param batch The batch used to draw text.
   * @param camera The world camera.
   * @param worldWidth Width of the visible world.
   * @param worldHeight Height of the visible world.
   */
  public InventoryHud(SpriteBatch batch, OrthographicCamera camera,
                      float worldWidth, float worldHeight) {
    this.batch = batch;
    this.camera = camera;
    this.worldWidth = worldWidth;
    this.worldHeight = worldHeight;
  }

  /**
   * Draws the HUD for a session.
   *
   * @param session The session to draw the HUD for.
   */
  public void render(GameSession session) {
    final Entity player = findPlayer(session);
    final InventoryComponent inventory =
        player == null ? null : InventoryComponent.MAPPER.get(player);
    // Same lookup the interact key uses, so the popup always matches what E will do
    final Entity target = player == null
        ? null : InputSystem.findNearestInteractable(player, session.getWorld().getEntities());
    final String prompt = target == null
        ? null : InputSystem.interactPrompt(InteractableComponent.MAPPER.get(target));

    Gdx.gl.glEnable(GL20.GL_BLEND);
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    shapes.setProjectionMatrix(camera.combined);
    if (inventory != null) {
      drawSlotShapes(inventory);
    }
    if (prompt != null) {
      layoutPromptBox(target, prompt);
      drawPromptBox();
    }

    batch.setProjectionMatrix(camera.combined);
    batch.begin();
    if (player != null) {
      drawHealth(player);
    }
    if (inventory != null) {
      drawSlotText(inventory);
    }
    drawPickupLabels(session, target);
    if (prompt != null) {
      drawPromptText(prompt);
    }
    if (session.getState() == SessionState.PAUSED) {
      drawCentred("PAUSED (P to resume)", worldWidth / 2, worldHeight / 2, Color.WHITE);
    }
    batch.end();
  }

  /**
   * Sizes the prompt popup to its text and places it just above the target, kept on screen.
   */
  private void layoutPromptBox(Entity target, String prompt) {
    layout.setText(font, prompt);
    float width = layout.width + PROMPT_PADDING * 2;
    float height = layout.height + PROMPT_PADDING * 2;

    Rectangle bounds = TransformComponent.MAPPER.get(target).position;
    float x = bounds.x + bounds.width / 2 - width / 2;
    float y = bounds.y + bounds.height + PROMPT_OFFSET;
    x = Math.max(PROMPT_EDGE_MARGIN, Math.min(x, worldWidth - width - PROMPT_EDGE_MARGIN));
    y = Math.min(y, worldHeight - height - PROMPT_EDGE_MARGIN);
    promptBox.set(x, y, width, height);
  }

  private void drawPromptBox() {
    shapes.begin(ShapeRenderer.ShapeType.Filled);
    shapes.setColor(PROMPT_FILL);
    shapes.rect(promptBox.x, promptBox.y, promptBox.width, promptBox.height);
    shapes.end();

    shapes.begin(ShapeRenderer.ShapeType.Line);
    shapes.setColor(PROMPT_BORDER);
    shapes.rect(promptBox.x, promptBox.y, promptBox.width, promptBox.height);
    shapes.end();
  }

  private void drawPromptText(String prompt) {
    font.setColor(Color.WHITE);
    font.draw(batch, prompt, promptBox.x + PROMPT_PADDING,
        promptBox.y + promptBox.height - PROMPT_PADDING);
  }

  private Entity findPlayer(GameSession session) {
    for (Entity entity : session.getWorld().getEntities()) {
      if (PlayerComponent.MAPPER.has(entity)) {
        return entity;
      }
    }
    return null;
  }

  private float slotX(InventoryComponent inventory, int index) {
    float total = inventory.capacity() * SLOT_SIZE + (inventory.capacity() - 1) * SLOT_GAP;
    return (worldWidth - total) / 2 + index * (SLOT_SIZE + SLOT_GAP);
  }

  private void drawSlotShapes(InventoryComponent inventory) {
    shapes.begin(ShapeRenderer.ShapeType.Filled);
    for (int i = 0; i < inventory.capacity(); ++i) {
      float x = slotX(inventory, i);
      shapes.setColor(SLOT_FILL);
      shapes.rect(x, BOTTOM_MARGIN, SLOT_SIZE, SLOT_SIZE);

      InventorySlot slot = inventory.getSlot(i);
      if (slot != null && slot.isOnCooldown()) {
        // Dark overlay that shrinks downwards as the cooldown runs out
        shapes.setColor(COOLDOWN_FILL);
        shapes.rect(x, BOTTOM_MARGIN, SLOT_SIZE, SLOT_SIZE * slot.cooldownFraction());
      }
    }
    shapes.end();

    shapes.begin(ShapeRenderer.ShapeType.Line);
    for (int i = 0; i < inventory.capacity(); ++i) {
      InventorySlot slot = inventory.getSlot(i);
      shapes.setColor(slot != null && slot.canUse() ? SLOT_READY_BORDER : SLOT_BORDER);
      shapes.rect(slotX(inventory, i), BOTTOM_MARGIN, SLOT_SIZE, SLOT_SIZE);
    }
    shapes.end();
  }

  private void drawSlotText(InventoryComponent inventory) {
    font.getData().setScale(0.85f);
    for (int i = 0; i < inventory.capacity(); ++i) {
      float x = slotX(inventory, i);
      float top = BOTTOM_MARGIN + SLOT_SIZE;

      if (i < InputSystem.MAX_ITEM_KEYS) {
        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, String.valueOf(i + 1), x + 4, top - 4);
      }

      InventorySlot slot = inventory.getSlot(i);
      if (slot == null) {
        continue;
      }
      drawCentred(slot.item.shortName, x + SLOT_SIZE / 2, top - 22, Color.WHITE);

      String uses = slot.isUnlimited() ? "inf" : "x" + slot.usesRemaining;
      font.setColor(Color.GOLD);
      layout.setText(font, uses);
      font.draw(batch, uses, x + SLOT_SIZE - layout.width - 4, BOTTOM_MARGIN + 16);

      if (slot.isOnCooldown()) {
        drawCentred(String.format("%.1fs", slot.cooldownTimer),
            x + SLOT_SIZE / 2, BOTTOM_MARGIN + 34, Color.SALMON);
      }
    }
    font.getData().setScale(1f);
  }

  private void drawHealth(Entity player) {
    HealthComponent health = HealthComponent.MAPPER.get(player);
    if (health == null) {
      return;
    }
    font.setColor(Color.WHITE);
    font.draw(batch, String.format("HP %.0f / %.0f", health.health, health.maxHealth),
        10, worldHeight - 10);
  }

  /**
   * Labels every pickup with its item name, except the targeted one, which shows the interact
   * prompt in the same place instead.
   */
  private void drawPickupLabels(GameSession session, Entity target) {
    font.getData().setScale(0.8f);
    for (Entity entity : session.getWorld().getEntities()) {
      ItemPickupComponent pickup = ItemPickupComponent.MAPPER.get(entity);
      TransformComponent transform = TransformComponent.MAPPER.get(entity);
      if (pickup == null || transform == null || entity == target) {
        continue;
      }
      Rectangle bounds = transform.position;
      drawCentred(pickup.item.displayName, bounds.x + bounds.width / 2,
          bounds.y + bounds.height + 16, Color.SKY);
    }
    font.getData().setScale(1f);
  }

  private void drawCentred(String text, float centreX, float topY, Color color) {
    font.setColor(color);
    layout.setText(font, text);
    font.draw(batch, text, centreX - layout.width / 2, topY);
  }

  @Override
  public void dispose() {
    shapes.dispose();
    font.dispose();
  }
}
