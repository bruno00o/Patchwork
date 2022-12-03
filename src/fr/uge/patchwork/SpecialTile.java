package fr.uge.patchwork;

/**
 * The special tile representing the 7x7 grid of the board.
 *
 * @author Seilliebert Bruno & Oeuvrard Dilien
 */
public class SpecialTile {
  private final int size;
  private final int earnings;
  private Player player;

  /**
   * Init a special tile.
   * @param size (int) the size of the special tile
   * @param earnings (int) the earnings of the special tile
   * @param player (Player) the player who owns the special tile
   */
  public SpecialTile(int size, int earnings, Player player) {
    this.size = size;
    this.earnings = earnings;
    this.player = player;
  }

  /**
   * Get the size of the special tile.
   * @return (int) the size of the special tile
   */
  public int getSize() {
    return size;
  }

  /**
   * Get the earnings of the special tile.
   * @return (int) the earnings of the special tile
   */
  public int getEarnings() {
    return earnings;
  }

  /**
   * Get the player who owns the special tile.
   * @return (Player) the player who owns the special tile
   */
  public Player getPlayer() {
    return player;
  }

  /**
   * Set the player who owns the special tile.
   * @param player (Player) the player who owns the special tile
   */
  public void setPlayer(Player player) {
    this.player = player;
  }

  /**
   * If the special tile is owned by no one, return true.
   * @return (boolean) true if the special tile is owned by no one
   */
  public boolean isFree() {
    return player == null;
  }
}
