package fr.uge.patchwork;

public class Game {

  private final Player player1;
  private final Player player2;
  private final Pieces pieces;
  private int neutral;

  public Game() {
    player1 = new Player(5, 0);
    player2 = new Player(5, 0);
    pieces = new Pieces();
    pieces.load();
    neutral = 0;
  }

  public Player whoPlay(Player lastPlayer) {
    if(player1.getPos() < 57){
      if (player2.getPos() < 57){
        if (player1.getPos() > player2.getPos()){
          return player2;
        } else if (player1.getPos() < player2.getPos()){
          return player1;
        }
      } else {
        return player1;
      }
    } else {
      if (player2.getPos() < 57){
        return player2;
      }
    }
    return lastPlayer;
  }

  public Player otherPlayer(Player player) {
    if (player.equals(player1)){
      return player2;
    } else {
      return player1;
    }
  }

  public void verifyNeutre(Player lastPlayer, Player otherPlayer) {
    var tempNeutre = -1;
    tempNeutre = lastPlayer.choosePlays(otherPlayer, pieces, neutre);
    if (tempNeutre != neutre) {
      System.out.println(pieces.get(tempNeutre));
      pieces.deletePiece(tempNeutre);
      neutre = tempNeutre;
    }
  }
  public void start() {
    Player lastPlayer = player1;
    Player otherPlayer = player2;
    while (player1.getPos() < 57 && player2.getPos() < 57) {
      lastPlayer = whoPlay(lastPlayer);
      System.out.println("\nList of 3 next pieces : \n" + pieces.nextThree(neutre));
      System.out.println("\nTurn for " + lastPlayer.Name() + " : \n" + lastPlayer);
      otherPlayer = otherPlayer(lastPlayer);
      verifyNeutre(lastPlayer, otherPlayer);
      System.out.println("\n" + lastPlayer.Name() + "\n" + lastPlayer + "\nLe pion Neutre est en position " + neutre);
    }
  }





}
