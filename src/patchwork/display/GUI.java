package patchwork.display;

import patchwork.game.*;

import java.util.ArrayList;
import java.util.List;

public record GUI() implements Display {
  public GUI {
    // TODO
  }

  @Override
  public String askGameMode(List<String> games) {
    // TODO
    return null;
  }

  @Override
  public ArrayList<Player> initPlayers(int money) {
    // TODO
    return null;
  }

  @Override
  public Player whoStarts(ArrayList<Player> players) {
    // TODO
    return null;
  }

  @Override
  public void displayBoard(CentralBoard centralBoard) {
    // TODO
  }

  @Override
  public void askContinue() {
    // TODO
  }

  @Override
  public void displayPlayer(Player player) {
    // TODO
  }

  @Override
  public boolean chooseAction(Player player, CirclePatches circlePatches, int nbPatch) {
    // TODO
    return false;
  }

  @Override
  public void buttonFound() {
    // TODO
  }

  @Override
  public void leatherPatchFound(Player player, Patch leatherPatch) {
    // TODO
  }

  @Override
  public void displayPlayerAfterMove(Player player) {
    // TODO
  }

  @Override
  public void printNextPlayer(Player player) {
    // TODO
  }

  @Override
  public void specialTileFound(Player player, SpecialTile specialTile) {
    // TODO
  }

  @Override
  public void displayWinner(Player player) {
    // TODO
  }
}

