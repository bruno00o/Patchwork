package patchwork.game;

import java.util.ArrayList;
import java.util.List;

public class CentralBoardSquare {
  private final List<TimeToken> timeTokens;
  private Patch leatherPatch;
  private final Button button;

  public CentralBoardSquare(Patch leatherPatch, Button button) {
    this.timeTokens = new ArrayList<>();
    this.leatherPatch = leatherPatch;
    this.button = button;
  }

  public CentralBoardSquare addTimeToken(TimeToken timeToken) {
    timeTokens.add(timeToken);
    return this;
  }

  public CentralBoardSquare removeTimeToken(TimeToken timeToken) {
    timeTokens.remove(timeToken);
    return this;
  }

  public boolean hasButton() {
    return button != null;
  }

  public boolean hasLeatherPatch() {

    return leatherPatch != null;
  }

  public boolean hasTimeToken() {
    return !timeTokens.isEmpty();
  }

  public TimeToken getTimeToken(char shortName) {
    return timeTokens.stream().filter(t -> t.shortName() == shortName).findFirst().orElse(null);
  }

  public Patch getLeatherPatch() {
    return leatherPatch;
  }

  public void removeLeatherPatch() {
    leatherPatch = null;
  }

  @Override
  public String toString() {
    if (hasLeatherPatch()) {
      return "*";
    } else if (hasButton()) {
      return "x";
    } else if (hasTimeToken()) {
      var sb = new StringBuilder();
      for (var timeToken : timeTokens) {
        sb.append(timeToken.shortName()).append(", ");
      }
      return sb.delete(sb.length() - 2, sb.length()).toString();
    } else {
      return ".";
    }
  }
}
