package fr.uge.patchwork;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record CentralTimeBoardCase(List<TimeToken> timeTokens, int leatherPatch, int button) {
  public CentralTimeBoardCase {
    // Objects.requireNonNull(timeToken);
    if (leatherPatch < 0) {
      throw new IllegalArgumentException("Leather patch must be positive or null");
    }
    if (button < 0) {
      throw new IllegalArgumentException("Button must be positive or null");
    }
  }

  public CentralTimeBoardCase addTimeToken(TimeToken timeToken) {
    if (timeTokens != null) {
      timeTokens.add(timeToken);
    } else {
      var copy = new ArrayList<TimeToken>();
      copy.add(timeToken);
      return new CentralTimeBoardCase(copy, leatherPatch, button);
    }
    return this;
  }

  public CentralTimeBoardCase removeTimeToken(TimeToken timeToken) {
    Objects.requireNonNull(timeToken);
    // remove by shortName
    timeTokens.stream().filter(t -> t.shortName() == timeToken.shortName()).findFirst().ifPresent(timeTokens::remove);
    return this;
  }

  @Override
  public String toString() {
    if (timeTokens != null && !timeTokens.isEmpty()) {
      StringBuilder sb = new StringBuilder();
      for (var timeToken : timeTokens) {
        sb.append(timeToken.shortName()).append(", ");
      }
      sb.delete(sb.length() - 2, sb.length());
      return sb.toString();
    } else if (timeTokens != null) {
      return " ";
    }
    if (leatherPatch != 0) {
      return "*";
    }
    if (button != 0) {
      return "x";
    }
    return " ";
  }
}
