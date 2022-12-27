package patchwork.game.main;

import patchwork.display.Display;
import patchwork.game.CentralBoard;
import patchwork.game.CirclePatches;
import patchwork.game.Player;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

public final class BasicGame implements Game {
  private final ArrayList<Player> players;
  private final CirclePatches circlePatches;
  private final CentralBoard centralBoard;

  public BasicGame(ArrayList<Player> players) {
    this.players = players;
    this.circlePatches = new CirclePatches();
    this.centralBoard = new CentralBoard();
  }

  public String getGameName() {
    return "basic";
  }

  @Override
  public void init() throws IOException {
    circlePatches.load(Path.of("src/patchwork/game/assets/basic_circle_patches.txt"));
    centralBoard.load(Path.of("src/patchwork/game/assets/basic_board.txt"));
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
      if (!display.chooseAction(player, circlePatches, 3)) { // TODO: add parameter to not ask for rotation in BasicMode
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
      display.displayPlayerAfterMove(player);
      display.askContinue();
      player = player.nextPlayer(players);
      display.printNextPlayer(player);
    } while (!centralBoard.gameIsFinished(players, circlePatches));
    var winner = centralBoard.getWinner(players);
    display.displayWinner(winner);
  }
}
