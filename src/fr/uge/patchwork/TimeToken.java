package fr.uge.patchwork;

/**
 * Representing player position in the game
 *
 * @param position    (int) position of the player
 * @param oldPosition (int) old position of the player
 * @param shortName   (char) short name of the player
 */
public record TimeToken(int position, int oldPosition, char shortName) {
  public TimeToken {
    // could check max value too
    if (position < 0) {
      throw new IllegalArgumentException("Position must be positive");
    }
  }

  /**
   * Forward the player position
   *
   * @param nbBlocks (int) number of blocks to forward
   * @return (TimeToken) new TimeToken with the new position
   */
  public TimeToken forward(int nbBlocks) {
    return new TimeToken(position + nbBlocks, position, shortName);
  }

  /**
   * Set position of time token
   *
   * @param position (int) new position
   * @return (TimeToken) new TimeToken with the new position
   */
  public TimeToken setPosition(int position) {
    return new TimeToken(position, position, shortName);
  }
}
