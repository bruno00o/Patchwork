package patchwork.game;

public record TimeToken(int position, char shortName) {
  public TimeToken {
    if (position < 0) {
      throw new IllegalArgumentException("Position must be positive");
    }
  }

  public TimeToken forward(int nbBlocks) {
    return new TimeToken(position + nbBlocks, shortName);
  }

  public TimeToken setPosition(int position) {
    return new TimeToken(position, shortName);
  }

  public int getPosition() {
    return position;
  }
}
