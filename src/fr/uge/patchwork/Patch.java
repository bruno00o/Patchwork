package fr.uge.patchwork;

import java.util.Objects;

public record Patch(String format, int price, int forwardBlocks, int earnings) {
  public Patch {
    Objects.requireNonNull(format);
    if (price < 0) {
      throw new IllegalArgumentException("Price must be positive");
    }
    if (forwardBlocks < 0) {
      throw new IllegalArgumentException("Forward blocks must be positive");
    }
    if (earnings < 0) {
      throw new IllegalArgumentException("Earnings must be positive");
    }
  }

  public int getNumberOfBlocks() {
    // count '*' in format use streams
    int count = 0;
    for (int i = 0; i < format.length(); i++) {
      if (format.charAt(i) == '*') {
        count++;
      }
    }
    return count;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Price: ").append(price).append(", Earnings: ").append(earnings).append(", Forward blocks: ").append(forwardBlocks).append("\nFormat:\n");
    var lines = format.split(",");
    for (var line : lines) {
      sb.append(line).append("\n");
    }
    return sb.toString();
  }
}

