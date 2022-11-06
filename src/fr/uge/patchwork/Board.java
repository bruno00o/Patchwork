package fr.uge.patchwork;

public class Board {

  private final int[][] board;

  public Board() {
    board = new int[9][9];
    for (int i = 0; i < 9; i++) {
      for (int j = 0; j < 9; j++) {
        board[i][j] = 0;
      }
    }
  }

  private int check(int x, int y, Piece piece) {
    var pieceBoard = piece.getFormat().split(",");
    for (int i = 0; i < pieceBoard.length; i++) {
      if (x < 0 || x + pieceBoard[i].length() > 9 || y < 0 || y + pieceBoard.length > 9) {
        return 0;
      }
    }
    return 1;
  }

  public Board addPiece(Piece piece, Board board, int x, int y) {
    var pieceBoard = piece.getFormat().split(",");
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
