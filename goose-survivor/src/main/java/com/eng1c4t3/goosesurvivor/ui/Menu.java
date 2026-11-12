package com.eng1c4t3.goosesurvivor.ui;

import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A vertical list of labelled buttons with a title, optional footer lines and a highlighted
 * selection. Each button carries a value of type {@code T} (typically an enum of actions) that
 * is returned when it is chosen, so the same menu can back a pause menu, a start screen or a
 * game over screen.
 *
 * <p>Holds no graphics resources, so it can be used and tested without an OpenGL context;
 * drawing is done by {@link MenuRenderer} and input by {@link MenuInput}.
 *
 * @param <T> the type of value each button represents
 */
public class Menu<T> {
  /** Width of each button, in world units. */
  public static final float BUTTON_WIDTH = 220f;
  /** Height of each button, in world units. */
  public static final float BUTTON_HEIGHT = 40f;
  /** Vertical gap between buttons, in world units. */
  public static final float BUTTON_GAP = 12f;

  /**
   * A single button: the text shown and the value it represents.
   *
   * @param <T> the type of value the button represents
   */
  public static final class Item<T> {
    public final String label;
    public final T value;

    private Item(String label, T value) {
      this.label = label;
      this.value = value;
    }
  }

  private final String title;
  private final float worldWidth;
  private final float worldHeight;
  private final List<Item<T>> items = new ArrayList<>();
  private final List<String> footer = new ArrayList<>();
  private int selectedIndex = 0;

  /**
   * Creates an empty menu centred in a world of the given size.
   *
   * @param title       Heading drawn above the buttons.
   * @param worldWidth  Width of the visible world.
   * @param worldHeight Height of the visible world.
   */
  public Menu(String title, float worldWidth, float worldHeight) {
    this.title = title;
    this.worldWidth = worldWidth;
    this.worldHeight = worldHeight;
  }

  /**
   * Appends a button.
   *
   * @param label Text shown on the button.
   * @param value Value returned when the button is chosen.
   * @return this menu, for chaining.
   */
  public Menu<T> addItem(String label, T value) {
    items.add(new Item<>(label, value));
    return this;
  }

  /**
   * Appends a line of small text drawn below the buttons, such as a controls reminder.
   *
   * @param line The text to show.
   * @return this menu, for chaining.
   */
  public Menu<T> addFooterLine(String line) {
    footer.add(line);
    return this;
  }

  public String getTitle() {
    return title;
  }

  public List<Item<T>> getItems() {
    return Collections.unmodifiableList(items);
  }

  public List<String> getFooter() {
    return Collections.unmodifiableList(footer);
  }

  public float getWorldWidth() {
    return worldWidth;
  }

  public float getWorldHeight() {
    return worldHeight;
  }

  public int getSelectedIndex() {
    return selectedIndex;
  }

  /** Returns the value of the highlighted button, or null if the menu is empty. */
  public T getSelected() {
    return items.isEmpty() ? null : items.get(selectedIndex).value;
  }

  /**
   * Highlights the button at an index.
   *
   * @param index Index of the button.
   * @throws IndexOutOfBoundsException if there is no button at the index.
   */
  public void select(int index) {
    if (index < 0 || index >= items.size()) {
      throw new IndexOutOfBoundsException("No menu item at index " + index);
    }
    selectedIndex = index;
  }

  /** Moves the highlight to the first button, used whenever the menu is opened. */
  public void reset() {
    selectedIndex = 0;
  }

  /** Moves the highlight up one button, wrapping to the bottom. */
  public void moveUp() {
    if (!items.isEmpty()) {
      selectedIndex = (selectedIndex - 1 + items.size()) % items.size();
    }
  }

  /** Moves the highlight down one button, wrapping to the top. */
  public void moveDown() {
    if (!items.isEmpty()) {
      selectedIndex = (selectedIndex + 1) % items.size();
    }
  }

  /**
   * Writes the bounds of a button into {@code out}.
   *
   * @param index Index of the button.
   * @param out   The rectangle to write into.
   * @return {@code out}, for chaining.
   */
  public Rectangle buttonBounds(int index, Rectangle out) {
    int count = items.size();
    float stackHeight = count * BUTTON_HEIGHT + Math.max(0, count - 1) * BUTTON_GAP;
    // Stack sits slightly below centre to leave room for the title above it
    float top = worldHeight / 2 + stackHeight / 2 - 20f;
    float y = top - BUTTON_HEIGHT - index * (BUTTON_HEIGHT + BUTTON_GAP);
    return out.set((worldWidth - BUTTON_WIDTH) / 2, y, BUTTON_WIDTH, BUTTON_HEIGHT);
  }

  /**
   * Finds the button containing a world-space point.
   *
   * @param x World x-coordinate.
   * @param y World y-coordinate.
   * @return The index of the button under the point, or -1 if none.
   */
  public int itemAt(float x, float y) {
    Rectangle bounds = new Rectangle();
    for (int i = 0; i < items.size(); ++i) {
      if (buttonBounds(i, bounds).contains(x, y)) {
        return i;
      }
    }
    return -1;
  }
}
