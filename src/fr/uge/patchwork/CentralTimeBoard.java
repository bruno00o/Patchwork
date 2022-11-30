package fr.uge.patchwork;

import java.util.ArrayList;

public class CentralTimeBoard {
  private final int size;
  private final ArrayList<CentralTimeBoardCase> centralTimeBoard;
  private boolean isBasic;

  public CentralTimeBoard(boolean isBasic) {
    this.size = 57;
    this.centralTimeBoard = new ArrayList<>(size);
    this.isBasic = isBasic;
    for (int i = 0; i < size; i++) {
      centralTimeBoard.add(new CentralTimeBoardCase(null, 0, 0));
    }
  }

  public void load(String path) {
    // TODO
    // Load the file (format 0 0 0 0 0 0 * 0 0 x 0 0 0...) with x a leather patch and * a button
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
    if (actual.getPosition() <= other.getPosition()){
        actual.setMoney(other.getPosition() - actual.getPosition() + 1);
        actual.setPosition(other.getPosition() + 1);
    }
  }


}
