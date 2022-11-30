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
      centralTimeBoard.add(new CentralTimeBoardCase(null, 0, 0));
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
            centralTimeBoard.set(i, new CentralTimeBoardCase(null, 0, 0));
          } else if (token.equals("*")) {
            centralTimeBoard.set(i, new CentralTimeBoardCase(null, 1, 0));
          } else if (token.equals("x")) {
            centralTimeBoard.set(i, new CentralTimeBoardCase(null, 0, 1));
          }
        }
      }
    }
    for (var player : players) {
      var firstCase = centralTimeBoard.get(0);
      centralTimeBoard.set(0, firstCase.addTimeToken(player.getTimeToken()));
    }
  }

  public boolean gameIsOver(Player player1, Player player2) {
    if (player1.getTimeToken().position() == size && player2.getTimeToken().position() == size) {
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
      actual.setMoney(other.getPosition() - actual.getPosition() + 1);
      actual.setPosition(other.getPosition() + 1);
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
