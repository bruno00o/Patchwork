package fr.uge.patchwork.game;

/**
 * Interface for a button
 *
 * @param value (int) value of the button
 */
public record Button(int value) {
  public Button {
    if (value < 0) {
      throw new IllegalArgumentException("Value must be positive");
    }
  }
}
