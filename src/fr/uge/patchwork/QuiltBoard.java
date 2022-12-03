package fr.uge.patchwork;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * QuiltBoard class, the player board.
 *
 * @author Seilliebert Bruno & Oeuvrard Dilien
 */
public class QuiltBoard {
  private final int[][] board;
  private final int size;

  /**
   * Init QuiltBoard class, the player board.
   */
  public QuiltBoard() {
    this.size = 9;
    this.board = new int[size][size];
    Arrays.stream(this.board).forEach(row -> Arrays.fill(row, 0));
  }

  /**
   * Check if the board is full, if the player can place a new patch.
   * @param patch (Patch) the patch to place.
   * @return (boolean) true if the player can place the patch, false otherwise.
   */
  public boolean possibleToBuy(Patch patch) {
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        if (board[i][j] == 0) {
          if (check(patch, i, j)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * Check if you can place a piece on the board.
   *
   * @param patch (Patch) the patch to place.
   * @param x     (int) the x position.
   * @param y     (int) the y position.
   * @return (boolean) true if you can place the patch.
   */
  private boolean check(Patch patch, int x, int y) {
    var patchBoard = patch.format().split(",");
    int j = x;
    var height = patch.getHeight();
    for (var row : patchBoard) {
      // get length of row without character '.'
      var length = row.replace(".", "").length();
      if (x < 0 || x + height > size || y < 0 || y + length > size) {
        return false;
      }
      // check if not overlap
      for (int i = 0; i < row.length(); i++) {
        if (row.charAt(i) == '*' && board[j][y + i] != 0) {
          return false;
        }
      }
      j++;
    }
    return true;
  }

  /**
   * Add a patch to the board.
   *
   * @param patch (Patch) the patch to add.
   * @param x     (int) the x position.
   * @param y     (int) the y position.
   * @return (boolean) true if the patch is added.
   */
  public boolean addPatch(Patch patch, int x, int y) {
    var patchBoard = patch.format().split(",");
    if (!check(patch, x, y)) return false;
    for (int j = 0; j < patchBoard.length; j++) {
      for (int k = 0; k < patchBoard[j].length(); k++) {
        if (patchBoard[j].charAt(k) == '*') {
          board[x + j][y + k] = 1;
        }
      }
    }
    return true;
  }

  /**
   * check if the board(9x9) contains squareSize x squareSize square of 1
   *
   * @param squareSize (int) the size of the square
   * @return (boolean) true if the board contains squareSize x squareSize square of 1
   */
  public boolean containsSquare(int squareSize) {
    return Stream.of(board)
            .flatMapToInt(Arrays::stream)
            .filter(i -> i == 1)
            .count() == squareSize * squareSize;
  }

  /**
   * get the number of case empty on the board.
   *
   * @return (int) the number of case empty on the board.
   */
  public int nbEmptyCases() {
    return (int) Arrays.stream(board).flatMapToInt(Arrays::stream).filter(i -> i == 0).count();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(" \\  0 1 2 3 4 5 6 7 8 y\n");
    sb.append("  \\ _ _ _ _ _ _ _ _ _\n");
    for (int i = 0; i < size; i++) {
      sb.append(i).append(" | ");
      for (int j = 0; j < size; j++) {
        sb.append(board[i][j] == 1 ? "*" : ".").append(" ");
      }
      sb.append("\n");
    }
    sb.append("x\n");
    return sb.toString();
  }
}
