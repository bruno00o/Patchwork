package fr.uge.patchwork;

import java.util.Objects;

public record CentralTimeBoardCase(TimeToken timeToken, int leatherPatch, int button) {
  public CentralTimeBoardCase {
    // Objects.requireNonNull(timeToken);
    if (leatherPatch < 0) {
      throw new IllegalArgumentException("Leather patch must be positive or null");
    }
    if (button < 0) {
      throw new IllegalArgumentException("Button must be positive or null");
    }
  }
}
