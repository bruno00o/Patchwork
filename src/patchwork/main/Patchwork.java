package patchwork.main;

import patchwork.display.ASCII;
import patchwork.display.GUI;
import patchwork.game.main.Game;

import fr.umlv.zen5.Application;
import fr.umlv.zen5.ApplicationContext;
import fr.umlv.zen5.Event;
import fr.umlv.zen5.ScreenInfo;
import fr.umlv.zen5.Event.Action;

import java.awt.*;
import java.util.Scanner;

public class Patchwork {
  private static final Color BACKGROUND_COLOR = new Color(235, 220, 179);
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
  public static void main(String[] args) {
    var displayString = selectDisplay();
    switch (displayString) {
      case "ASCII" -> {
        var display = new ASCII();
        var game = Game.selectGame(display.askGameMode(Game.getGames()), display.initPlayers(5));
        try {
          game.init();
        } catch (Exception e) {
          e.printStackTrace();
        }
        game.play(display);
      }
      case "GUI" -> {
        Application.run(BACKGROUND_COLOR, context -> {
          var display = new GUI(context);
          var game = Game.selectGame(display.askGameMode(Game.getGames()), display.initPlayers(5));
          try {
            game.init();
          } catch (Exception e) {
            e.printStackTrace();
          }
          game.play(display);
        });
      }
    }
  }
}
