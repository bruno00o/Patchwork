package patchwork.game;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class Player {
  public final String name;
  public final char shortName;
  private final QuiltBoard quiltBoard;
  private TimeToken timeToken;
  public int earnings;
  public int money;


  /**
   * Init a player
   *
   * @param name  (String) name of the player
   * @param money (int) money of the player
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

  public TimeToken getTimeToken() {
    return timeToken;
  }

  public int getPosition() {
    return timeToken.getPosition();
  }

  public Patch getPatchByChoice(CirclePatches circlePatches, int nbPatch, int choice) {
    List<Patch> patches = circlePatches.getNextPatches(nbPatch);
    return patches.get(choice - 1);
  }

  public String checkPatchChoice(CirclePatches circlePatches, int nbPatch, int choice) {
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

  public void buyPatch(Patch patch, CirclePatches circlePatches) {
    this.timeToken = timeToken.forward(patch.forwardBlocks());
    this.money -= patch.price();
    this.earnings += patch.earnings();
    circlePatches.removePatch(patch);
  }

  public void placePatch(Patch patch, int x, int y) {
    quiltBoard.addPatch(patch, x, y);
  }

  private Player getMaxPosition(List<Player> players) {
    return players.stream().max(Comparator.comparingInt(Player::getPosition)).orElse(null);
  }

  public void passTurn(List<Player> players) {
    Player maxPosition = getMaxPosition(players);
    if (maxPosition == null || maxPosition == this) {
      return;
    }
    int maxPositionValue = maxPosition.getPosition();
    int position = getPosition();
    if (position < maxPositionValue) {
      this.timeToken = timeToken.forward(maxPositionValue - position);
    }
    money += maxPositionValue - position;
  }

  public Player nextPlayer(List<Player> players) {
    return players.stream().min(Comparator.comparingInt(Player::getPosition)).orElse(null);
  }

  public boolean checkSpecialTile(SpecialTile specialTile) {
    return quiltBoard.containsSquare(specialTile.getSize());
  }

  public void addSpecialTileGain(SpecialTile specialTile) {
    this.money += specialTile.getEarnings();
  }

  public int getScore() {
    return money - quiltBoard.nbEmptySquares();
  }

  @Override
  public String toString() {
    var sb = new StringBuilder();
    sb.append(name).append("\n")
            .append("Money: ").append(money).append("\n")
            .append("Board:\n").append(quiltBoard);
    return sb.toString();
  }
}