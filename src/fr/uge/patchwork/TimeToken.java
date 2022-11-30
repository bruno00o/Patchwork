package fr.uge.patchwork;

public record TimeToken(int position) {
  public TimeToken {
    // could check max value too
    if (position < 0) {
      throw new IllegalArgumentException("Position must be positive");
    }
  }

  public TimeToken forward(int nbBlocks) {
    return new TimeToken(position + nbBlocks);
  }
}
