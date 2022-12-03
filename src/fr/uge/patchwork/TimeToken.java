package fr.uge.patchwork;

import java.util.Objects;

public record TimeToken(int position, int oldPosition, char shortName) {
  public TimeToken {
    // could check max value too
    Objects.requireNonNull(shortName);
    if (position < 0) {
      throw new IllegalArgumentException("Position must be positive");
    }
  }

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
