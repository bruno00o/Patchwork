package patchwork.game.main;

import patchwork.display.Display;
import patchwork.game.CentralBoard;
import patchwork.game.CirclePatches;
import patchwork.game.Player;
import patchwork.game.SpecialTile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

public final class AdvancedGame implements Game {
  private final ArrayList<Player> players;
  private final CirclePatches circlePatches;
  private final CentralBoard centralBoard;

  private final SpecialTile specialTile;

  /**
   * Init a basic game
   *
   * @param players (ArrayList<Player>) list of players
   */
  public AdvancedGame(ArrayList<Player> players) {
    this.players = players;
    this.circlePatches = new CirclePatches();
    this.centralBoard = new CentralBoard();
    this.specialTile = new SpecialTile(7, 0, null);
  }

  @Override
  public void init() throws IOException {
    circlePatches.load(Path.of("src/patchwork/game/assets/complete_circle_patches.txt"));
    centralBoard.load(Path.of("src/patchwork/game/assets/complete_board.txt"));
    circlePatches.shuffle();
    circlePatches.placeNeutralToken();
    centralBoard.initPlayers(players);
  }

  @Override
  public void play(Display display) {
    // var player = display.whoStarts(players);
    var player = players.get(0);
    display.askContinue();
    do {
      display.displayBoard(centralBoard);
      display.askContinue();
      display.displayPlayer(player);
      if (!display.chooseAction(player, circlePatches, 3)) {
        player.passTurn(players);
      }
      int oldPosition = centralBoard.moveToken(player);
      if (centralBoard.playerPassedOnButton(player, oldPosition)) {
        display.buttonFound();
      }
      var leatherPatch = centralBoard.playerPassedOnPatch(player, oldPosition);
      if (leatherPatch != null) {
        display.leatherPatchFound(player, leatherPatch);
        centralBoard.removeLeatherPatch(leatherPatch);
      }
      if (!specialTile.isOwned() && player.checkSpecialTile(specialTile)) {
        display.specialTileFound(player, specialTile);
        specialTile.setPlayer(player);
      }
      display.displayPlayerAfterMove(player);
      display.askContinue();
      player = player.nextPlayer(players);
      display.printNextPlayer(player);
    } while (!centralBoard.gameIsFinished(players, circlePatches));
    var specialTileOwner = specialTile.getPlayer();
    if (specialTileOwner != null) {
      specialTileOwner.addSpecialTileGain(specialTile);
    }
    var winner = centralBoard.getWinner(players);
    display.displayWinner(winner);
  }
}
