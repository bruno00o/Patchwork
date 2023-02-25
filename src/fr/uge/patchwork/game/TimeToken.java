package fr.uge.patchwork.game;

/**
 * Record for a time token
 *
 * @param position  (int) position of the time token
 * @param shortName (char) short name of the player
 */
public record TimeToken(int position, char shortName) {
  public TimeToken {
    if (position < 0) {
      throw new IllegalArgumentException("Position must be positive");
    }
    if (shortName == ' ') {
      throw new IllegalArgumentException("Short name must be visible");
    }
  }

  /**
   * Move the time token forward
   *
   * @param nbBlocks (int) number of blocks to move forward
   * @return (TimeToken) new time token with the new position set
   */
  public TimeToken forward(int nbBlocks) {
    return new TimeToken(position + nbBlocks, shortName);
  }

  /**
   * Set the position of the time token and return a new time token
   *
   * @param position (int) new position
   * @return (TimeToken) new time token with the new position set
   */
  public TimeToken setPosition(int position) {
    return new TimeToken(position, shortName);
  }
}
