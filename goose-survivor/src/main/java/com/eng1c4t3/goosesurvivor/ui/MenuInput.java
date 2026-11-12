package com.eng1c4t3.goosesurvivor.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Drives any {@link Menu} from the keyboard and mouse: Up/Down or W/S move the highlight,
 * Enter or Space choose it, hovering highlights a button and left-clicking chooses it.
 * Screen-specific keys (such as Esc to resume) are left to the caller.
 */
public class MenuInput {
  private final Viewport viewport;
  private final Vector2 mouse = new Vector2();

  /**
   * Creates an input handler.
   *
   * @param viewport The viewport the menu is drawn in, used to map the mouse into world space.
   */
  public MenuInput(Viewport viewport) {
    this.viewport = viewport;
  }

  /**
   * Applies this frame's input to a menu.
   *
   * @param menu The menu being shown.
   * @param <T>  The menu's value type.
   * @return The value of the button chosen this frame, or null if none was chosen.
   */
  public <T> T poll(Menu<T> menu) {
    if (Gdx.input.isKeyJustPressed(Input.Keys.UP)
        || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
      menu.moveUp();
    }
    if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)
        || Gdx.input.isKeyJustPressed(Input.Keys.S)) {
      menu.moveDown();
    }

    mouse.set(Gdx.input.getX(), Gdx.input.getY());
    viewport.unproject(mouse);
    int hovered = menu.itemAt(mouse.x, mouse.y);
    // Only follow the mouse when it moves, so it doesn't fight keyboard navigation
    if (hovered >= 0 && (Gdx.input.getDeltaX() != 0 || Gdx.input.getDeltaY() != 0)) {
      menu.select(hovered);
    }
    if (hovered >= 0 && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
      menu.select(hovered);
      return menu.getSelected();
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
        || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_ENTER)
        || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
      return menu.getSelected();
    }
    return null;
  }
}
