package patchwork.display;

import patchwork.game.*;
import patchwork.game.main.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public record ASCII() implements Display {
  private static final String ANSI_RESET = " \033[H\033[2J";
  private static final String ANSI_BLACK = "\033[0m";
  private static final String ANSI_RED = "\033[31m";
  private static final String ANSI_GREEN = "\033[32m";
  private static final String ANSI_BLUE = "\033[34m";
  private static final String ANSI_CYAN = "\033[36m";
  private static final String ANSI_UNDERLINE = "\033[4m";

  @Override
  public String askGameMode(List<String> games) {
    System.out.println("\n" + ANSI_CYAN + "Select game mode:\n" + ANSI_BLACK);
    for (int i = 0; i < games.size(); i++) {
      System.out.println(i + 1 + ". " + games.get(i));
    }
    System.out.print("\n" + ANSI_GREEN + "Your choice: " + ANSI_BLACK);
    var reader = new Scanner(System.in);
    var choice = reader.nextLine();
    while (!Pattern.matches("[1-" + games.size() + "]", choice)) {
      System.out.print("\n" + ANSI_RED + "Your choice (1-" + games.size() + "): " + ANSI_BLACK);
      choice = reader.nextLine();
    }
    return games.get(Integer.parseInt(choice) - 1);
  }

  private static final void clearScreen() {
    System.out.print(ANSI_RESET);
    System.out.flush();
  }

  private static char shortNameGenerator(String name, ArrayList<Player> players, int i) {
    char shortName = name.charAt(0);
    for (Player player : players) {
      if (player.shortName == shortName) {
        return (char) (i + 49);
      }
    }
    return shortName;
  }

  @Override
  public ArrayList<Player> initPlayers(int money) {
    clearScreen();
    var players = new ArrayList<Player>();
    var reader = new Scanner(System.in);
    System.out.print(ANSI_CYAN + "How many players? " + ANSI_BLACK);
    var nbPlayers = reader.nextLine();
    if (!Pattern.matches("[2-9]", nbPlayers)) {
      System.out.println("Please enter a number between 2 and 9");
      return initPlayers(money);
    }
    System.out.println();
    int nbPlayersInt = Integer.parseInt(nbPlayers);
    for (int i = 0; i < nbPlayersInt; i++) {
      System.out.print("Player " + (i + 1) + " name: ");
      var name = reader.next();
      char shortName = shortNameGenerator(name, players, i);
      name = "Player " + (i + 1) + " (" + name + ")";
      players.add(new Player(name, shortName, money));
    }
    System.out.println();
    for (Player player : players) {
      System.out.println(player.name + " is " + player.shortName);
    }
    return players;
  }

  private static HashMap<String, Player> askPlayerNeedles(ArrayList<Player> players) {
    var reader = new Scanner(System.in);
    var answers = new HashMap<String, Player>();
    var pattern = Pattern.compile("^(0[1-9]|[12]\\d|3[01])\\/(0[1-9]|1[0-2])\\/(19|20)\\d{2}$");
    System.out.println();
    for (var player : players) {
      System.out.print(player.name + ", when was the last time you used a needle? (dd/mm/yyyy): ");
      var answer = reader.nextLine();
      while (!pattern.matcher(answer).matches()) {
        System.out.print("Invalid date format, try again: ");
        answer = reader.nextLine();
      }
      answers.put(answer, player);
    }
    return answers;
  }

  @Override
  public Player whoStarts(ArrayList<Player> players) {
    var answers = askPlayerNeedles(players);
    Player mostRecentPlayer = null;
    var mostRecentDate = 0;
    for (var answer : answers.keySet()) {
      var date = answer.split("/");
      var time = Integer.parseInt(date[0]) + Integer.parseInt(date[1]) * 30 + Integer.parseInt(date[2]) * 365;
      if (time > mostRecentDate) {
        mostRecentDate = time;
        mostRecentPlayer = answers.get(answer);
      }
    }
    System.out.println("\n" + mostRecentPlayer.name + " starts!");
    return mostRecentPlayer;
  }

  @Override
  public void askContinue() {
    var reader = new Scanner(System.in);
    System.out.print("\nPress enter to continue...");
    reader.nextLine();
  }

  @Override
  public void displayBoard(CentralBoard centralBoard) {
    clearScreen(); // works in Powershell
    System.out.println("Central board:");
    System.out.println(centralBoard);
  }

  @Override
  public void displayPlayer(Player player) {
    clearScreen();
    System.out.println(player);
  }

  private static int[] askCoordinates(Player player, Patch patch) {
    Scanner scanner = new Scanner(System.in);
    System.out.println("Enter the coordinates of the piece you want to place (x, y)");
    var pattern = Pattern.compile("\\d+, *\\d+");
    var input = scanner.nextLine();
    if (!pattern.matcher(input).matches()) {
      System.out.println("Invalid coordinates");
      return askCoordinates(player, patch);
    }
    var coordinates = input.split(",");
    int x = Integer.parseInt(coordinates[0].strip());
    int y = Integer.parseInt(coordinates[1].strip());
    if (player.getQuiltBoard().isValidPlacement(patch, x, y)) {
      return new int[]{x, y};
    }
    System.out.println("Invalid coordinates");
    return askCoordinates(player, patch);
  }

  private static Patch askRotation(Patch patch) {
    Scanner scanner = new Scanner(System.in);
    System.out.println("Choose an action for the patch:");
    System.out.println("1. Rotate 90° clockwise");
    System.out.println("2. Flip the patch");
    System.out.println("3. Do nothing");
    var input = scanner.nextLine();
    switch (input) {
      case "1" -> {
        patch = patch.rotate();
        System.out.println("Patch rotated 90° clockwise\n" + patch);
        askRotation(patch);
      }
      case "2" -> {
        patch = patch.flip();
        System.out.println("Patch flipped\n" + patch);
        askRotation(patch);
      }
      case "3" -> {}
      default -> {
        System.out.println("Invalid input");
        askRotation(patch);
      }
    }
    return patch;
  }

  private static String indexLineString(int numberOfPatches) {
    StringBuilder sb = new StringBuilder();
    sb.append("-".repeat(12 * numberOfPatches + 22)).append("\n");
    sb.append("| Index              |").append(" ".repeat(5));
    for (int i = 0; i < numberOfPatches; i++) {
      sb.append(i + 1).append(" ".repeat(5)).append("|").append(" ".repeat(5));
    }
    sb.append("\n");
    sb.append("-".repeat(12 * numberOfPatches + 22));
    return sb.toString();
  }

  private static String priceLineString(CirclePatches circlePatches, int numberOfPatches) {
    StringBuilder sb = new StringBuilder();
    sb.append("| Price              |").append(" ".repeat(5));
    for (var patch : circlePatches.getNextPatches(numberOfPatches)) {
      sb.append(patch.price());
      if (patch.price() < 10) {
        sb.append(" ".repeat(5)).append("|").append(" ".repeat(5));
      } else {
        sb.append(" ".repeat(4)).append("|").append(" ".repeat(5));
      }
    }
    return sb.toString();
  }

  private static String blocksLineString(CirclePatches circlePatches, int numberOfPatches) {
    StringBuilder sb = new StringBuilder();
    sb.append("| Number of blocks   |").append(" ".repeat(5));
    for (var patch : circlePatches.getNextPatches(numberOfPatches)) {
      sb.append(patch.getNumberOfBlocks());
      if (patch.getNumberOfBlocks() < 10) {
        sb.append(" ".repeat(5)).append("|").append(" ".repeat(5));
      } else {
        sb.append(" ".repeat(4)).append("|").append(" ".repeat(5));
      }
    }
    return sb.toString();
  }

  private static String earningsLineString(CirclePatches circlePatches, int numberOfPatches) {
    StringBuilder sb = new StringBuilder();
    sb.append("| Earnings           |").append(" ".repeat(5));
    for (var patch : circlePatches.getNextPatches(numberOfPatches)) {
      sb.append(patch.earnings());
      if (patch.earnings() < 10) {
        sb.append(" ".repeat(5)).append("|").append(" ".repeat(5));
      } else {
        sb.append(" ".repeat(4)).append("|").append(" ".repeat(5));
      }
    }
    return sb.toString();
  }

  private static String formatLineString(CirclePatches circlePatches, int numberOfPatches) {
    StringBuilder sb = new StringBuilder();
    sb.append("-".repeat(12 * numberOfPatches + 22)).append("\n");
    for (int i = 0; i < circlePatches.getMaxHeight(circlePatches.getNextPatches(numberOfPatches)); i++) {
      if (i == 0) {
        sb.append("| Format             |");
      } else {
        sb.append("|                    |");
      }
      for (int j = 0; j < numberOfPatches; j++) {
        var patch = circlePatches.getNextPatches(numberOfPatches).get(j);
        if (i < patch.getHeight()) {
          int spaces = 11 - patch.getFormatLine(i).length();
          int backSpaces = spaces / 2;
          int frontSpaces = spaces - backSpaces;
          sb.append(" ".repeat(frontSpaces)).append(patch.getFormatLine(i)).append(" ".repeat(backSpaces)).append("|");
        } else {
          sb.append(" ".repeat(11)).append("|");
        }
      }
      sb.append("\n");
    }
    sb.append("-".repeat(12 * numberOfPatches + 22));
    return sb.toString();
  }

  private static void printNextPatches(CirclePatches circlePatches, int nbPatches) {
    System.out.println("Next " + nbPatches + " patches:");
    System.out.println(indexLineString(nbPatches));
    System.out.println(priceLineString(circlePatches, nbPatches));
    System.out.println(blocksLineString(circlePatches, nbPatches));
    System.out.println(earningsLineString(circlePatches, nbPatches));
    System.out.println(formatLineString(circlePatches, nbPatches));
  }

  private static void askPlacement(Player player, Patch patch, int x, int y) {
    QuiltBoard copyQuiltBoard = player.getQuiltBoard().copy();
    copyQuiltBoard.addPatch(patch, x, y);
    clearScreen();
    System.out.println("Use z, q, s, d to move the patch, r to rotate, f to flip, v to place the patch. Press enter each time to confirm.");
    System.out.println(copyQuiltBoard);
    Scanner scanner = new Scanner(System.in);
    var input = scanner.nextLine();
    switch (input) {
      case "r" -> {
        patch = patch.rotate();
        askPlacement(player, patch, x, y);
      }
      case "f" -> {
        patch = patch.flip();
        askPlacement(player, patch, x, y);
      }
      case "v" -> {
        if (copyQuiltBoard.isValidPlacement(patch, x, y)) {
          player.placePatch(patch, x, y);
        } else {
          System.out.println("Invalid placement");
          askPlacement(player, patch, x, y);
        }
      }
      case "z" -> {
        if (y > 0) {
          y--;
        }
        askPlacement(player, patch, x, y);
      }
      case "q" -> {
        if (x > 0) {
          x--;
        }
        askPlacement(player, patch, x, y);
      }
      case "s" -> {
        if (y < player.getQuiltBoard().getHeight() - patch.getHeight()) {
          y++;
        }
        askPlacement(player, patch, x, y);
      }
      case "d" -> {
        if (x < player.getQuiltBoard().getWidth() - patch.getWidth()) {
          x++;
        }
        askPlacement(player, patch, x, y);
      }
      default -> {
        System.out.println("Invalid input");
        askPlacement(player, patch, x, y);
      }
    }
  }

  @Override
  public boolean chooseAction(Player player, CirclePatches circlePatches, int nbPatch) {
    var reader = new Scanner(System.in);
    nbPatch = circlePatches.getNbPatches(nbPatch);
    printNextPatches(circlePatches, nbPatch);
    System.out.println("\nChoose an action:");
    System.out.println("1. Buy a patch"); // TODO: faire passer en paramètre la liste des actions disponibles pour le joueur, ainsi on peut vérifier en amont si le joueur peut acheter l'un des trois prochains patchs, s'il ne peut pas autant ne pas afficher l'action
    System.out.println("2. Pass");
    System.out.print(ANSI_GREEN + "Your choice: " + ANSI_BLACK);
    var choice = reader.nextLine();
    switch (choice) {
      case "1" -> {
        if (circlePatches.isEmpty()) {
          System.out.println("No more patches in the circle!");
          return false;
        }
        String result = null;
        do {
          System.out.print("Which patch do you want to buy? (1-" + nbPatch + "): "); // TODO: ajouter une vérification pour savoir si le joueur peut acheter le patch et aussi un moyen d'annuler l'achat (si pas la flemme)
          choice = reader.nextLine();
          result = player.checkPatchChoice(circlePatches, nbPatch, Integer.parseInt(choice));
          if (result != null) {
            System.out.println(result);
          }
        } while (result != null);
        var patch = player.getPatchByChoice(circlePatches, nbPatch, Integer.parseInt(choice));
        player.buyPatch(patch, circlePatches);
//        patch = askRotation(patch);
//        int[] coordinates = askCoordinates(player, patch);
//        player.placePatch(patch, coordinates[0], coordinates[1]);

        askPlacement(player, patch, 0, 0);

        return true;
      }
      case "2" -> {
        System.out.println("You passed");
        return false;
      }
      default -> {
        System.out.println("Invalid choice");
        return chooseAction(player, circlePatches, nbPatch);
      }
    }
  }

  @Override
  public void buttonFound() {
    System.out.println("You found a button!");
  }

  @Override
  public void leatherPatchFound(Player player, Patch leatherPatch) {
    System.out.println("You found a leather patch!");
    System.out.println("You can place it on your board for free");
    var coordinates = askCoordinates(player, leatherPatch);
    player.placePatch(leatherPatch, coordinates[0], coordinates[1]);
  }

  @Override
  public void displayPlayerAfterMove(Player player) {
    clearScreen();
    System.out.println("After move:");
    System.out.println(player);
  }

  @Override
  public void printNextPlayer(Player player) {
    System.out.println("\n" + player.name + "'s turn!");
  }

  @Override
  public void specialTileFound(Player player, SpecialTile specialTile) {
    System.out.println("You found a special tile of " + specialTile.getSize() + "x" + specialTile.getSize() + "!");
  }

  @Override
  public void displayWinner(Player player) {
    clearScreen();
    System.out.println(player.name + " won!");
  }
}
