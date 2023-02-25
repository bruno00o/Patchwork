package fr.uge.patchwork.display;

import fr.uge.patchwork.game.*;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Class for the ASCII display
 */
public record ASCII() implements Display {

  private static final String ANSI_RESET = " \033[H\033[2J";
  private static final String ANSI_BLACK = "\033[0m";
  private static final String ANSI_RED = "\033[31m";
  private static final String ANSI_GREEN = "\033[32m";
  private static final String ANSI_CYAN = "\033[36m";

  /**
   * @param games (List < String >) the list of the games
   * @return (String) Game selection
   */
  @Override
  public String askGameMode(List<String> games) {
    Objects.requireNonNull(games);
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

  /**
   * Clear the screen
   */
  private static void clearScreen() {
    System.out.print(ANSI_RESET);
    System.out.flush();
  }

  /**
   * Ask the players when was the last time they used a needle
   *
   * @param players (List < Player >) the list of the players
   * @return (HashMap < String, Player >) answers of the players
   */
  private static HashMap<String, Player> askPlayerNeedles(ArrayList<Player> players) {
    Objects.requireNonNull(players);
    var reader = new Scanner(System.in);
    var answers = new HashMap<String, Player>();
    var pattern = Pattern.compile("^(0[1-9]|[12]\\d|3[01])/(0[1-9]|1[0-2])/(19|20)\\d{2}$");
    System.out.println();
    for (var player : players) {
      System.out.print(player.getName() + ", when was the last time you used a needle? (dd/mm/yyyy): ");
      var answer = reader.nextLine();
      while (!pattern.matcher(answer).matches()) {
        System.out.print("Invalid date format, try again: ");
        answer = reader.nextLine();
      }
      answers.put(answer, player);
    }
    return answers;
  }

  /**
   * Ask the players last time they used a needle
   *
   * @param players (ArrayList < Player >) the list of the players
   * @return (Player) the player who used the needle last time
   */
  @Override
  public Player whoStarts(ArrayList<Player> players) {
    Objects.requireNonNull(players);
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
    if (mostRecentPlayer == null) {
      throw new IllegalStateException("No player found");
    }
    if (mostRecentPlayer.getName() != null)
      System.out.println("\n" + mostRecentPlayer.getName() + " starts!");
    return mostRecentPlayer;
  }

  /**
   * Wait for the user to press enter
   */
  @Override
  public void askContinue() {
    var reader = new Scanner(System.in);
    System.out.print("\nPress enter to continue...");
    reader.nextLine();
  }

  /**
   * Display the central board
   *
   * @param centralBoard (CentralBoard) the central board
   */
  @Override
  public void displayBoard(CentralBoard centralBoard) {
    Objects.requireNonNull(centralBoard);
    clearScreen();
    System.out.println("Central board:");
    System.out.println(centralBoard);
  }

  /**
   * Display the player
   *
   * @param player (Player) the player
   */
  @Override
  public void displayPlayer(Player player) {
    Objects.requireNonNull(player);
    clearScreen();
    System.out.println(player);
  }

  /**
   * Write the row of index of the table of next patches
   *
   * @param numberOfPatches (int) the number of patches
   * @return (String) the row of index
   */
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

  /**
   * Write the row of prices of the table of next patches
   *
   * @param circlePatches   (CirclePatches) the circle of patches
   * @param numberOfPatches (int) the number of patches
   * @return (String) the row of prices
   */
  private static String priceLineString(CirclePatches circlePatches, int numberOfPatches) {
    Objects.requireNonNull(circlePatches);
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

  /**
   * Write the row of forwards blocks of the table of next patches
   *
   * @param circlePatches   (CirclePatches) the circle of patches
   * @param numberOfPatches (int) the number of patches
   * @return (String) the row of forwards blocks
   */
  private static String blocksLineString(CirclePatches circlePatches, int numberOfPatches) {
    Objects.requireNonNull(circlePatches);
    StringBuilder sb = new StringBuilder();
    sb.append("| Number of blocks   |").append(" ".repeat(5));
    for (var patch : circlePatches.getNextPatches(numberOfPatches)) {
      sb.append(patch.forwardBlocks());
      if (patch.getNumberOfBlocks() < 10) {
        sb.append(" ".repeat(5)).append("|").append(" ".repeat(5));
      } else {
        sb.append(" ".repeat(4)).append("|").append(" ".repeat(5));
      }
    }
    return sb.toString();
  }

  /**
   * Write the row of earnings of the table of next patches
   *
   * @param circlePatches   (CirclePatches) the circle of patches
   * @param numberOfPatches (int) the number of patches
   * @return (String) the row of earnings
   */
  private static String earningsLineString(CirclePatches circlePatches, int numberOfPatches) {
    Objects.requireNonNull(circlePatches);
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

  /**
   * Write the row of formats of the table of next patches
   *
   * @param circlePatches   (CirclePatches) the circle of patches
   * @param numberOfPatches (int) the number of patches
   * @return (String) the row of formats
   */
  private static String formatLineString(CirclePatches circlePatches, int numberOfPatches) {
    Objects.requireNonNull(circlePatches);
    StringBuilder sb = new StringBuilder();
    sb.append("-".repeat(12 * numberOfPatches + 22)).append("\n");
    for (int i = 0; i < CirclePatches.getMaxHeight(circlePatches.getNextPatches(numberOfPatches)); i++) {
      if (i == 0) sb.append("| Format             |");
      else sb.append("|                    |");
      for (int j = 0; j < numberOfPatches; j++) {
        var patch = circlePatches.getNextPatches(numberOfPatches).get(j);
        if (i < patch.getHeight()) {
          int spaces = 11 - patch.getFormatLine(i).length();
          int backSpaces = spaces / 2;
          int frontSpaces = spaces - backSpaces;
          sb.append(" ".repeat(frontSpaces)).append(patch.getFormatLine(i)).append(" ".repeat(backSpaces)).append("|");
        } else sb.append(" ".repeat(11)).append("|");
      }
      sb.append("\n");
    }
    sb.append("-".repeat(12 * numberOfPatches + 22));
    return sb.toString();
  }

  /**
   * Display the next patches
   *
   * @param circlePatches (CirclePatches) the circle of patches
   * @param nbPatches     (int) the number of patches
   */
  private static void printNextPatches(CirclePatches circlePatches, int nbPatches) {
    Objects.requireNonNull(circlePatches);
    System.out.println("Next " + nbPatches + " patches:");
    System.out.println(indexLineString(nbPatches));
    System.out.println(priceLineString(circlePatches, nbPatches));
    System.out.println(blocksLineString(circlePatches, nbPatches));
    System.out.println(earningsLineString(circlePatches, nbPatches));
    System.out.println(formatLineString(circlePatches, nbPatches));
  }

  /**
   * Ask for the placement of a patch
   * The user use z, q, s, d to move the patch, r to rotate, f to flip and v to validate
   *
   * @param player (Player) the player
   * @param patch  (Patch) the patch
   * @param x      (int) the x coordinate of the patch
   * @param y      (int) the y coordinate of the patch
   * @return (Map < Patch, int[] >) the patch and its coordinates
   */
  private static Map<Patch, int[]> askPlacement(Player player, Patch patch, int x, int y) {
    Objects.requireNonNull(player);
    Objects.requireNonNull(patch);
    QuiltBoard copyQuiltBoard = player.getQuiltBoard().copy();
    copyQuiltBoard.addPatch(patch, x, y);
    clearScreen();
    System.out.println("Use z, q, s, d to move the patch, r to rotate, f to flip, v to place the patch. Press enter each time to confirm.");
    System.out.println(copyQuiltBoard);
    Scanner scanner = new Scanner(System.in);
    var input = scanner.nextLine();
    return switch (input) {
      case "r" -> {
        patch = patch.rotate();
        yield askPlacement(player, patch, x, y);
      }
      case "f" -> {
        patch = patch.flip();
        yield askPlacement(player, patch, x, y);
      }
      case "v" -> {
        if (player.getQuiltBoard().isValidPlacement(patch, x, y))
          yield Map.of(patch, new int[]{x, y});
        else {
          System.out.println("Invalid placement");
          yield askPlacement(player, patch, x, y);
        }
      }
      case "z" -> {
        if (y > 0) y--;
        yield askPlacement(player, patch, x, y);
      }
      case "q" -> {
        if (x > 0) x--;
        yield askPlacement(player, patch, x, y);
      }
      case "s" -> {
        if (y < player.getQuiltBoard().getHeight() - patch.getHeight()) y++;
        yield askPlacement(player, patch, x, y);
      }
      case "d" -> {
        if (x < player.getQuiltBoard().getWidth() - patch.getWidth()) x++;
        yield askPlacement(player, patch, x, y);
      }
      default -> {
        System.out.println("Invalid input");
        yield askPlacement(player, patch, x, y);
      }
    };
  }

  /**
   * Display when the player choose to buy a patch
   *
   * @param p      (Player) the player
   * @param cp     (CirclePatches) the circle of patches
   * @param nb     (int) the number of patches
   * @param opt    (List<String>) the options
   * @param rd     (Scanner) the scanner
   * @param choice (String) the choice of the player
   * @return (Map < Patch, int[] >) the patch and its coordinates
   */
  private Optional<Map<Patch, int[]>> buyActionChosen(Player p, CirclePatches cp, int nb, List<String> opt, Scanner rd, String choice) {
    Objects.requireNonNull(p);
    Objects.requireNonNull(cp);
    Objects.requireNonNull(opt);
    Objects.requireNonNull(rd);
    Objects.requireNonNull(choice);
    if (opt.size() == 1) {
      System.out.println("You pass because you don't have enough money");
      return Optional.empty();
    }
    if (cp.isEmpty()) {
      System.out.println("No more patches in the circle!");
      return Optional.empty();
    }
    String result;
    do {
      System.out.print("Which patch do you want to buy? (1-" + nb + "): ");
      choice = rd.nextLine();
      result = p.checkPatchChoice(cp, nb, Integer.parseInt(choice));
      if (result != null) {
        System.out.println(result);
      }
    } while (result != null);
    var patch = p.getPatchByChoice(cp, nb, Integer.parseInt(choice));
    System.out.println("forward: " + patch.forwardBlocks());
    int[] coords = p.getQuiltBoard().getFirstValidPosition(patch);
    var placement = askPlacement(p, patch, coords[0], coords[1]);
    return Optional.of(placement);
  }

  /**
   * Ask the player to choose an action based on a list of options
   *
   * @param player        (Player) the player
   * @param circlePatches (CirclePatches) the circle of patches
   * @param nbPatch       (int) the number of patches to display
   * @param options       (List < String >) the list of options
   * @return (Optional < Map < Patch, int[] > >) the action chosen by the player
   * (empty if the player passed)
   * (the patch and the position chosen by the player if the player bought a patch)
   */
  @Override
  public Optional<Map<Patch, int[]>> chooseAction(Player player, CirclePatches circlePatches, int nbPatch, List<String> options) {
    Objects.requireNonNull(player);
    Objects.requireNonNull(circlePatches);
    Objects.requireNonNull(options);
    var reader = new Scanner(System.in);
    nbPatch = circlePatches.getNbPatches(nbPatch);
    printNextPatches(circlePatches, nbPatch);
    System.out.println("\nChoose an action:");
    for (String option : options) {
      System.out.println(option);
    }
    System.out.print(ANSI_GREEN + "Your choice: " + ANSI_BLACK);
    var choice = reader.nextLine();
    switch (choice) {
      case "1" -> {
        return buyActionChosen(player, circlePatches, nbPatch, options, reader, choice);
      }
      case "2" -> {
        System.out.println("You passed");
        return Optional.empty();
      }
      default -> {
        System.out.println("Invalid choice");
        return chooseAction(player, circlePatches, nbPatch, options);
      }
    }
  }

  /**
   * Display the button found message
   */
  @Override
  public void buttonFound() {
    System.out.println("You found a button!");
  }

  /**
   * Display the leather patch found message
   *
   * @param player       (Player) the player
   * @param leatherPatch (LeatherPatch) the leather patch
   * @return (Map < Patch, int[] >) the patch and its coordinates
   */
  @Override
  public Map<Patch, int[]> leatherPatchFound(Player player, Patch leatherPatch) {
    Objects.requireNonNull(player);
    Objects.requireNonNull(leatherPatch);
    System.out.println("You found a leather patch!");
    System.out.println("You can place it on your board for free");
    int[] coords = player.getQuiltBoard().getFirstValidPosition(leatherPatch);
    return askPlacement(player, leatherPatch, coords[0], coords[1]);
  }

  /**
   * Display the player after a move
   *
   * @param player (Player) the player
   */
  @Override
  public void displayPlayerAfterMove(Player player) {
    clearScreen();
    System.out.println("After move:");
    System.out.println(player);
  }

  /**
   * Display the next player message
   *
   * @param player (Player) the player
   */
  @Override
  public void printNextPlayer(Player player) {
    System.out.println("\n" + player.getName() + "'s turn!");
  }

  /**
   * Display the special tile message
   *
   * @param player      (Player) the player
   * @param specialTile (SpecialTile) the special tile
   */
  @Override
  public void specialTileFound(Player player, SpecialTile specialTile) {
    System.out.println("You found a special tile of " + specialTile.getSize() + "x" + specialTile.getSize() + "!");
  }

  /**
   * Display the winner message
   *
   * @param player (Player) the player
   */
  @Override
  public void displayWinner(Player player) {
    clearScreen();
    System.out.println(player.getName() + " won!");
  }
}
