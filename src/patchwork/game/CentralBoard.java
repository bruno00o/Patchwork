package patchwork.game;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CentralBoard {
  private final ArrayList<CentralBoardSquare> centralBoard;

  public CentralBoard() {
    this.centralBoard = new ArrayList<>();
  }

  public void load(Path path) throws IOException {
    Objects.requireNonNull(path);
    try (var reader = Files.newBufferedReader(path)) {
      String line;
      while ((line = reader.readLine()) != null) {
        var tokens = line.split("");
        for (var token : tokens) {
          if (token.equals(".")) {
            centralBoard.add(new CentralBoardSquare(null, null));
          } else if (token.equals("*")) {
            centralBoard.add(new CentralBoardSquare(new Patch("*", 0, 0, 0), null));
          } else if (token.equals("x")) {
            centralBoard.add(new CentralBoardSquare(null, new Button(1)));
          }
        }
      }
    }
  }

  public CentralBoardSquare getCentralBoardSquare(int position) {
    if (position < 0 || position >= centralBoard.size()) {
      return null;
    }
    return centralBoard.get(position);
  }

  public int getCentralBoardSize() {
    return centralBoard.size();
  }

  public void initPlayers(List<Player> players) {
    Objects.requireNonNull(players);
    for (var player : players) {
      centralBoard.get(0).addTimeToken(player.getTimeToken());
    }
  }

  private int getMaxSquareSize() {
    return centralBoard.stream()
            .mapToInt(square -> square.toString().length())
            .max()
            .orElse(0);
  }

  public boolean gameIsFinished(ArrayList<Player> players, CirclePatches circlePatches) {
    for (var player : players) {
      if (player.getPosition() >= centralBoard.size()) {
        return true;
      }
    }
    return circlePatches.isEmpty();
  }

  private CentralBoardSquare findPositionPlayer(Player player) {
    return centralBoard.stream()
            .filter(square -> square.getTimeToken(player.shortName) != null)
            .findFirst()
            .orElse(null);
  }

  public int moveToken(Player player) {
    var square = findPositionPlayer(player);
    if (square != null) {
      var oldTimeToken = square.getTimeToken(player.shortName);
      var newSquare = centralBoard.get(player.getPosition()).addTimeToken(player.getTimeToken());
      square.removeTimeToken(oldTimeToken);
      centralBoard.set(player.getPosition(), newSquare);
      return oldTimeToken.getPosition();
    }
    return player.getPosition();
  }

  public boolean playerPassedOnButton(Player player, int oldPosition) {
    var newPosition = player.getPosition();
    if (oldPosition < newPosition) {
      for (int i = oldPosition; i < newPosition; i++) {
        if (centralBoard.get(i).hasButton()) {
          player.money += player.earnings;
          return true;
        }
      }
    }
    return false;
  }

  public Patch playerPassedOnPatch(Player player, int oldPosition) {
    var newPosition = player.getPosition();
    if (oldPosition < newPosition) {
      for (int i = oldPosition; i < newPosition; i++) {
        if (centralBoard.get(i).hasLeatherPatch()) {
          return centralBoard.get(i).getLeatherPatch();
        }
      }
    }
    return null;
  }

  public void removeLeatherPatch(Patch patch) {
    centralBoard.stream()
            .filter(square -> square.getLeatherPatch() == patch)
            .findFirst()
            .ifPresent(square -> square.removeLeatherPatch());
  }

  public Player getWinner(ArrayList<Player> players) {
    return players.stream()
            .max((p1, p2) -> p1.getScore() - p2.getScore())
            .orElse(null);
  }

  @Override
  public String toString() {
    int maxSquareSize = getMaxSquareSize();
    if (maxSquareSize % 2 == 0) {
      maxSquareSize++;
    }
    int maxRow = 15;
    var sb = new StringBuilder();
    sb.append(" ");
    for (int i = 0; i < centralBoard.size(); i++) {
      var square = centralBoard.get(i);
      int padding = (maxSquareSize - square.toString().length()) / 2;
      for (int j = 0; j < padding; j++) {
        sb.append(" ");
      }
      sb.append(square);
      for (int j = 0; j < padding; j++) {
        sb.append(" ");
      }
      if (square.toString().length() % 2 == 0) {
        sb.append(" ");
      }
      sb.append(" | ");
      if ((i + 1) % maxRow == 0) {
        sb.append("\n|");
      }
    }
    sb.delete(sb.length() - 3, sb.length());
    return sb.toString();
  }
}
