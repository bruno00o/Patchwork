package fr.uge.patchwork.game.main;

import fr.uge.patchwork.display.Display;
import fr.uge.patchwork.game.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Interface for a game
 */
public sealed interface Game permits BasicGame, AdvancedGame {
  /**
   * Initialize the game
   *
   * @throws Exception
   */
  void init() throws Exception;

  /**
   * Play the game
   *
   * @param display
   */
  void play(Display display);

  /**
   * Get the name of the game
   *
   * @return (List < String >) the name of the game
   */
  static List<String> getGames() {
    return List.of("basic", "advanced");
  }

  /**
   * Get the game corresponding to the name
   *
   * @param gameName (String) the name of the game
   * @return (Game) the game
   */
  static Game selectGame(String gameName, ArrayList<Player> players) {
    return switch (gameName) {
      case "basic" -> new BasicGame(players);
      case "advanced" -> new AdvancedGame(players);
      default -> throw new IllegalArgumentException("Unknown game: " + gameName);
    };
  }
}
