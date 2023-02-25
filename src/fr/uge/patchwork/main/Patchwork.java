package fr.uge.patchwork.main;

import fr.umlv.zen5.Application;
import fr.uge.patchwork.display.ASCII;
import fr.uge.patchwork.display.Display;
import fr.uge.patchwork.display.GUI;
import fr.uge.patchwork.game.Player;
import fr.uge.patchwork.game.main.Game;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Main class of the game.
 */
public class Patchwork {

  private static final int MONEY = 5;
  private static final Color BACKGROUND_COLOR = new Color(235, 220, 179);

  /**
   * Switch between the ASCII and GUI mode.
   *
   * @return (String) the mode chosen by the user.
   */
  public static String selectDisplay() {
    System.out.println("Select display:");
    System.out.println("1. ASCII");
    System.out.println("2. GUI");
    System.out.print("Your choice: ");
    var reader = new Scanner(System.in);
    var choice = reader.nextLine();
    return switch (choice) {
      case "1" -> "ASCII";
      case "2" -> "GUI";
      default -> selectDisplay();
    };
  }

  /**
   * Init the two players.
   *
   * @return (ArrayList < Player >) the list of the players.
   */
  private static ArrayList<Player> initPlayers() {
    ArrayList<Player> players = new ArrayList<>();
    for (int i = 0; i < 2; i++) {
      players.add(new Player("Player " + (i + 1), (char) ('1' + i), MONEY));
    }
    return players;
  }

  /**
   * Start the game.
   *
   * @param display (Display) the display chosen by the user.
   */
  private static void startGame(Display display) {
    var game = Game.selectGame(display.askGameMode(Game.getGames()), initPlayers());
    try {
      game.init();
    } catch (Exception e) {
      e.printStackTrace();
    }
    game.play(display);
  }

  /**
   * Main method of the game.
   *
   * @param args (String[]) the arguments.
   */
  public static void main(String[] args) {
    var displayString = selectDisplay();
    switch (displayString) {
      case "ASCII" -> {
        var display = new ASCII();
        startGame(display);
      }
      case "GUI" -> Application.run(BACKGROUND_COLOR, context -> {
        var display = new GUI(context);
        startGame(display);
      });
    }
  }
}
