package patchwork.main;

import patchwork.display.Display;
import patchwork.game.main.Game;

public class Patchwork {
  public static void main(String[] args) {
    Display display = Display.selectDisplay();
    var players = display.initPlayers(5);
    var game = Game.selectGame(display.askGameMode(Game.getGames()), players);
    try {
      game.init();
    } catch (Exception e) {
      e.printStackTrace();
    }
    game.play(display);
  }
}
