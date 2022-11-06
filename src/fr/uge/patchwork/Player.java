package fr.uge.patchwork;

import java.util.Scanner;

public class Player {

  private int money;
  private int pos;
  private Board board;
  private int income;

  public Player(int money, int pos) {
    this.money = money;
    // initial Position on the Board progress
    this.pos = 0;
    this.income = 0;
    board = new Board();
  }

  public int getMoney() {
    return money;
  }

  public Board getBoard() {
    return board;
  }

  public int buyPiece(Piece piece, int neutre) {
    var reader = new Scanner(System.in);
    System.out.println("Enter the position of the top left corner of the piece to place on the board");
    System.out.println("Enter x & y positions");
    int x = reader.nextInt();
    int y = reader.nextInt();
    if (money >= piece.getPrice()) {
      var newBoard = board.addPiece(piece, board, x, y);
      if (newBoard != null) {
        board = newBoard;
        this.income += piece.getEarnings();
        this.money -= piece.getPrice();
        this.pos += piece.blocks();
        return piece.id();
      }
    }
    return neutre;
  }

  public int choosePlays(Player player2, Pieces pieces, int neutre) {
    System.out.println("List of 3 next pieces : \n" + pieces.nextThree(neutre));
    System.out.println("B - Buy a piece \nAnother key - Pass your turn");
    var reader = new Scanner(System.in);
    var choice = reader.next();
    if (choice.equals("b") || choice.equals("B")) {
      System.out.println("Enter the number of the piece you want to buy");
      int piece = reader.nextInt();
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

  @Override
  public String toString() {
    return board + "Money : " + money + "\tPosition : " + pos + "\tIncome : " + income + "\n";
  }

}
