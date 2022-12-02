package fr.uge.patchwork;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CentralTimeBoard {
  private final int size;
  private final ArrayList<CentralTimeBoardCase> centralTimeBoard;
  private boolean isBasic;

  public CentralTimeBoard(boolean isBasic) {
    this.size = 59;
    this.centralTimeBoard = new ArrayList<>(size);
    this.isBasic = isBasic;
    for (int i = 0; i < size; i++) {
      centralTimeBoard.add(new CentralTimeBoardCase(null, null, 0));
    }
  }

  public void load(Path path, List<Player> players) throws IOException {
    Objects.requireNonNull(path);
    try (var reader = Files.newBufferedReader(path)) {
      String line;
      while ((line = reader.readLine()) != null) {
        var tokens = line.split(" ");
        for (int i = 0; i < tokens.length; i++) {
          var token = tokens[i];
          if (token.equals("0") || (token.equals("*") && isBasic)) {
            centralTimeBoard.set(i, new CentralTimeBoardCase(null, null, 0));
          } else if (token.equals("*")) {
            centralTimeBoard.set(i, new CentralTimeBoardCase(null, new Patch("*", 0, 0, 0), 0));
          } else if (token.equals("x")) {
            centralTimeBoard.set(i, new CentralTimeBoardCase(null, null, 1));
          }
        }
      }
    }
    for (var player : players) {
      var firstCase = centralTimeBoard.get(0);
      centralTimeBoard.set(0, firstCase.addTimeToken(player.getTimeToken()));
    }
  }

  public boolean gameIsOver(Player player1, Player player2, CirclePatches circlePatches) {
    if (player1.getTimeToken().position() == size && player2.getTimeToken().position() == size) {
      return true;
    }
    if (circlePatches.isEmpty()) {
      return true;
    }
    return false;
  }

  public Player whoPlays(Player player1, Player player2) {
    if (player1.getTimeToken().position() < player2.getTimeToken().position()) {
      return player1;
    }
    return player2;
  }

  public void passedTurn(Player actual, Player other) {
    if (actual.getPosition() <= other.getPosition()) {
      actual.addMoney(other.getPosition() - actual.getPosition() + 1);
      actual.setPosition(other.getPosition() + 1, actual.getTimeToken().position());
      action(actual, centralTimeBoard.get(actual.getPosition()));
    }
  }

  public void moveTimeToken(Player player) {
    var timeToken = player.getTimeToken();
    var position = timeToken.position();
    var oldPosition = timeToken.oldPosition();
    var caseOfTimeToken = centralTimeBoard.get(oldPosition);
    centralTimeBoard.set(oldPosition, caseOfTimeToken.removeTimeToken(timeToken));
    var newCase = centralTimeBoard.get(position);
    centralTimeBoard.set(position, newCase.addTimeToken(timeToken));
    action(player, newCase);
  }

  private void action(Player player, CentralTimeBoardCase centralTimeBoardCase) {
    var timeToken = player.getTimeToken();
    if (centralTimeBoardCase.hasLeatherPatch()) {
      System.out.println("You have found a leather patch");
      var patch = centralTimeBoardCase.getLeatherPatch();
      int[] coordinates = Player.askCoordinates();
      if (player.getQuiltBoard().addPatch(patch, coordinates[0], coordinates[1])) {
        centralTimeBoardCase.removeLeatherPatch();
      }
    }
    if (centralTimeBoardCase.hasButton()) {
      System.out.println("You have found a button");
      player.addMoney(1);
      centralTimeBoardCase.removeButton();
    }
  }

  @Override
  public String toString() {
    var sb = new StringBuilder().append("| ");
    for (var centralTimeBoardCase : centralTimeBoard) {
      sb.append(centralTimeBoardCase.toString()).append(" | ");
    }
    return sb.toString();
  }

}
