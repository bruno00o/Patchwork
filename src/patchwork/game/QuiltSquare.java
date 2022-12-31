package patchwork.game;

public record QuiltSquare(boolean isFilled, Patch patch) {
  public QuiltSquare {
    if (isFilled && patch == null) {
      throw new IllegalArgumentException("If a quilt square is filled, it must have a patch");
    }
  }

  public boolean isEmpty() {
    return !isFilled;
  }

  @Override
  public String toString() {
    if (isFilled) {
      return "*";
    }
    return ".";
  }

  public QuiltSquare copy() {
    return new QuiltSquare(isFilled, patch);
  }
}
