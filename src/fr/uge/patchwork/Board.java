package fr.uge.patchwork;

public class Board {

  private final int[][] board;
  private final int size;

  public Board() {
    size = 9;
    board = new int[size][size];
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        board[i][j] = 0;
      }
    }
  }

  private int check(int x, int y, Piece piece) {
    var pieceBoard = piece.format().split(",");
    for (int i = 0; i < pieceBoard.length; i++) {
      if (x < 0 || x + pieceBoard[i].length() > size || y < 0 || y + pieceBoard.length > size) {
        return 0;
      }
    }
    return 1;
  }

  public Board addPiece(Piece piece, Board board, int x, int y) {
    var pieceBoard = piece.format().split(",");
    if (check(x, y, piece) == 1) {
      for (int j = 0; j < pieceBoard.length; j++) {
        for (int k = 0; k < pieceBoard[j].length(); k++) {
          if (pieceBoard[j].charAt(k) == '*') {
            board.board[x + j][y + k] = 1;
          }
        }
      }
      return board;
    }
    return null;
  }

  public int nbEmpty() {
    int nb = 0;
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        if (board[i][j] == 0) {
          nb++;
        }
      }
    }
    return nb;
  }


  @Override
  public String toString() {
    var i = 0;
    var sb = new StringBuilder();
    sb.append(" \\  0 1 2 3 4 5 6 7 8\n");
    sb.append("  \\ _ _ _ _ _ _ _ _ _\n");
    for (var line : board) {
      sb.append(i).append(" | ");
      for (var c : line) {
        sb.append(c).append(" ");
      }
      sb.append("\n");
      i++;
    }
    return sb.toString();
  }

}
