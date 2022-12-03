package fr.uge.patchwork;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Player class represents a player in the game.
 *
 * @author Seilliebert Bruno & Oeuvrard Dilien
 */
public class Player {
  private final String name;
  private final QuiltBoard quiltBoard;
  private final char shortName;
  private TimeToken timeToken;
  private int earnings;
  private int money;
  private final SpecialTile specialTile;

  /**
   * Init a player
   *
   * @param name        (String) name of the player
   * @param money       (int) money of the player
   * @param specialTile (SpecialTile) special tile of the player
   */
  public Player(String name, int money, SpecialTile specialTile) {
    Objects.requireNonNull(name);
    if (money < 0) {
      throw new IllegalArgumentException("Money must be positive");
    }
    this.name = name;
    this.shortName = name.charAt(name.length() - 1);
    this.quiltBoard = new QuiltBoard();
    this.timeToken = new TimeToken(0, 0, shortName);
    this.earnings = 0;
    this.money = money;
    this.specialTile = specialTile;
  }

  /**
   * Get the player's money
   *
   * @return (int) money of the player
   */
  public int getMoney() {
    return money;
  }

  /**
   * add money to the player
   *
   * @param money (int) money to add
   */
  public void addMoney(int money) {
    this.money += money;
  }

  /**
   * get the player's position
   *
   * @return (int) position of the player
   */
  public int getPosition() {
    return timeToken.position();
  }

  /**
   * Set the player's position
   *
   * @param position    (int) position to set to the player
   * @param oldPosition (int) old position of the player
   */
  public void setPosition(int position, int oldPosition) {
    timeToken = new TimeToken(position, oldPosition, shortName);
  }

  /**
   * get the player's earnings
   *
   * @return (int) earnings of the player
   */
  public int getEarnings() {
    return earnings;
  }

  /**
   * Ask coordinates to the player (0,8) for x, y.
   *
   * @return (int[]) coordinates of the player
   */
  public static int[] askCoordinates() {
    Scanner scanner = new Scanner(System.in);
    System.out.println("Enter the coordinates of the piece you want to place (x, y)");
    var pattern = Pattern.compile("\\d+, *\\d+");
    var input = scanner.nextLine();
    if (!pattern.matcher(input).matches()) {
      System.out.println("Invalid coordinates");
      return askCoordinates();
    }
    var coordinates = input.split(",");
    return new int[]{Integer.parseInt(coordinates[0].strip()), Integer.parseInt(coordinates[1].strip())};
  }

  /**
   * ask if the player want to rotate the piece
   *
   * @param patch (Patch) patch to rotate
   * @return (Patch) patch rotated
   */
  private Patch askRotation(Patch patch) {
    // Ask the player want to rotate the patch
    var reader = new Scanner(System.in);
    System.out.println("Rotate the patch (r/R) Flip the patch (f/F) or do nothing (n/N)?");
    var pattern = Pattern.compile("[rRfFnN]");
    var answer = reader.nextLine();
    if (!pattern.matcher(answer).matches()) {
      System.out.println("Invalid answer");
      askRotation(patch);
    }
    if (answer.equals("r") || answer.equals("R")) {
      System.out.println("Rotate the patch\n" + patch.rotatePiece());
      return askRotation(patch.rotatePiece());
    } else if (answer.equals("f") || answer.equals("F")) {
      System.out.println("Flip the patch\n" + patch.flipPiece());
      return askRotation(patch.flipPiece());
    }
    return patch;
  }

  /**
   * Check if the player can place the patch
   *
   * @param patch       (Patch) patch to place
   * @param coordinates (int[]) coordinates of the patch
   */
  public void placePatch(Patch patch, int[] coordinates) {
    while (!quiltBoard.addPatch(patch, coordinates[0], coordinates[1])) {
      System.out.println("You can't place the patch here");
      coordinates = askCoordinates();
    }
    if (specialTile != null && quiltBoard.containsSquare(specialTile.getSize()) && specialTile.isFree()) {
      System.out.println("You've completed a square of " + specialTile.getSize() + "!");
      specialTile.setPlayer(this);
    }
  }

  /**
   * When the player bought a patch, stats are updated
   *
   * @param patchToBuy    (Patch) patch bought
   * @param circlePatches (CirclePatches) circle of patches
   */
  private void patchBought(Patch patchToBuy, CirclePatches circlePatches) {
    System.out.println("You bought a patch");
    this.timeToken = this.timeToken.forward(patchToBuy.forwardBlocks());
    this.money -= patchToBuy.price();
    this.earnings += patchToBuy.earnings();
    circlePatches.removePatch(patchToBuy);
  }

  /**
   * buy again a patch if the id patch is invalid
   *
   * @param circlePatches (CirclePatches) circle of patches
   * @param nbPatch       (int) number of patches
   * @return (boolean) false
   */
  private boolean buyAgain(CirclePatches circlePatches, int nbPatch) {
    System.out.println("Invalid patch number");
    buyPatch(circlePatches, nbPatch);
    return false;
  }

  /**
   * Under function of the buyPatch player's choice, the patch is bought or not
   *
   * @param patchToBuy    (Patch) patch to buy
   * @param circlePatches (CirclePatches) circle of patches
   * @param nbPatch       (int) number of patches
   * @return (boolean) true if the patch is bought, false otherwise
   */
  private boolean buyThePatch(Patch patchToBuy, CirclePatches circlePatches, int nbPatch) {
    if (patchToBuy.price() > money) {
      System.out.println("You don't have enough money");
      return false;
    }
    if (!quiltBoard.possibleToBuy(patchToBuy)) {
      return buyAgain(circlePatches, nbPatch);
    }
    var newPatch = askRotation(patchToBuy);
    int[] coordinates = askCoordinates();
    placePatch(newPatch, coordinates);
    patchBought(patchToBuy, circlePatches);
    return true;
  }

  /**
   * Ask for buying to the player
   * @param nbPatch (int) number of patches
   * @param circlePatches (CirclePatches) circle of patches
   * @return (boolean) true if the player bought a patch, false otherwise
   */
  private boolean askToBuy(int nbPatch, CirclePatches circlePatches) {
    var scanner = new Scanner(System.in);
    System.out.println("Enter the patch you want to buy (1 to " + nbPatch + "): ");
    var pattern = Pattern.compile(" *\\d+ *");
    var patch = scanner.nextLine();
    List<Patch> patches = circlePatches.getNextPatches(nbPatch);
    if (!pattern.matcher(patch).matches()) {
      return buyAgain(circlePatches, nbPatch);
    }
    var chosen = Integer.parseInt(patch);
    if (chosen > nbPatch || chosen < 1) {
      return buyAgain(circlePatches, nbPatch);
    }
    var patchToBuy = patches.get(chosen - 1);
    return buyThePatch(patchToBuy, circlePatches, nbPatch);
  }

  /**
   * Buy a patch from the circle of patches
   *
   * @param circlePatches (CirclePatches) circle of patches
   * @param nbPatch       (int) number of patches
   * @return (boolean) true if the patch is bought, false otherwise
   */
  private boolean buyPatch(CirclePatches circlePatches, int nbPatch) {
    if (circlePatches.isEmpty()) {
      System.out.println("There is no patch left");
      return false;
    }
    if (circlePatches.size() < nbPatch) {
      nbPatch = circlePatches.size();
    }
    return askToBuy(nbPatch, circlePatches);
  }

  /**
   * Player can choose an action (buy, pass)
   *
   * @param circlePatches (CirclePatches) circle of patches
   * @param nbPatch       (int) number of patches
   * @return (boolean) true if the player Buy, false if the player pass
   */
  public boolean chooseAction(CirclePatches circlePatches, int nbPatch) {
    var reader = new Scanner(System.in);
    System.out.println("Choose an action: B to buy a patch, P to pass");
    var action = reader.next();
    if (action.equals("B") || action.equals("b")) {
      if (!buyPatch(circlePatches, nbPatch)) {
        chooseAction(circlePatches, nbPatch);
      }
    } else if (action.equals("P") || action.equals("p")) {
      return false;
    } else {
      System.out.println("Invalid action");
      chooseAction(circlePatches, nbPatch);
    }
    return true;
  }

  /**
   * get the player's board
   *
   * @return (String) player's board
   */
  public String getBoard() {
    return quiltBoard.toString();
  }

  /**
   * get the player's name
   *
   * @return (String) player's name
   */
  public String getName() {
    return name;
  }

  /**
   * get the time token of the player
   *
   * @return (TimeToken) time token
   */
  public TimeToken getTimeToken() {
    return timeToken;
  }

  /**
   * If the player has a special tile
   *
   * @return (boolean) true if the player has a special tile, false otherwise
   */
  public boolean hasSpecialTile() {
    return specialTile.getPlayer() == this;
  }

  /**
   * get the special tile of the player
   *
   * @return (SpecialTile) special tile
   */
  public SpecialTile getSpecialTile() {
    return specialTile;
  }

  /**
   * get the player's score
   *
   * @return (int) score
   */
  public int getScore() {
    return money - quiltBoard.nbEmptyCases() * 2;
  }
}
