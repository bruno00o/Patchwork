package patchwork.display;

import fr.umlv.zen5.Application;
import fr.umlv.zen5.ApplicationContext;
import patchwork.game.*;
import patchwork.game.main.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public sealed interface Display permits ASCII, GUI {
  String askGameMode(List<String> games);
  ArrayList<Player> initPlayers(int money);
  Player whoStarts(ArrayList<Player> players);
  void displayBoard(CentralBoard centralBoard);

  void askContinue();

  void displayPlayer(Player player);

  boolean chooseAction(Player player, CirclePatches circlePatches, int nbPatch);

  void buttonFound();

  void leatherPatchFound(Player player, Patch leatherPatch);

  void displayPlayerAfterMove(Player player);

  void printNextPlayer(Player player);

  void specialTileFound(Player player, SpecialTile specialTile);

  void displayWinner(Player player);

}
