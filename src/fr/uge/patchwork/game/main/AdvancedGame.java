package fr.uge.patchwork.game.main;

import fr.uge.patchwork.display.Display;
import fr.uge.patchwork.game.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;


/**
 * Class for an advanced game
 */
public final class AdvancedGame implements Game {
  private final ArrayList<Player> players;
  private final CirclePatches circlePatches;
  private final CentralBoard centralBoard;

  private final SpecialTile specialTile;

  /**
   * Init an advanced game
   *
   * @param players (ArrayList<Player>) list of players
   */
  public AdvancedGame(ArrayList<Player> players) {
    Objects.requireNonNull(players);
    this.players = players;
    this.circlePatches = new CirclePatches();
    this.centralBoard = new CentralBoard();
    this.specialTile = new SpecialTile(7, 0, null);
  }

  /**
   * Init the game
   *
   * @throws IOException if the file cannot be read
   */
  @Override
  public void init() throws IOException {
    circlePatches.load(Path.of("src/fr/uge/patchwork/game/assets/complete_circle_patches.txt"));
    centralBoard.load(Path.of("src/fr/uge/patchwork/game/assets/complete_board.txt"));
    circlePatches.shuffle();
    circlePatches.placeNeutralToken();
    centralBoard.initPlayers(players);
  }

  /**
   * Main actions of the game for a player
   *
   * @param display (Display) the display
   * @param player  (Player) the player
   */
  private void mainAction(Display display, Player player) {
    List<String> options;
    if (player.getQuiltBoard().canAddPatches(circlePatches.getNextPatches(3), player)) {
      options = List.of("1. Buy a patch", "2. Pass");
    } else {
      options = List.of("2. Pass");
    }
    Optional<Map<Patch, int[]>> action = display.chooseAction(player, circlePatches, 3, options);
    if (action.isEmpty()) {
      player.passTurn(players, centralBoard);
    } else {
      Patch patch = action.get().keySet().iterator().next();
      int x = action.get().get(patch)[0];
      int y = action.get().get(patch)[1];
      player.buyPatch(patch, circlePatches, centralBoard);
      player.placePatch(patch, x, y);
    }
  }

  /**
   * Move tokens of the game and do the actions if needed
   *
   * @param display (Display) the display
   * @param player  (Player) the player
   */
  private void moveTokenActions(Display display, Player player) {
    int oldPosition = centralBoard.moveToken(player);
    display.displayPlayerAfterMove(player);
    if (centralBoard.playerPassedOnButton(player, oldPosition)) {
      display.buttonFound();
    }
    var leatherPatch = centralBoard.playerPassedOnPatch(player, oldPosition);
    if (leatherPatch != null) {
      Map<Patch, int[]> leatherPatchP = display.leatherPatchFound(player, leatherPatch);
      Patch lp = leatherPatchP.keySet().iterator().next();
      int x = leatherPatchP.get(lp)[0];
      int y = leatherPatchP.get(lp)[1];
      player.getQuiltBoard().addPatch(lp, x, y);
      centralBoard.removeLeatherPatch(leatherPatch);
    }
    if (!specialTile.isOwned() && player.checkSpecialTile(specialTile)) {
      display.specialTileFound(player, specialTile);
      specialTile.setPlayer(player);
    }
  }

  /**
   * End of the game actions
   *
   * @param display (Display) the display
   */
  private void endGame(Display display) {
    var specialTileOwner = specialTile.getPlayer();
    if (specialTileOwner != null) {
      specialTileOwner.addSpecialTileGain(specialTile);
    }
    var winner = centralBoard.getWinner(players);
    display.displayWinner(winner);
  }

  /**
   * Play the game
   *
   * @param display (Display) display
   */
  @Override
  public void play(Display display) {
    Objects.requireNonNull(display);
    // var player = display.whoStarts(players);
    var player = players.get(0);
    display.askContinue();
    do {
      display.displayBoard(centralBoard);
      display.askContinue();
      display.displayPlayer(player);
      display.askContinue();
      mainAction(display, player);
      moveTokenActions(display, player);
      display.askContinue();
      player = player.nextPlayer(players);
      display.printNextPlayer(player);
    } while (!centralBoard.gameIsFinished(players, circlePatches));
    endGame(display);
  }
}
