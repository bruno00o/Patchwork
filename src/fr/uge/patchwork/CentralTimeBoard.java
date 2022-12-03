package fr.uge.patchwork;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * The central time board of the game.
 * Use this class to init the game.
 * To finish the game, reach the end of the time board.
 *
 * @author Seilliebert Bruno & Oeuvrard Dilien
 */
public class CentralTimeBoard {
  private final int size;
  private final ArrayList<CentralTimeBoardCase> centralTimeBoard;
  private final boolean isBasic;

  /**
   * Init the central time board.
   *
   * @param isBasic (boolean) true if the game is in basic mode, false if it's in advanced mode.
   */
  public CentralTimeBoard(boolean isBasic) {
    this.size = 59;
    this.centralTimeBoard = new ArrayList<>(size);
    this.isBasic = isBasic;
    for (int i = 0; i < size; i++) {
      centralTimeBoard.add(new CentralTimeBoardCase(null, null, 0));
    }
  }

  /**
   * load the central time board from a file
   * with * for leather patch
   * and x for button gift
   *
   * @param path    (Path) the path of the file
   * @param players (List<Player>) the list of players
   * @throws IOException if the file is not found
   */
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

  /**
   * Show the score for each player at the end of the game.
   * And who is the winner.
   *
   * @return true if the winner is player 1
   */
  public boolean theWinnerIs(Player player1, Player player2) {
    if (player1.getSpecialTile() != null && player2.getSpecialTile() != null) {
      var playerGetSpecial = player1.hasSpecialTile() ? player1 : player2;
      var specialTile = playerGetSpecial.getSpecialTile();
      playerGetSpecial.addMoney(specialTile.getEarnings());
    }
    System.out.println("Player 1: " + player1.getScore() + "\nPlayer 2: " + player2.getScore() + "\n");
    System.out.println("The winner is: " + (player1.getScore() > player2.getScore() ? "Player 1" : "Player 2"));
    return player1.getScore() > player2.getScore();
  }

  /**
   * Call to know if the game is finish.
   *
   * @param player1       (Player) the first player.
   * @param player2       (Player) the second player.
   * @param circlePatches (List<Patch>) the list of the circle patches.
   * @return true if game is finish.
   */
  public boolean gameIsOver(Player player1, Player player2, CirclePatches circlePatches) {
    if (player1.getPosition() >= size && player2.getPosition() >= size) {
      return theWinnerIs(player1, player2);
    }
    if (circlePatches.isEmpty()) {
      return theWinnerIs(player1, player2);
    }
    return false;
  }

  /**
   * To know who it is to play
   *
   * @param player1 (Player) the first player.
   * @param player2 (Player) the second player.
   * @return the player who it is to play
   */
  public Player whoPlays(Player player1, Player player2) {
    if (player1.getTimeToken().position() < player2.getTimeToken().position()) {
      return player1;
    }
    return player2;
  }

  /**
   * When you passed your turn
   *
   * @param actual (Player) the player who passed his turn
   * @param other  (Player) the other player
   */
  public void passedTurn(Player actual, Player other) {
    var to = other.getPosition() >= size ? 0 : 1;
    if (actual.getPosition() <= other.getPosition()) {
      actual.addMoney(other.getPosition() - actual.getPosition() + to);
      actual.setPosition(other.getPosition() + to, actual.getPosition());
      action(actual);
    }
  }

  /**
   * When you want to move your token.
   *
   * @param player (Player) the player who want to move his token.
   */
  public void moveTimeToken(Player player) {
    var timeToken = player.getTimeToken();
    var position = timeToken.position();
    var oldPosition = timeToken.oldPosition();
    var caseOfTimeToken = centralTimeBoard.get(oldPosition);
    centralTimeBoard.set(oldPosition, caseOfTimeToken.removeTimeToken(timeToken));
    if (position > size) {
      timeToken = timeToken.setPosition(size - 1);
      position = size - 1;
    }
    var newCase = centralTimeBoard.get(position);
    centralTimeBoard.set(position, newCase.addTimeToken(timeToken));
    action(player);
  }

  /**
   * When you found a special item on the central time board.
   * You can found a patch or a button gift
   *
   * @param player               (Player) the player who found the item.
   */
  private void action(Player player) {
    var timeToken = player.getTimeToken();
    for (int i = timeToken.oldPosition(); i < timeToken.position(); i++) {
      var caseOfTimeToken = centralTimeBoard.get(i);
      if (caseOfTimeToken.hasLeatherPatch()) {
        System.out.println("You have found a leather patch");
        var patch = caseOfTimeToken.getLeatherPatch();
        int[] coordinates = Player.askCoordinates();
        player.placePatch(patch, coordinates);
        caseOfTimeToken.removeLeatherPatch();
      }
      if (caseOfTimeToken.hasButton()) {
        System.out.println("You have found a button");
        player.addMoney(player.getEarnings());
      }
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
