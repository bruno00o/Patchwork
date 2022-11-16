package fr.uge.patchwork;

import java.util.Scanner;

public class Player {

  private int money;
  private int pos;
  private Board board;
  private int income;
  private final String name;

  public Player(String name, int money, int pos) {
    this.money = money;
    // initial Position on the Board progress
    this.pos = 0;
    this.income = 0;
    this.name = name;
    board = new Board();
  }

  public String Name() {
    return this.name;
  }

  public int getPos() {
    return pos;
  }

  public int buyPiece(Piece piece, int neutre) {
    var reader = new Scanner(System.in);
    System.out.println("Enter the position of the top left corner of the piece to place on the board");
    System.out.println("Enter x & y positions");
    int x = reader.nextInt();
    int y = reader.nextInt();
    if (money >= piece.price()) {
      var newBoard = board.addPiece(piece, board, x, y);
      if (newBoard != null) {
        board = newBoard;
        this.income += piece.earnings();
        this.money -= piece.price();
        this.pos += piece.blocks();
        return piece.id();
      }
    }
    return neutre;
  }

  public int choosePlays(Player player2, Pieces pieces, int neutre) {
    System.out.println("B - Buy a piece \nAnother key - Pass your turn");
    var reader = new Scanner(System.in);
    var choice = reader.next();
    if (choice.equals("b") || choice.equals("B")) {
      System.out.println("Enter the number of the piece you want to buy");
      int piece = reader.nextInt();
      while (0 >= piece || piece > 3) {
        piece = reader.nextInt();
      }
      neutre = buyPiece(pieces.get(piece), neutre);
    } else {
      System.out.println("You passed your turn\n");
      if (player2.pos >= this.pos) {
        this.money += player2.pos - this.pos + 1;
        this.pos = player2.pos + 1;
      }
    }
    return neutre;
  }

  public int score() {
    return money - board.nbEmpty() * 2;
  }

  @Override
  public String toString() {
    return board + "Money : " + money + "\tPosition : " + pos + "\tIncome : " + income + "\n";
  }

}
