package fr.uge.patchwork.display;

import fr.uge.patchwork.game.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Interface for a display
 */
public sealed interface Display permits ASCII, GUI {
  /**
   * Ask the game mode to the user
   *
   * @param games (List < String >) the list of the games
   * @return (String) the game mode chosen by the user
   */
  String askGameMode(List<String> games);

  /**
   * Ask the players last time they used a needle
   *
   * @param players (ArrayList < Player >) the list of the players
   * @return (Player) the player who used the needle last time
   */
  Player whoStarts(ArrayList<Player> players);

  /**
   * Display the central board
   *
   * @param centralBoard (CentralBoard) the central board
   */
  void displayBoard(CentralBoard centralBoard);

  /**
   * Ask the player to continue
   */
  void askContinue();

  /**
   * Display the player (name, money, board)
   *
   * @param player (Player) the player
   */
  void displayPlayer(Player player);

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
  Optional<Map<Patch, int[]>> chooseAction(Player player, CirclePatches circlePatches, int nbPatch, List<String> options);

  /**
   * Display the button found message
   */
  void buttonFound();

  /**
   * Display the leather patch found message and ask the player to place it
   *
   * @param player       (Player) the player
   * @param leatherPatch (LeatherPatch) the leather patch
   * @return (Map < Patch, int[] >) the patch and the position chosen by the player
   */
  Map<Patch, int[]> leatherPatchFound(Player player, Patch leatherPatch);

  /**
   * Display the player after a move
   *
   * @param player (Player) the player
   */
  void displayPlayerAfterMove(Player player);

  /**
   * Display the next player
   *
   * @param player (Player) the player
   */
  void printNextPlayer(Player player);

  /**
   * Display the special tile found message
   *
   * @param player (Player) the player
   * @param specialTile (SpecialTile) the special tile
   */
  void specialTileFound(Player player, SpecialTile specialTile);

  /**
   * Display the winner
   *
   * @param player (Player) the player
   */
  void displayWinner(Player player);

}
