package fr.uge.patchwork;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A board for the game Patchwork.
 *
 * @author Seilliebert Bruno & Oeuvrard Dilien
 */
public class CentralTimeBoardCase {
  private final List<TimeToken> timeTokens;
  private Patch leatherPatch;
  private final int button;
  public CentralTimeBoardCase(List<TimeToken> timeTokens, Patch leatherPatch, int button) {
    this.timeTokens = timeTokens;
    this.leatherPatch = leatherPatch;
    this.button = button;
    // Objects.requireNonNull(timeToken);
    if (button < 0) {
      throw new IllegalArgumentException("Button must be positive or null");
    }
  }

  /**
   * Add a time token to the board.
   * @return the timeToken
   */
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

  /**
   * Remove a time token from the board.
   * @return the timeToken
   */
  public CentralTimeBoardCase removeTimeToken(TimeToken timeToken) {
    Objects.requireNonNull(timeToken);
    // remove by shortName
    timeTokens.stream().filter(t -> t.shortName() == timeToken.shortName()).findFirst().ifPresent(timeTokens::remove);
    return this;
  }

  /**
   * Check if the case has a button
   * @return true if the case has a button
   */
  public boolean hasButton() {
    return button > 0;
  }

  /**
   * Check if the case has a leather patch
   * @return true if the case has a leather patch
   */
  public boolean hasLeatherPatch() {
    return leatherPatch != null;
  }

  /**
   * Get a leather patch from the case
   * @return (Patch) the leather patch
   */
  public Patch getLeatherPatch() {
    return leatherPatch;
  }

  /**
   * Remove a leather patch from the case
   */
  public void removeLeatherPatch() {
    leatherPatch = null;
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
    if (leatherPatch != null) {
      return "*";
    }
    if (button != 0) {
      return "x";
    }
    return " ";
  }
}
