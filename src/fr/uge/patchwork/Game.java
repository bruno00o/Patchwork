package fr.uge.patchwork;

public class Game {

  private final Player player1;
  private final Player player2;
  private final Pieces pieces;
  private int neutral;

  public Game() {
    player1 = new Player("Player 1", 5, 0);
    player2 = new Player("Player 2", 5, 0);
    pieces = new Pieces();
    pieces.load();
    neutral = 0;
  }

  /**
   * Determine which player plays
   *
   * @param lastPlayer
   * @return
   */
  public Player whoPlay(Player lastPlayer) {
    if (player1.getPos() < 57) {
      if (player2.getPos() < 57) {
        if (player1.getPos() > player2.getPos()) {
          return player2;
        } else if (player1.getPos() < player2.getPos()) {
          return player1;
        }
      } else {
        return player1;
      }
    } else {
      if (player2.getPos() < 57) {
        return player2;
      }
    }
    return lastPlayer;
  }

  /**
   * Return the other player
   *
   * @param player
   * @return
   */
  public Player otherPlayer(Player player) {
    if (player.equals(player1)) {
      return player2;
    } else {
      return player1;
    }
  }

  /**
   * Check neutral piece
   * @param lastPlayer
   * @param otherPlayer
   */
  public void verifyNeutral(Player lastPlayer, Player otherPlayer) {
    var tmpNeutral = -1;
    tmpNeutral = lastPlayer.choosePlays(otherPlayer, pieces, neutral);
    if (tmpNeutral != neutral) {
      System.out.println(pieces.get(tmpNeutral));
      pieces.deletePiece(tmpNeutral);
      neutral = tmpNeutral;
    }
  }

  public void start() {
    Player lastPlayer = player1;
    Player otherPlayer = player2;
    while (player1.getPos() < 57 && player2.getPos() < 57) {
      lastPlayer = whoPlay(lastPlayer);
      System.out.println("\nList of 3 next pieces : \n" + pieces.nextThree(neutral));
      System.out.println("\nTurn for " + lastPlayer.Name() + " : \n" + lastPlayer);
      otherPlayer = otherPlayer(lastPlayer);
      verifyNeutral(lastPlayer, otherPlayer);
      System.out.println("\n" + lastPlayer.Name() + "\n" + lastPlayer + "\nLe pion Neutre est en position " + neutral);
    }
  }


}
