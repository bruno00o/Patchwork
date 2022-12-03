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

  public QuiltBoard() {
    this.size = 9;
    this.board = new int[size][size];
    Arrays.stream(this.board).forEach(row -> Arrays.fill(row, 0));
  }

  public int[][] getBoard() {
    return board;
  }

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
        if (row.charAt(i) == '*' && board[x][y + i] != 0) {
          return false;
        }
      }
      x++;
    }
    return true;
  }

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
