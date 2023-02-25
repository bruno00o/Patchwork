package fr.uge.patchwork.game;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Class for the central board
 */
public class CentralBoard {
  /**
   * Representation of the central board
   */
  private final ArrayList<CentralBoardSquare> centralBoard;

  /**
   * Init the central board
   */
  public CentralBoard() {
    this.centralBoard = new ArrayList<>();
  }

  /**
   * Load the central board from a file
   * FORMAT:
   * - . represents an empty square
   * - * represents a square with a leather patch
   * - x represents a square with a button
   *
   * @param path (Path) path to the file
   * @throws IOException if the file cannot be read
   */
  public void load(Path path) throws IOException {
    Objects.requireNonNull(path);
    try (var reader = Files.newBufferedReader(path)) {
      String line;
      while ((line = reader.readLine()) != null) {
        var tokens = line.split("");
        for (var token : tokens) {
          switch (token) {
            case "." -> centralBoard.add(new CentralBoardSquare(null, null));
            case "*" -> centralBoard.add(new CentralBoardSquare(new Patch(0, "*", 0, 0, 0, 0, false, -1, -1, 0), null));
            case "x" -> centralBoard.add(new CentralBoardSquare(null, new Button(1)));
            default -> throw new IOException("Invalid character in central board file");
          }
        }
      }
    }
  }

  /**
   * Get the central board square at a given index
   *
   * @param position (int) index of the square
   * @return (CentralBoardSquare) the square at the given index
   */
  public CentralBoardSquare getCentralBoardSquare(int position) {
    if (position < 0 || position >= centralBoard.size()) {
      return null;
    }
    return centralBoard.get(position);
  }

  /**
   * Get the central board size
   *
   * @return (int) the central board size
   */
  public int getCentralBoardSize() {
    return centralBoard.size();
  }

  /**
   * Initialize the players on the central board
   *
   * @param players (List<Player>) list of players
   */
  public void initPlayers(List<Player> players) {
    Objects.requireNonNull(players);
    for (var player : players) {
      centralBoard.get(0).addTimeToken(player.getTimeToken());
    }
  }

  /**
   * Return true if the game is over
   * The game is over if a player reaches the end of the central board or there are no more patches on the circle
   *
   * @param players       (List<Player>) list of players
   * @param circlePatches (CirclePatches) circle patches
   * @return (boolean) true if the game is over
   */
  public boolean gameIsFinished(ArrayList<Player> players, CirclePatches circlePatches) {
    Objects.requireNonNull(players);
    Objects.requireNonNull(circlePatches);
    if (players.stream().allMatch(player -> player.getPosition() == centralBoard.size() - 1)) {
      return true;
    }
    return circlePatches.isEmpty();
  }

  /**
   * Find the square where the player is
   *
   * @param player (Player) the player
   * @return (CentralBoardSquare) the square where the player is
   */
  private CentralBoardSquare findPositionPlayer(Player player) {
    Objects.requireNonNull(player);
    return centralBoard.stream()
            .filter(square -> square.getTimeToken(player.getShortName()) != null)
            .findFirst()
            .orElse(null);
  }

  /**
   * Move the player on the central board
   *
   * @param player (Player) the player
   * @return (int) the new position of the player
   */
  public int moveToken(Player player) {
    Objects.requireNonNull(player);
    var square = findPositionPlayer(player);
    if (square != null) {
      var oldTimeToken = square.getTimeToken(player.getShortName());
      var newSquare = centralBoard.get(player.getPosition()).addTimeToken(player.getTimeToken());
      square.removeTimeToken(oldTimeToken);
      centralBoard.set(player.getPosition(), newSquare);
      return oldTimeToken.position();
    }
    return player.getPosition();
  }

  /**
   * Return true if the player passed over a button (or is on a button)
   *
   * @param player      (Player) the player
   * @param oldPosition (int) the old position of the player
   * @return (boolean)
   */
  public boolean playerPassedOnButton(Player player, int oldPosition) {
    Objects.requireNonNull(player);
    var newPosition = player.getPosition();
    if (oldPosition < newPosition) {
      for (int i = oldPosition; i <= newPosition; i++) {
        if (centralBoard.get(i).hasButton()) {
          player.addMoney(player.getEarnings());
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Return true if the player passed over a leather patch (or is on a patch)
   *
   * @param player      (Player) the player
   * @param oldPosition (int) the old position of the player
   * @return (boolean)
   */
  public Patch playerPassedOnPatch(Player player, int oldPosition) {
    Objects.requireNonNull(player);
    var newPosition = player.getPosition();
    if (oldPosition < newPosition) {
      for (int i = oldPosition; i <= newPosition; i++) {
        if (centralBoard.get(i).hasLeatherPatch()) {
          return centralBoard.get(i).getLeatherPatch();
        }
      }
    }
    return null;
  }

  /**
   * Remove the leather patch from the central board
   *
   * @param patch (Patch) the patch to remove
   */
  public void removeLeatherPatch(Patch patch) {
    Objects.requireNonNull(patch);
    centralBoard.stream()
            .filter(square -> square.getLeatherPatch() == patch)
            .findFirst()
            .ifPresent(CentralBoardSquare::removeLeatherPatch);
  }

  /**
   * Return the winner of the game
   *
   * @param players (List<Player>) list of players
   * @return (Player) the winner of the game
   */
  public Player getWinner(ArrayList<Player> players) {
    Objects.requireNonNull(players);
    return players.stream()
            .max(Comparator.comparingInt(Player::getScore))
            .orElse(null);
  }

  /**
   * Return the max square size (for display purpose)
   *
   * @return (int) the max square size
   */
  private int getMaxSquareSize() {
    return centralBoard.stream()
            .mapToInt(square -> square.toString().length())
            .max()
            .orElse(0);
  }

  /**
   * Return the string representation of the central board
   * @return (String)
   */
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
      sb.append(" ".repeat(Math.max(0, padding)));
      sb.append(square);
      sb.append(" ".repeat(Math.max(0, padding)));
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
