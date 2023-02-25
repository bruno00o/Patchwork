package fr.uge.patchwork.game;

import java.util.Objects;

/**
 * Class for a special tile
 */
public class SpecialTile {

  private final int size;
  private final int earnings;
  private Player player;

  /**
   * Init a special tile
   * Here player can be null if the tile is not owned
   *
   * @param size     (int) size of the special tile
   * @param earnings (int) earnings of the special tile
   * @param player   (Player) player of the special tile
   */
  public SpecialTile(int size, int earnings, Player player) {
    this.size = size;
    this.earnings = earnings;
    this.player = player;
  }

  /**
   * Return the size of the special tile
   *
   * @return (int)
   */
  public int getSize() {
    return size;
  }

  /**
   * Return the earnings of the special tile
   *
   * @return (int)
   */
  public int getEarnings() {
    return earnings;
  }

  /**
   * Return the player of the special tile
   *
   * @return (Player)
   */
  public Player getPlayer() {
    return player;
  }

  /**
   * Set the player of the special tile
   *
   * @param player (Player) player to set
   */
  public void setPlayer(Player player) {
    Objects.requireNonNull(player);
    if (this.isOwned()) {
      throw new IllegalStateException("This special tile is already owned");
    }
    this.player = player;
  }

  /**
   * Return true if the special tile is owned
   *
   * @return (boolean)
   */
  public boolean isOwned() {
    return player != null;
  }
}
