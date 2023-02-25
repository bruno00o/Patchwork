package fr.uge.patchwork.game;

/**
 * Record for a square of the quilt board
 *
 * @param isFilled (boolean) true if the square is filled
 * @param patch    (Patch) patch on the square
 */
public record QuiltSquare(boolean isFilled, Patch patch) {
  public QuiltSquare {
    if (isFilled && patch == null) {
      throw new IllegalArgumentException("If a quilt square is filled, it must have a patch");
    }
  }

  /**
   * Return true if the square is empty
   *
   * @return (boolean)
   */
  public boolean isEmpty() {
    return !isFilled;
  }

  /***
   * Return the patch on the square
   * @return (Patch)
   */
  public Patch getPatch() {
    return patch;
  }

  /**
   * String representation of the square
   *
   * @return (String)
   */
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
