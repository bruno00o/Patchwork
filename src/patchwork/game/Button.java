package patchwork.game;

public record Button(int value) {
  public Button {
    if (value < 0) {
      throw new IllegalArgumentException("Value must be positive");
    }
  }
}
