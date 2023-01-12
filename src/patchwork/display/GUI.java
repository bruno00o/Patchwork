package patchwork.display;

import patchwork.game.*;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
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

import javax.imageio.ImageIO;

public record GUI(ApplicationContext context) implements Display {

  private static final Color BACKGROUND_COLOR = new Color(235, 220, 179);
  private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 40);
  private static final Font TEXT_FONT = new Font("Arial", Font.PLAIN, 20);
  private static float WIDTH;
  private static float HEIGHT;

  // in GUI, we can only have 2 players
  private static final Color PLAYER_1_COLOR = new Color(0, 132, 184);
  private static final Color PLAYER_2_COLOR = new Color(0, 102, 75);

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
    context.pollOrWaitEvent(100); // to avoid a bug
    context.renderFrame(graphics -> {
      drawCenteredTitleText(graphics, "Please select a game mode", HEIGHT / 2 - HEIGHT / 4);
      graphics.setColor(Color.BLACK);
      graphics.setFont(TEXT_FONT);
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
          if (location.x > WIDTH / 2 - WIDTH / 4 && location.x < WIDTH / 2 + WIDTH / 4 + WIDTH / 2 && location.y > HEIGHT / 2 - HEIGHT / 8 + i * HEIGHT / 8 && location.y < HEIGHT / 2 - HEIGHT / 8 + i * HEIGHT / 8 + HEIGHT / 8) {
            return games.get(i);
          }
        }
      }
    }
  }

  @Override
  public ArrayList<Player> initPlayers(int money) {
    ArrayList<Player> players = new ArrayList<>();
    for (int i = 0; i < 2; i++) {
      players.add(new Player("Player " + (i + 1), (char) ('1' + i), money));
    }
    return players;
//    context.renderFrame(graphics -> {
//      clearScreen(graphics);
//      drawCenteredTitleText(graphics, "Please select the number of players", HEIGHT / 2 - HEIGHT / 4);
//      for (int i = 2; i <= 5; i++) {
//        // Draw the text in a little box from left to right
//        graphics.setColor(Color.WHITE);
//        graphics.fill(new Rectangle2D.Float(WIDTH / 2 - WIDTH / 4 + (i - 2) * WIDTH / 8, HEIGHT / 2 - HEIGHT / 8, WIDTH / 8, HEIGHT / 8));
//        graphics.setColor(Color.BLACK);
//        graphics.draw(new Rectangle2D.Float(WIDTH / 2 - WIDTH / 4 + (i - 2) * WIDTH / 8, HEIGHT / 2 - HEIGHT / 8, WIDTH / 8, HEIGHT / 8));
//        graphics.drawString(Integer.toString(i), WIDTH / 2 - WIDTH / 4 + (i - 2) * WIDTH / 8 + WIDTH / 32, HEIGHT / 2 - HEIGHT / 8 + HEIGHT / 16);
//      }
//    });
//
//    // Wait for the user to select the number of players
//
//    for (; ; ) {
//      Event event = context.pollOrWaitEvent(10);
//      if (event == null) {
//        continue;
//      }
//      if (event.getAction() == Action.POINTER_UP) {
//        Point2D.Float location = event.getLocation();
//        for (int i = 2; i <= 5; i++) {
//          if (location.x > WIDTH / 2 - WIDTH / 4 + (i - 2) * WIDTH / 8 && location.x < WIDTH / 2 - WIDTH / 4 + (i - 1) * WIDTH / 8 && location.y > HEIGHT / 2 - HEIGHT / 8 && location.y < HEIGHT / 2 + HEIGHT / 8) {
//            int nbPlayers = i;
//            context.renderFrame(graphics -> {
//              clearScreen(graphics);
//              drawCenteredTitleText(graphics, "You selected " + nbPlayers + " players", HEIGHT / 2 - HEIGHT / 4);
//            });
//            for (int j = 0; j < i; j++) {
//              players.add(new Player("Player " + (j + 1), (char) (j + 1 + 49), money));
//            }
//            return players;
//          }
//        }
//      }
//    }
  }

  @Override
  public Player whoStarts(ArrayList<Player> players) {
    // TODO
    return null;
  }

  public void drawCentralBoardSquare(Graphics2D g, int x, int y, int width, int height, Color color) {
    g.setColor(color);
    g.fill(new Rectangle2D.Float(x, y, width, height));
    g.setColor(Color.BLACK);
    g.draw(new Rectangle2D.Float(x, y, width, height));
  }

  @Override
  public void displayBoard(CentralBoard centralBoard) {
    //System.out.println(centralBoard);
    context.renderFrame(graphics -> {
      clearScreen(graphics);
      drawCenteredTitleText(graphics, "Central Board", HEIGHT / 2 - HEIGHT / 4);
      // draw a table 8x8 of 50x50 centered
      float x = WIDTH / 2 - 8 * 50 / 2;
      float y = HEIGHT / 2 - 8 * 50 / 2;
      // draw an image of (8*50)x(8*50) centered
      var path = Path.of("src/patchwork/display/assets/central_board.png");
      try {
        var image = ImageIO.read(path.toFile());
        graphics.drawImage(image, (int) x, (int) y, 8 * 50, 8 * 50, null);
      } catch (IOException e) {
        e.printStackTrace();
      }

      graphics.setColor(Color.BLACK);
      graphics.setFont(new Font("Arial", Font.BOLD, 20));
      int direction = 1;
      int k = 0;
      int i = 2;
      int j = 0;
      int n = 8;
      while (k <= centralBoard.getCentralBoardSize()) {
        //graphics.drawString(Integer.toString(k), (int) x + i * 50 + 25, (int) y + j * 50 + 25);
        CentralBoardSquare square = centralBoard.getCentralBoardSquare(k);
        if (square != null) {
          if (square.hasButton()) {
            //System.out.println("Button at " + k);
            var path2 = Path.of("src/patchwork/display/assets/button.png");
            try {
              var image = ImageIO.read(path2.toFile());
              // draw an image of 40x40 centered
              graphics.drawImage(image, (int) x + i * 50 + 5, (int) y + j * 50 + 5, 40, 40, null);
            } catch (IOException e) {
              e.printStackTrace();
            }
          }

          if (square.hasLeatherPatch()) {
            //System.out.println("Leather patch at " + k);
            var path2 = Path.of("src/patchwork/display/assets/leather_patch.png");
            try {
              var image = ImageIO.read(path2.toFile());
              // draw an image of 40x40 centered
              graphics.drawImage(image, (int) x + i * 50 + 5, (int) y + j * 50 + 5, 40, 40, null);
            } catch (IOException e) {
              e.printStackTrace();
            }
          }

          if (square.hasTimeToken()) {
            List<TimeToken> timeTokens = square.getTimeTokens();
            for (int l = 0; l < timeTokens.size(); l++) {
              TimeToken timeToken = timeTokens.get(l);
              // draw a filled circle of 20x20 top left for the first time token, bottom right for the second
              graphics.setColor(timeToken.shortName() == '1' ? PLAYER_1_COLOR : PLAYER_2_COLOR);
              graphics.fill(new Ellipse2D.Float((int) x + i * 50 + 5 + l * 20, (int) y + j * 50 + 5 + l * 20, 20, 20));
              // write the short name of the time token in the center of the circle
              graphics.setColor(Color.WHITE);
              graphics.setFont(new Font("Arial", Font.BOLD, 12));
              graphics.drawString(Character.toString(timeToken.shortName()), (int) x + i * 50 + 5 + l * 20 + 7, (int) y + j * 50 + 5 + l * 20 + 15);
            }

          }
        }
        if (direction == 1) {
          if (i ==  n - 1) {
            direction = 2;
            n--;
            j++;
          } else {
            i++;
          }
        } else if (direction == 2) {
          if (j == n) {
            direction = 3;
            i--;
          } else {
            j++;
          }
        } else if (direction == 3) {
          if (i == 8 - n - 1) {
            direction = 4;
            n--;
            j--;
          } else {
            i--;
          }
        } else if (direction == 4) {
          if (j == 8 - n - 1) {
            direction = 1;
            n++;
            i++;
          } else {
            j--;
          }
        }
        k++;

      }
    });
  }

  @Override
  public void askContinue() {
    context.renderFrame(graphics -> {
      graphics.setColor(Color.BLACK);
      graphics.setFont(TEXT_FONT);
      // write this on bottom right
      graphics.drawString("Press any key to continue", (int) WIDTH - 300, (int) HEIGHT - 50);
    });
    for (; ; ) {
      Event event = context.pollOrWaitEvent(10);
      if (event == null) {
        continue;
      }
      if (event.getAction() == Action.KEY_PRESSED) {
        break;
      }
    }
  }

  @Override
  public void displayPlayer(Player player) {
    // Clear the screen and show player
    context.renderFrame(graphics -> {
      clearScreen(graphics);
      drawCenteredTitleText(graphics, player.name, HEIGHT / 2 - HEIGHT / 4);
      graphics.setColor(Color.BLACK);
      graphics.setFont(TEXT_FONT);
      graphics.drawString("Money: " + player.money, WIDTH / 2 - WIDTH / 4, HEIGHT / 2 - HEIGHT / 8);
      // display the player's board
      float x = WIDTH / 2 - 8 * 50 / 2;
      float y = HEIGHT / 2 - 8 * 50 / 2;
      graphics.setColor(Color.BLACK);
      // display the player's patches

      for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
          graphics.draw(new Rectangle2D.Float(x + i * 50, y + j * 50, 50, 50));
        }
      }
    });
  }

  @Override
  public boolean chooseAction(Player player, CirclePatches circlePatches, int nbPatch) {
    // TODO
    return false;
  }

  @Override
  public void buttonFound() {
    // Display text
    context.renderFrame(graphics -> {
      graphics.drawString("You found a button !", WIDTH / 2 - WIDTH / 4, HEIGHT / 2 - HEIGHT / 6);
    });
  }

  @Override
  public void leatherPatchFound(Player player, Patch leatherPatch) {
    // Display text
    context.renderFrame(graphics -> {
      graphics.drawString("You found a leather patch !", WIDTH / 2 - WIDTH / 4, HEIGHT / 2 - HEIGHT / 6);
      graphics.drawString("You can place it on your board for free", WIDTH / 2 - WIDTH / 2, HEIGHT / 2 - HEIGHT / 5);
    });
  }

  @Override
  public void displayPlayerAfterMove(Player player) {
    // TODO
  }

  @Override
  public void printNextPlayer(Player player) {
    // Display the name of the next player
    context.renderFrame(graphics -> {
      graphics.drawString(player.name + "'s turn", WIDTH / 2 - WIDTH / 4, HEIGHT / 2 - HEIGHT / 6);
    });
  }

  @Override
  public void specialTileFound(Player player, SpecialTile specialTile) {
    // Display the winner
    context.renderFrame(graphics -> {
      graphics.drawString("You found a special tile of " + specialTile.getSize() + "x" + specialTile.getSize() + "!",
              WIDTH / 2 - WIDTH / 4, HEIGHT / 2 - HEIGHT / 6);
    });
  }

  @Override
  public void displayWinner(Player player) {
    // Display the winner
    context.renderFrame(graphics -> {
      graphics.drawString(player.name + " won!", WIDTH / 2 - WIDTH / 4, HEIGHT / 2 - HEIGHT / 6);
    });
  }
}

