package fr.uge.patchwork;

public class Game {

  private final Player player1;
  private final Player player2;
  private final Pieces pieces;
  private int neutre;

  public Game() {
    player1 = new Player(5, 0);
    player2 = new Player(5, 0);
    pieces = new Pieces();
    pieces.load();
    neutre = 0;
  }
  public void start() {
    System.out.println("Player 1 : \n" + player1);
    //System.out.println("Player 2 : \n" + player2.getBoard());
    neutre = player1.choosePlays(player2, pieces, neutre);
    System.out.println("\nPlayer 1 : \n" + player1);
    System.out.println("Neutre " + neutre);
  }





}
