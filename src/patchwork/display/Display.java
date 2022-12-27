package patchwork.display;

import patchwork.game.*;
import patchwork.game.main.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public sealed interface Display permits ASCII, GUI {
  String askGameMode(List<String> games);
  ArrayList<Player> initPlayers(int money);
  Player whoStarts(ArrayList<Player> players);
  void displayBoard(CentralBoard centralBoard);

  void askContinue();

  void displayPlayer(Player player);

  boolean chooseAction(Player player, CirclePatches circlePatches, int nbPatch);

  void buttonFound();

  void leatherPatchFound(Player player, Patch leatherPatch);

  void displayPlayerAfterMove(Player player);

  void printNextPlayer(Player player);

  void specialTileFound(Player player, SpecialTile specialTile);

  void displayWinner(Player player);

  static Display selectDisplay() {
    System.out.println("Select display:");
    System.out.println("1. ASCII");
    System.out.println("2. GUI");
    System.out.print("Your choice: ");
    var reader = new Scanner(System.in);
    var choice = reader.nextLine();
    return switch (choice) {
      case "1" -> new ASCII();
//      case "2" -> new GUI();
      default -> selectDisplay();
    };
  }
}
