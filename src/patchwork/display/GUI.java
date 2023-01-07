package patchwork.display;

import patchwork.game.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import fr.umlv.zen5.Application;
import fr.umlv.zen5.ApplicationContext;
import fr.umlv.zen5.Event;
import fr.umlv.zen5.ScreenInfo;
import fr.umlv.zen5.Event.Action;

public record GUI(ApplicationContext context) implements Display {

  private static final Color BACKGROUND_COLOR = new Color(235, 220, 179);
  private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 40);
  private static final Font TEXT_FONT = new Font("Arial", Font.PLAIN, 20);
  private static float WIDTH;
  private static float HEIGHT;

  public GUI {
    ScreenInfo screenInfo = context.getScreenInfo();
    WIDTH = screenInfo.getWidth();
    HEIGHT = screenInfo.getHeight();
  }

  public static void drawCenteredTitleText(Graphics2D g, String text, float y) {
    g.setColor(Color.BLACK);
    FontMetrics metrics = g.getFontMetrics(TITLE_FONT);
    g.setFont(TITLE_FONT);
    float x = (WIDTH - metrics.stringWidth(text)) / 2;
    g.drawString(text, x, y);
    g.setFont(TEXT_FONT);
  }

  private static void clearScreen(Graphics2D g) {
    g.setColor(BACKGROUND_COLOR);
    g.fillRect(0, 0, (int) WIDTH, (int) HEIGHT);
  }

  @Override
  public String askGameMode(List<String> games) {
    context.renderFrame(graphics -> {
      graphics.setColor(BACKGROUND_COLOR);
      graphics.fill(new Rectangle2D.Float(0, 0, WIDTH, HEIGHT));
      drawCenteredTitleText(graphics, "Please select a game mode", HEIGHT / 2 - HEIGHT / 4);
      for (int i = 0; i < games.size(); i++) {
        // Draw the text in a box
        graphics.setColor(Color.WHITE);
        graphics.fill(new Rectangle2D.Float(WIDTH / 2 - WIDTH / 4, HEIGHT / 2 - HEIGHT / 8 + i * HEIGHT / 8, WIDTH / 2, HEIGHT / 8));
        graphics.setColor(Color.BLACK);
        graphics.draw(new Rectangle2D.Float(WIDTH / 2 - WIDTH / 4, HEIGHT / 2 - HEIGHT / 8 + i * HEIGHT / 8, WIDTH / 2, HEIGHT / 8));
        graphics.drawString(games.get(i).toUpperCase(), WIDTH / 2 - WIDTH / 4 + WIDTH / 32, HEIGHT / 2 - HEIGHT / 8 + i * HEIGHT / 8 + HEIGHT / 16);
      }
    });

// Wait for the user to select a game mode

    for (; ; ) {
      Event event = context.pollOrWaitEvent(10);
      if (event == null) {
        continue;
      }
      if (event.getAction() == Action.POINTER_UP) {
        Point2D.Float location = event.getLocation();
        for (int i = 0; i < games.size(); i++) {
          if (location.x > WIDTH / 2 - WIDTH / 4 && location.x < WIDTH / 2 + WIDTH / 4 && location.y > HEIGHT / 2 - HEIGHT / 8 + i * HEIGHT / 8 && location.y < HEIGHT / 2 + HEIGHT / 8 + i * HEIGHT / 8) {
            return games.get(i);
          }
        }
      }
    }
  }

  @Override
  public ArrayList<Player> initPlayers(int money) {
    ArrayList<Player> players = new ArrayList<>();
    context.renderFrame(graphics -> {
      clearScreen(graphics);
      drawCenteredTitleText(graphics, "Please select the number of players", HEIGHT / 2 - HEIGHT / 4);
      for (int i = 2; i <= 5; i++) {
        // Draw the text in a little box from left to right
        graphics.setColor(Color.WHITE);
        graphics.fill(new Rectangle2D.Float(WIDTH / 2 - WIDTH / 4 + (i - 2) * WIDTH / 8, HEIGHT / 2 - HEIGHT / 8, WIDTH / 8, HEIGHT / 8));
        graphics.setColor(Color.BLACK);
        graphics.draw(new Rectangle2D.Float(WIDTH / 2 - WIDTH / 4 + (i - 2) * WIDTH / 8, HEIGHT / 2 - HEIGHT / 8, WIDTH / 8, HEIGHT / 8));
        graphics.drawString(Integer.toString(i), WIDTH / 2 - WIDTH / 4 + (i - 2) * WIDTH / 8 + WIDTH / 32, HEIGHT / 2 - HEIGHT / 8 + HEIGHT / 16);
      }
    });

    // Wait for the user to select the number of players

    for (; ; ) {
      Event event = context.pollOrWaitEvent(10);
      if (event == null) {
        continue;
      }
      if (event.getAction() == Action.POINTER_UP) {
        Point2D.Float location = event.getLocation();
        for (int i = 2; i <= 5; i++) {
          if (location.x > WIDTH / 2 - WIDTH / 4 + (i - 2) * WIDTH / 8 && location.x < WIDTH / 2 - WIDTH / 4 + (i - 1) * WIDTH / 8 && location.y > HEIGHT / 2 - HEIGHT / 8 && location.y < HEIGHT / 2 + HEIGHT / 8) {
            for (int j = 0; j < i; j++) {
              players.add(new Player("Player " + (j + 1), (char) (j + 1 + 49), money));
            }
            return players;
          }
        }
      }
    }
  }

  @Override
  public Player whoStarts(ArrayList<Player> players) {
    // TODO
    return null;
  }

  @Override
  public void displayBoard(CentralBoard centralBoard) {
    context.renderFrame(graphics -> {
      clearScreen(graphics);
      drawCenteredTitleText(graphics, "Central Board", HEIGHT / 2 - HEIGHT / 4);
      graphics.setColor(Color.BLACK);
      graphics.draw(new Rectangle2D.Float(WIDTH / 2 - WIDTH / 4, HEIGHT / 2 - HEIGHT / 8, WIDTH / 2, HEIGHT / 8));
      graphics.drawString(centralBoard.toString(), WIDTH / 2 - WIDTH / 4 + WIDTH / 32, HEIGHT / 2 - HEIGHT / 8 + HEIGHT / 16);
    });
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

