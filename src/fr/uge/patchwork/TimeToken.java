package fr.uge.patchwork;

import java.util.Objects;

public record TimeToken(int position, char shortName) {
  public TimeToken {
    // could check max value too
    Objects.requireNonNull(shortName);
    if (position < 0) {
      throw new IllegalArgumentException("Position must be positive");
    }
  }

  public TimeToken forward(int nbBlocks) {
    return new TimeToken(position + nbBlocks, shortName);
  }
}
