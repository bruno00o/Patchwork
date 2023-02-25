package fr.uge.patchwork.game.main;

import fr.uge.patchwork.display.Display;
import fr.uge.patchwork.game.CentralBoard;
import fr.uge.patchwork.game.CirclePatches;
import fr.uge.patchwork.game.Patch;
import fr.uge.patchwork.game.Player;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;


/**
 * Class for a basic game
 */
public final class BasicGame implements Game {

  private final ArrayList<Player> players;
  private final CirclePatches circlePatches;
  private final CentralBoard centralBoard;

  /**
   * Init a basic game
   *
   * @param players (ArrayList<Player>) list of players
   */
  public BasicGame(ArrayList<Player> players) {
    Objects.requireNonNull(players);
    this.players = players;
    this.circlePatches = new CirclePatches();
    this.centralBoard = new CentralBoard();
  }

  /**
   * Init the game
   *
   * @throws IOException if the file cannot be read
   */
  @Override
  public void init() throws IOException {
    circlePatches.load(Path.of("src/fr/uge/patchwork/game/assets/basic_circle_patches.txt"));
    centralBoard.load(Path.of("src/fr/uge/patchwork/game/assets/basic_board.txt"));
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
    Objects.requireNonNull(display);
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
    if (centralBoard.playerPassedOnButton(player, oldPosition)) {
      display.buttonFound();
    }
    var leatherPatch = centralBoard.playerPassedOnPatch(player, oldPosition);
    if (leatherPatch != null) {
      display.leatherPatchFound(player, leatherPatch);
      centralBoard.removeLeatherPatch(leatherPatch);
    }
    display.askContinue();
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
      display.displayPlayerAfterMove(player);
      moveTokenActions(display, player);
      player = player.nextPlayer(players);
      display.printNextPlayer(player);
    } while (!centralBoard.gameIsFinished(players, circlePatches));
    var winner = centralBoard.getWinner(players);
    display.displayWinner(winner);
  }
}
