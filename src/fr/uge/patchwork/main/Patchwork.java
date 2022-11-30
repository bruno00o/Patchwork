package fr.uge.patchwork.main;

import fr.uge.patchwork.CentralTimeBoard;
import fr.uge.patchwork.CirclePatches;
import fr.uge.patchwork.Player;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Patchwork {
  private final Player player1;
  private final Player player2;
  private final CirclePatches circlePatches;
  private final CentralTimeBoard centralTimeBoard;

  public Patchwork(String player1Name, String player2Name) {
    this.player1 = new Player(player1Name, 5);
    this.player2 = new Player(player2Name, 5);
    this.circlePatches = new CirclePatches();
    this.centralTimeBoard = new CentralTimeBoard(false);
  }

  public void init() throws IOException {
    circlePatches.load(Path.of("pieces/pieces.txt"));
    circlePatches.shuffle();
    circlePatches.placeNeutralToken();
    centralTimeBoard.load(Path.of("boards/board1.txt"), List.of(player1, player2));
  }

  public Player whoStarts() {
    var reader = new Scanner(System.in);
    System.out.println("Player 1: When was the last time you used a needle? (dd/mm/yyyy)");
    var player1Answer = reader.nextLine();
    System.out.println("Player 2: When was the last time you used a needle? (dd/mm/yyyy)");
    var player2Answer = reader.nextLine();
    var pattern = Pattern.compile("\\d{2}/\\d{2}/\\d{4}");
    if (!pattern.matcher(player1Answer).matches() || !pattern.matcher(player2Answer).matches()) {
      System.out.println("Invalid date format");
      return whoStarts();
    }
    var player1Date = player1Answer.split("/");
    var player2Date = player2Answer.split("/");
    var player1Time = Integer.parseInt(player1Date[0]) + Integer.parseInt(player1Date[1]) * 30 + Integer.parseInt(player1Date[2]) * 365;
    var player2Time = Integer.parseInt(player2Date[0]) + Integer.parseInt(player2Date[1]) * 30 + Integer.parseInt(player2Date[2]) * 365;
    if (player1Time > player2Time) {
      return player1;
    }
    return player2;
  }

  public void start() {
    var player = player1;
    System.out.println("\n" + player.getName() + " starts!\n");
    System.out.println("The board is:");
    System.out.println(centralTimeBoard);
    System.out.println("\nPress a enter to continue...");
    new Scanner(System.in).nextLine();
    do {
      System.out.println(player.getName() + ":");
      System.out.println("Your money: " + player.getMoney());
      System.out.println("Your board:\n");
      System.out.println(player.getBoard());
      System.out.println(circlePatches.displayNextPatches(3));
      if (!player.chooseAction(circlePatches, 3)){
        centralTimeBoard.passedTurn(player, player == player1 ? player2 : player1);
      }
      System.out.println(player.getName() + "'s board after action:");
      System.out.println(player.getBoard());
      System.out.println("Press a enter to continue...");
      new Scanner(System.in).nextLine();
      player = centralTimeBoard.whoPlays(player1, player2);
      System.out.println("\n" + player.getName() + " turn!\n");

    } while (!centralTimeBoard.gameIsOver(player1, player2));
  }

  public static void main(String[] args) throws IOException {
    var patchwork = new Patchwork("Player 1", "Player 2");
    patchwork.init();
    patchwork.start();
  }

}
