package patchwork.game.main;

import patchwork.display.Display;
import patchwork.game.Player;

import java.util.ArrayList;
import java.util.List;

public sealed interface Game permits BasicGame, AdvancedGame {
  void init() throws Exception;

  void play(Display display);

  static List<String> getGames() {
    return List.of("basic", "advanced");
  }

  static Game selectGame(String gameName, ArrayList<Player> players) {
    return switch (gameName) {
      case "basic" -> new BasicGame(players);
      case "advanced" -> new AdvancedGame(players);
      default -> throw new IllegalArgumentException("Unknown game: " + gameName);
    };
  }
}
