package fr.uge.patchwork;



import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Player {
  private final String name;
  private final QuiltBoard quiltBoard;
  private final char shortName;
  private TimeToken timeToken;

  private int earnings;
  private int money;

  public Player(String name, int money) {
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
  }

  public int getMoney() {
    return money;
  }

  public void setMoney(int money) {
    this.money = money;
  }

  public void addMoney(int money) {
    this.money += money;
  }


  public int getPosition() {
    return timeToken.position();
  }

  public void setPosition(int position, int oldPosition) {
    timeToken = new TimeToken(position, oldPosition, shortName);
  }

  public QuiltBoard getQuiltBoard() {
    return quiltBoard;
  }

  public static int[] askCoordinates() {
    Scanner scanner = new Scanner(System.in);
    System.out.println("Enter the coordinates of the piece you want to place (x, y): ");
    var pattern = Pattern.compile("\\d+, *\\d+");
    var input = scanner.nextLine();
    if (!pattern.matcher(input).matches()) {
      System.out.println("Invalid coordinates");
      return askCoordinates();
    }
    var coordinates = input.split(",");
    return new int[]{Integer.parseInt(coordinates[0].strip()), Integer.parseInt(coordinates[1].strip())};
  }

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

  private void patchBought(Patch patchToBuy, CirclePatches circlePatches) {
    System.out.println("You bought a patch");
    this.timeToken = this.timeToken.forward(patchToBuy.forwardBlocks());
    this.money += patchToBuy.price();
    this.earnings = patchToBuy.earnings();
    circlePatches.removePatch(patchToBuy);
  }

  private boolean buyAgain(CirclePatches circlePatches, int nbPatch) {
    System.out.println("Invalid patch number");
    buyPatch(circlePatches, nbPatch);
    return false;
  }

  private boolean buyPatch(CirclePatches circlePatches, int nbPatch) {
    var scanner = new Scanner(System.in);
    if (circlePatches.isEmpty()) {
      System.out.println("There is no patch left");
      return false;
    }
    if (circlePatches.size() < nbPatch) {
      nbPatch = circlePatches.size();
    }

    System.out.println("Enter the patch you want to buy (1 to " + nbPatch + "): ");
    var pattern = Pattern.compile(" *\\d+ *");
    var patch = scanner.nextLine();
    List<Patch> patches = circlePatches.getNextPatches(nbPatch);
    if (!pattern.matcher(patch).matches()) {
      return buyAgain(circlePatches, nbPatch);
    }
    var chosen = Integer.parseInt(patch);
    if(chosen > nbPatch || chosen < 1){
      return buyAgain(circlePatches, nbPatch);
    }
    var patchToBuy = patches.get(chosen - 1);
    if (patchToBuy.price() > money) {
      System.out.println("You don't have enough money");
      return false;
    }
    patchToBuy = askRotation(patchToBuy);
    int[] coordinates = askCoordinates();
    if (quiltBoard.addPatch(patchToBuy, coordinates[0], coordinates[1])) {
      patchBought(patchToBuy, circlePatches);
      return true;
    }
    System.out.println("Invalid coordinates");
    return false;
  }

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

  public String getBoard() {
    return quiltBoard.toString();
  }

  public String getName() {
    return name;
  }

  public TimeToken getTimeToken() {
    return timeToken;
  }

  public int getScore() {
    return money - quiltBoard.nbEmptyCases() * 2;
  }
}
