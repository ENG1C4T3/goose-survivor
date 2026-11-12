package com.eng1c4t3.goosesurvivor.ui;

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
import java.util.List;

/**
 * Draws any {@link Menu}: a dimmed backdrop over whatever is already on screen, a panel with
 * the title, the buttons with the selected one highlighted, and the footer lines. Uses plain
 * shapes and the default font, like {@link InventoryHud}, so it needs no assets.
 */
public class MenuRenderer implements Disposable {
  private static final Color BACKDROP = new Color(0f, 0f, 0f, 0.6f);
  private static final Color PANEL_FILL = new Color(0.1f, 0.1f, 0.14f, 0.95f);
  private static final Color ACCENT = new Color(1f, 0.85f, 0.3f, 1f);
  private static final Color BUTTON_FILL = new Color(0.2f, 0.2f, 0.26f, 1f);
  private static final Color BUTTON_SELECTED_FILL = new Color(0.35f, 0.3f, 0.12f, 1f);
  private static final Color BUTTON_BORDER = new Color(0.5f, 0.5f, 0.55f, 1f);
  private static final Color FOOTER = new Color(0.7f, 0.7f, 0.75f, 1f);

  private static final float PANEL_PADDING = 24f;
  private static final float TITLE_SPACE = 56f;
  private static final float FOOTER_LINE_HEIGHT = 18f;
  private static final float TITLE_SCALE = 2f;
  private static final float BUTTON_SCALE = 1.2f;
  private static final float FOOTER_SCALE = 0.85f;

  private final SpriteBatch batch;
  private final OrthographicCamera camera;
  private final ShapeRenderer shapes = new ShapeRenderer();
  private final BitmapFont font = new BitmapFont();
  private final GlyphLayout layout = new GlyphLayout();
  private final Rectangle bounds = new Rectangle();
  private final Rectangle panel = new Rectangle();

  /**
   * Creates the renderer.
   *
   * @param batch  The batch used to draw text.
   * @param camera The camera the menu's world coordinates are drawn with.
   */
  public MenuRenderer(SpriteBatch batch, OrthographicCamera camera) {
    this.batch = batch;
    this.camera = camera;
  }

  /**
   * Draws a menu on top of whatever has already been rendered.
   *
   * @param menu The menu to draw.
   */
  public void render(Menu<?> menu) {
    final List<? extends Menu.Item<?>> items = menu.getItems();
    final int selected = menu.getSelectedIndex();
    layoutPanel(menu);

    Gdx.gl.glEnable(GL20.GL_BLEND);
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    shapes.setProjectionMatrix(camera.combined);

    shapes.begin(ShapeRenderer.ShapeType.Filled);
    shapes.setColor(BACKDROP);
    shapes.rect(0, 0, menu.getWorldWidth(), menu.getWorldHeight());
    shapes.setColor(PANEL_FILL);
    shapes.rect(panel.x, panel.y, panel.width, panel.height);
    for (int i = 0; i < items.size(); ++i) {
      menu.buttonBounds(i, bounds);
      shapes.setColor(i == selected ? BUTTON_SELECTED_FILL : BUTTON_FILL);
      shapes.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }
    shapes.end();

    shapes.begin(ShapeRenderer.ShapeType.Line);
    shapes.setColor(ACCENT);
    shapes.rect(panel.x, panel.y, panel.width, panel.height);
    for (int i = 0; i < items.size(); ++i) {
      menu.buttonBounds(i, bounds);
      shapes.setColor(i == selected ? ACCENT : BUTTON_BORDER);
      shapes.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }
    shapes.end();

    batch.setProjectionMatrix(camera.combined);
    batch.begin();
    font.getData().setScale(TITLE_SCALE);
    drawCentred(menu, menu.getTitle(), panel.y + panel.height - PANEL_PADDING, ACCENT);

    font.getData().setScale(BUTTON_SCALE);
    for (int i = 0; i < items.size(); ++i) {
      menu.buttonBounds(i, bounds);
      String label = items.get(i).label;
      if (i == selected) {
        label = "> " + label + " <";
      }
      layout.setText(font, label);
      drawCentred(menu, label, bounds.y + bounds.height / 2 + layout.height / 2,
          i == selected ? Color.WHITE : Color.LIGHT_GRAY);
    }

    font.getData().setScale(FOOTER_SCALE);
    float lineY = panel.y + PANEL_PADDING + FOOTER_LINE_HEIGHT * menu.getFooter().size();
    for (String line : menu.getFooter()) {
      drawCentred(menu, line, lineY, FOOTER);
      lineY -= FOOTER_LINE_HEIGHT;
    }
    font.getData().setScale(1f);
    batch.end();
  }

  /** Sizes the panel to wrap the title, buttons and footer. */
  private void layoutPanel(Menu<?> menu) {
    int count = menu.getItems().size();
    final Rectangle first = menu.buttonBounds(0, new Rectangle());
    final Rectangle last = menu.buttonBounds(Math.max(0, count - 1), new Rectangle());

    font.getData().setScale(TITLE_SCALE);
    layout.setText(font, menu.getTitle());
    float widest = Math.max(Menu.BUTTON_WIDTH, layout.width);
    font.getData().setScale(FOOTER_SCALE);
    for (String line : menu.getFooter()) {
      layout.setText(font, line);
      widest = Math.max(widest, layout.width);
    }
    font.getData().setScale(1f);

    float footerSpace = menu.getFooter().isEmpty()
        ? PANEL_PADDING
        : PANEL_PADDING * 2 + FOOTER_LINE_HEIGHT * menu.getFooter().size();
    float width = widest + PANEL_PADDING * 2;
    float top = first.y + first.height + TITLE_SPACE;
    float bottom = last.y - footerSpace;
    panel.set((menu.getWorldWidth() - width) / 2, bottom, width, top - bottom);
  }

  private void drawCentred(Menu<?> menu, String text, float topY, Color color) {
    font.setColor(color);
    layout.setText(font, text);
    font.draw(batch, text, (menu.getWorldWidth() - layout.width) / 2, topY);
  }

  @Override
  public void dispose() {
    shapes.dispose();
    font.dispose();
  }
}
