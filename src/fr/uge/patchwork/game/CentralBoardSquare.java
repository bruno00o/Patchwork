package fr.uge.patchwork.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class for a central board square
 */
public class CentralBoardSquare {

  private final List<TimeToken> timeTokens;
  private Patch leatherPatch;
  private final Button button;

  /**
   * Init a central board square
   * Here leatherPatch and button can be null if the square is empty
   *
   * @param leatherPatch (Patch) leather patch on the square
   * @param button       (Button) button on the square
   */
  public CentralBoardSquare(Patch leatherPatch, Button button) {
    this.timeTokens = new ArrayList<>();
    this.leatherPatch = leatherPatch;
    this.button = button;
  }

  /**
   * Add a time token to the square
   *
   * @param timeToken (TimeToken) time token to add
   * @return (CentralBoardSquare) the square
   */
  public CentralBoardSquare addTimeToken(TimeToken timeToken) {
    Objects.requireNonNull(timeToken);
    timeTokens.add(timeToken);
    return this;
  }

  /**
   * Remove a time token from the square
   *
   * @param timeToken (TimeToken) time token to remove
   */
  public void removeTimeToken(TimeToken timeToken) {
    Objects.requireNonNull(timeToken);
    timeTokens.remove(timeToken);
  }

  /**
   * Return true is the square has a button
   *
   * @return (boolean)
   */
  public boolean hasButton() {
    return button != null;
  }

  /**
   * Return true is the square has a leather patch
   *
   * @return (boolean)
   */
  public boolean hasLeatherPatch() {
    return leatherPatch != null;
  }

  /**
   * Return true is the square has a time token
   *
   * @return (boolean)
   */
  public boolean hasTimeToken() {
    return !timeTokens.isEmpty();
  }

  /**
   * Get the time tokens on the square
   *
   * @return (List < TimeToken >) time tokens
   */
  public List<TimeToken> getTimeTokens() {
    return timeTokens;
  }

  /**
   * Get the time token based on its short name
   *
   * @param shortName (String) short name of the time token
   * @return (TimeToken) time token
   */
  public TimeToken getTimeToken(char shortName) {
    return timeTokens.stream().filter(t -> t.shortName() == shortName).findFirst().orElse(null);
  }

  /**
   * Get the leather patch on the square
   *
   * @return (Patch) leather patch
   */
  public Patch getLeatherPatch() {
    return leatherPatch;
  }

  /**
   * Remove the leather patch from the square
   */
  public void removeLeatherPatch() {
    leatherPatch = null;
  }

  /**
   * Return a string representation of the square
   *
   * @return (String) string representation
   */
  @Override
  public String toString() {
    if (hasTimeToken()) {
      var sb = new StringBuilder();
      for (var timeToken : timeTokens) {
        sb.append(timeToken.shortName()).append(", ");
      }
      return sb.delete(sb.length() - 2, sb.length()).toString();
    } else if (hasLeatherPatch()) {
      return "*";
    } else if (hasButton()) {
      return "x";
    } else {
      return ".";
    }
  }
}
