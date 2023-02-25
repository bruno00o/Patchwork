package fr.uge.patchwork.game;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Class for a player
 */
public class Player {
  private final String name;
  private final char shortName;
  private final QuiltBoard quiltBoard;
  private TimeToken timeToken;
  private int earnings;
  private int money;


  /**
   * Init a player
   *
   * @param name            (String) name of the player
   * @param shortName(char) short name of the player
   * @param money           (int) money of the player
   */
  public Player(String name, char shortName, int money) {
    Objects.requireNonNull(name);
    if (money < 0) {
      throw new IllegalArgumentException("Money must be positive");
    }
    this.name = name;
    this.shortName = shortName;
    this.quiltBoard = new QuiltBoard(9, 9);
    this.timeToken = new TimeToken(0, shortName);
    this.earnings = 0;
    this.money = money;
  }

  /**
   * Return the name of the player
   *
   * @return (String)
   */
  public String getName() {
    return name;
  }

  /**
   * Return the short name of the player
   *
   * @return (char)
   */
  public char getShortName() {
    return shortName;
  }

  /**
   * Return the earnings of the player
   *
   * @return (int)
   */
  public int getEarnings() {
    return earnings;
  }

  /**
   * Return the money of the player
   *
   * @return (int)
   */
  public int getMoney() {
    return money;
  }

  /**
   * Add money to the player
   *
   * @param money (int) money to add
   */
  public void addMoney(int money) {
    this.money += money;
  }

  /**
   * Get the quilt board of the player
   *
   * @return (QuiltBoard)
   */
  public QuiltBoard getQuiltBoard() {
    return quiltBoard;
  }

  /**
   * Get the time token of the player
   *
   * @return (TimeToken)
   */
  public TimeToken getTimeToken() {
    return timeToken;
  }

  /**
   * Get the position of the time token
   *
   * @return (int)
   */
  public int getPosition() {
    return timeToken.position();
  }


  /**
   * Return the patch at the given position's choice of the player in the next nbPatch of the circle patches list
   *
   * @param circlePatches (CirclePatches) circle patches list
   * @param nbPatch       (int) number of patches to look at
   * @param choice        (int) choice of the player
   * @return (Patch)
   */
  public Patch getPatchByChoice(CirclePatches circlePatches, int nbPatch, int choice) {
    Objects.requireNonNull(circlePatches);
    List<Patch> patches = circlePatches.getNextPatches(nbPatch);
    return patches.get(choice - 1);
  }

  /**
   * Check the choice of the player for the patch he wants to take
   * If the choice is valid, return null, else return the error message
   *
   * @param circlePatches (CirclePatches) circle patches list
   * @param nbPatch       (int) number of patches to look at
   * @param choice        (int) choice of the player
   * @return (String) error message if the choice is invalid, null otherwise
   */
  public String checkPatchChoice(CirclePatches circlePatches, int nbPatch, int choice) {
    Objects.requireNonNull(circlePatches);
    nbPatch = circlePatches.getNbPatches(nbPatch);
    if (choice < 1 || choice > nbPatch) {
      return "You must choose a patch between 1 and " + nbPatch;
    }
    Patch patch = getPatchByChoice(circlePatches, nbPatch, choice);
    if (patch == null) {
      return "You must choose a patch between 1 and " + nbPatch;
    }
    if (patch.price() > money) {
      return "You don't have enough money to buy this patch";
    }
    if (!quiltBoard.canAddPatch(patch)) {
      return "You can't add this patch to your quilt board";
    }
    return null;
  }

  /**
   * Make the actions of the player when he buys a patch
   *
   * @param patch         (Patch) patch bought
   * @param circlePatches (CirclePatches) circle patches list
   * @param centralBoard  (CentralBoard) central board
   */
  public void buyPatch(Patch patch, CirclePatches circlePatches, CentralBoard centralBoard) {
    Objects.requireNonNull(patch);
    Objects.requireNonNull(circlePatches);
    Objects.requireNonNull(centralBoard);
    int position = timeToken.position() + patch.forwardBlocks();
    if (position >= centralBoard.getCentralBoardSize() - 1) {
      position = centralBoard.getCentralBoardSize() - 1;
    }
    this.timeToken = new TimeToken(position, shortName);
    this.money -= patch.price();
    this.earnings += patch.earnings();
    circlePatches.removePatch(patch);
  }

  /**
   * Place the patch at the given position in the player's quilt board
   *
   * @param patch (Patch) patch to place
   * @param x     (int) x position
   * @param y     (int) y position
   */
  public void placePatch(Patch patch, int x, int y) {
    Objects.requireNonNull(patch);
    quiltBoard.addPatch(patch, x, y);
  }

  /**
   * Return the player with the highest position
   *
   * @param players (List<Player>) list of players
   * @return (Player)
   */
  private Player getMaxPosition(List<Player> players) {
    Objects.requireNonNull(players);
    return players.stream().max(Comparator.comparingInt(Player::getPosition)).orElse(null);
  }

  /**
   * Realize the actions of the player when he pass his turn
   *
   * @param players      (List<Player>) list of players
   * @param centralBoard (CentralBoard) central board
   */
  public void passTurn(List<Player> players, CentralBoard centralBoard) {
    Objects.requireNonNull(players);
    Objects.requireNonNull(centralBoard);
    Player maxPosition = getMaxPosition(players);
    if (maxPosition == null) {
      return;
    }
    int maxPositionValue = maxPosition.getPosition();
    int position = timeToken.position();
    if (position <= maxPositionValue) {
      int newPosition = maxPositionValue + 1;
      if (newPosition >= centralBoard.getCentralBoardSize() - 1) {
        newPosition = centralBoard.getCentralBoardSize() - 1;
      }
      this.timeToken = new TimeToken(newPosition, shortName);
    }
    money += maxPositionValue - position;
  }

  /**
   * Return the next player to play (the player with the lowest position)
   *
   * @param players (List<Player>) list of players
   * @return (Player)
   */
  public Player nextPlayer(List<Player> players) {
    Objects.requireNonNull(players);
    return players.stream().min(Comparator.comparingInt(Player::getPosition)).orElse(null);
  }

  /**
   * Return true if the player can have the special tile, else return false
   *
   * @param specialTile (SpecialTile) special tile
   * @return (boolean)
   */
  public boolean checkSpecialTile(SpecialTile specialTile) {
    Objects.requireNonNull(specialTile);
    return quiltBoard.containsSquare(specialTile.getSize());
  }

  /**
   * Add the special tile earnings to the player
   *
   * @param specialTile (SpecialTile) special tile
   */
  public void addSpecialTileGain(SpecialTile specialTile) {
    this.money += specialTile.getEarnings();
  }

  /**
   * Return the final score of the player
   *
   * @return (int)
   */
  public int getScore() {
    return money - quiltBoard.nbEmptySquares();
  }

  /**
   * Return the string representation of the player
   *
   * @return (String)
   */
  @Override
  public String toString() {
    return name + "\n" +
            "Money: " + money + "\n" +
            "Board:\n" + quiltBoard;
  }
}