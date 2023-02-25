package fr.uge.patchwork.display;

import fr.umlv.zen5.ApplicationContext;
import fr.umlv.zen5.Event;
import fr.umlv.zen5.Event.Action;
import fr.umlv.zen5.ScreenInfo;
import fr.uge.patchwork.game.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.*;

/**
 * Class for the GUI
 *
 * @param context (ApplicationContext) the context
 */
public record GUI(ApplicationContext context) implements Display {

  private static final Color BACKGROUND_COLOR = new Color(235, 220, 179);
  private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 40);
  private static final Font TEXT_FONT = new Font("Arial", Font.PLAIN, 20);
  private static float WIDTH;
  private static float HEIGHT;
  private static final int SQUARE_SIZE = 50;

  // in GUI, we can only have 2 players
  private static final Color PLAYER_1_COLOR = new Color(0, 132, 184);
  private static final Color PLAYER_2_COLOR = new Color(0, 102, 75);

  public GUI {
    Objects.requireNonNull(context);
    ScreenInfo screenInfo = context.getScreenInfo();
    WIDTH = screenInfo.getWidth();
    HEIGHT = screenInfo.getHeight();
  }

  /**
   * Draw a centered title at the y position
   *
   * @param g    (Graphics2D) the graphics
   * @param text (String) the text to draw
   * @param y    (float) the y position
   */
  private static void drawCenteredTitleText(Graphics2D g, String text, float y) {
    Objects.requireNonNull(g);
    Objects.requireNonNull(text);
    g.setColor(Color.BLACK);
    FontMetrics metrics = g.getFontMetrics(TITLE_FONT);
    g.setFont(TITLE_FONT);
    float x = (WIDTH - metrics.stringWidth(text)) / 2;
    g.drawString(text, x, y);
    g.setFont(TEXT_FONT);
  }

  /**
   * Draw a centered text at the y position
   *
   * @param g    (Graphics2D) the graphics
   * @param text (String) the text to draw
   * @param y    (float) the y position
   */
  private static void drawCenteredSubTitleText(Graphics2D g, String text, float y) {
    Objects.requireNonNull(g);
    Objects.requireNonNull(text);
    g.setColor(Color.BLACK);
    FontMetrics metrics = g.getFontMetrics(TEXT_FONT);
    g.setFont(TEXT_FONT);
    float x = (WIDTH - metrics.stringWidth(text)) / 2;
    g.drawString(text, x, y);
  }

  /**
   * Clear the screen
   *
   * @param g (Graphics2D) the graphics
   */
  private static void clearScreen(Graphics2D g) {
    Objects.requireNonNull(g);
    g.setColor(BACKGROUND_COLOR);
    g.fillRect(0, 0, (int) WIDTH, (int) HEIGHT);
  }

  /**
   * Print the askGameMode menu on the screen
   *
   * @param games (List < Game >) the list of the games
   */
  private void printAskGameMode(List<String> games) {
    Objects.requireNonNull(games);
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
  }

  /**
   * Display the selected game mode on the screen
   *
   * @param mode (String) the selected game mode
   */
  private void printModeSelected(String mode) {
    Objects.requireNonNull(mode);
    context.renderFrame(graphics -> {
      clearScreen(graphics);
      drawCenteredTitleText(graphics, "You selected " + mode.toUpperCase(), HEIGHT / 2);
    });
  }

  /**
   * Ask the game mode to the user
   *
   * @param games (List < String >) the list of the games
   * @return (String) the game mode chosen by the user
   */
  @Override
  public String askGameMode(List<String> games) {
    Objects.requireNonNull(games);
    context.pollOrWaitEvent(5); // to avoid a bug
    printAskGameMode(games);
    int choice = -1;
    for (; ; ) {
      Event event = context.pollOrWaitEvent(10);
      if (event == null) continue;
      if (event.getAction() == Action.POINTER_UP) {
        Point2D.Float location = event.getLocation();
        for (int i = 0; i < games.size(); i++) {
          if (location.x > WIDTH / 2 - WIDTH / 4 && location.x < WIDTH / 2 + WIDTH / 4 + WIDTH / 2 && location.y > HEIGHT / 2 - HEIGHT / 8 + i * HEIGHT / 8 && location.y < HEIGHT / 2 - HEIGHT / 8 + i * HEIGHT / 8 + HEIGHT / 8) {
            choice = i;
            break;
          }
        }
        if (choice != -1) break;
      }
    }
    printModeSelected(games.get(choice));
    return games.get(choice);
  }

  /**
   * Ask the players last time they used a needle
   *
   * @param players (ArrayList < Player >) the list of the players
   * @return (Player) the player who used the needle last time
   */
  @Override
  public Player whoStarts(ArrayList<Player> players) {
    // TODO
    return null;
  }

  /**
   * Draw the image of the board on the screen
   *
   * @param g (Graphics2D) the graphics
   * @param x (int) the x position
   * @param y (int) the y position
   */
  private static void drawImageBoard(Graphics2D g, float x, float y) {
    Objects.requireNonNull(g);
    var path = Path.of("src/fr/uge/patchwork/display/assets/central_board.png");
    try {
      var image = ImageIO.read(path.toFile());
      g.drawImage(image, (int) x, (int) y, 8 * SQUARE_SIZE, 8 * SQUARE_SIZE, null);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Draw an image on a square of the board
   *
   * @param g    (Graphics2D) the graphics
   * @param path (Path) the path of the image
   * @param x    (int) the x position
   * @param y    (int) the y position
   * @param i    (int) the row
   * @param j    (int) the column
   */
  private static void drawImageOnSquare(Graphics2D g, Path path, float x, float y, int i, int j) {
    Objects.requireNonNull(g);
    Objects.requireNonNull(path);
    try {
      var image = ImageIO.read(path.toFile());
      g.drawImage(image, (int) x + i * SQUARE_SIZE + 5, (int) y + j * SQUARE_SIZE + 5, 40, 40, null);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Draw time tokens on the squares of the board
   *
   * @param g          (Graphics2D) the graphics
   * @param timeTokens (List < TimeToken >) the list of the time tokens
   * @param x          (int) the x position
   * @param y          (int) the y position
   * @param i          (int) the row
   * @param j          (int) the column
   */
  private static void drawTimeTokensOnSquare(Graphics2D g, List<TimeToken> timeTokens, float x, float y, int i, int j) {
    Objects.requireNonNull(g);
    Objects.requireNonNull(timeTokens);
    for (int l = 0; l < timeTokens.size(); l++) {
      TimeToken timeToken = timeTokens.get(l);
      g.setColor(timeToken.shortName() == '1' ? PLAYER_1_COLOR : PLAYER_2_COLOR);
      g.fill(new Ellipse2D.Float((int) x + i * SQUARE_SIZE + 5 + l * 20, (int) y + j * SQUARE_SIZE + 5 + l * 20, 20, 20));
      g.setColor(Color.WHITE);
      g.setFont(new Font("Arial", Font.BOLD, 12));
      g.drawString(Character.toString(timeToken.shortName()), (int) x + i * SQUARE_SIZE + 5 + l * 20 + 7, (int) y + j * SQUARE_SIZE + 5 + l * 20 + 15);
    }
  }

  /**
   * Display the central board
   *
   * @param centralBoard (CentralBoard) the central board
   */
  @Override
  public void displayBoard(CentralBoard centralBoard) {
    Objects.requireNonNull(centralBoard);
    float x = WIDTH / 2 - 8 * (float) SQUARE_SIZE / 2;
    float y = HEIGHT / 2 - 8 * (float) SQUARE_SIZE / 2;
    var buttonImagePath = Path.of("src/fr/uge/patchwork/display/assets/button.png");
    var leatherPatchImagePath = Path.of("src/fr/uge/patchwork/display/assets/leather_patch.png");
    context.renderFrame(graphics -> {
      clearScreen(graphics);
      drawCenteredTitleText(graphics, "Central Board", 100);
      drawImageBoard(graphics, x, y);
      graphics.setColor(Color.BLACK);
      graphics.setFont(new Font("Arial", Font.BOLD, 20));
      int direction = 1; // to the right
      int k = 0;
      int i = 2; // start in the grid
      int j = 0;
      int n = 8; // number of squares in the grid
      while (k <= centralBoard.getCentralBoardSize()) {
        CentralBoardSquare square = centralBoard.getCentralBoardSquare(k);
        if (square != null) {
          if (square.hasButton()) {
            drawImageOnSquare(graphics, buttonImagePath, x, y, i, j);
          }
          if (square.hasLeatherPatch()) {
            drawImageOnSquare(graphics, leatherPatchImagePath, x, y, i, j);
          }
          if (square.hasTimeToken()) {
            drawTimeTokensOnSquare(graphics, square.getTimeTokens(), x, y, i, j);
          }
        }
        // to make a spiral update direction and i, j
        if (direction == 1) {
          if (i == n - 1) {
            direction = 2;
            n--;
            j++;
          } else
            i++;
        } else if (direction == 2) {
          if (j == n) {
            direction = 3;
            i--;
          } else
            j++;
        } else if (direction == 3) {
          if (i == 8 - n - 1) {
            direction = 4;
            n--;
            j--;
          } else
            i--;
        } else {
          if (j == 8 - n - 1) {
            direction = 1;
            n++;
            i++;
          } else
            j--;
        }
        k++;
      }
    });
  }

  /**
   * Ask the player to continue
   */
  @Override
  public void askContinue() {
    context.renderFrame(graphics -> {
      graphics.setColor(Color.BLACK);
      graphics.setFont(TEXT_FONT);
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

  /**
   * Rotate an image
   *
   * @param image (BufferedImage) the image
   * @param angle (double) the angle
   * @return (BufferedImage) the rotated image
   */
  private BufferedImage rotate(BufferedImage image, double angle) {
    Objects.requireNonNull(image);
    int w = image.getWidth();
    int h = image.getHeight();
    if (angle == 0) {
      return image;
    }
    double sin = Math.abs(Math.sin(Math.toRadians(angle)));
    double cos = Math.abs(Math.cos(Math.toRadians(angle)));
    double neww = Math.floor(w * cos + h * sin);
    double newh = Math.floor(h * cos + w * sin);
    BufferedImage rotated = new BufferedImage((int) neww, (int) newh, image.getType());
    Graphics2D g2d = rotated.createGraphics();
    AffineTransform at = new AffineTransform();
    at.translate((neww - w) / 2, (newh - h) / 2);
    int x = w / 2;
    int y = h / 2;
    at.rotate(Math.toRadians(angle), x, y);
    g2d.setTransform(at);
    g2d.drawImage(image, 0, 0, null);
    return rotated;
  }

  /**
   * Mirror an image
   *
   * @param image (BufferedImage) the image
   * @return (BufferedImage) the mirrored image
   */
  private BufferedImage mirror(BufferedImage image) {
    Objects.requireNonNull(image);
    AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
    tx.translate(-image.getWidth(null), 0);
    AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    image = op.filter(image, null);
    return image;
  }

  /**
   * Display the quilt board
   *
   * @param g          (Graphics2D) the graphics
   * @param quiltBoard (QuiltBoard) the quilt board
   * @param y          (float) the y coordinate
   */
  private void displayQuiltBoard(Graphics2D g, QuiltBoard quiltBoard, float y, int squareSize) {
    Objects.requireNonNull(g);
    Objects.requireNonNull(quiltBoard);
    float x = WIDTH / 2 - (float) quiltBoard.getWidth() * squareSize / 2;
    g.setColor(Color.BLACK);
    HashMap<Patch, int[]> patchPositions = quiltBoard.getPatchPositions();
    for (var patch : patchPositions.keySet()) {
      var path = Path.of("src/fr/uge/patchwork/display/assets/" + patch.imageId() + ".png");
      try {
        var image = ImageIO.read(path.toFile());
        image = rotate(image, patch.angle() * 90);
        if (patch.isFlipped()) {
          image = mirror(image);
        }
        int widthToDraw = patch.getWidth() * squareSize;
        int heightToDraw = patch.getHeight() * squareSize;
        g.drawImage(image, (int) x + patchPositions.get(patch)[0] * squareSize, (int) y + patchPositions.get(patch)[1] * 50, widthToDraw, heightToDraw, null);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    for (int i = 0; i < quiltBoard.getHeight(); i++) {
      for (int j = 0; j < quiltBoard.getWidth(); j++) {
        g.draw(new Rectangle2D.Float(x + i * squareSize, y + j * squareSize, squareSize, squareSize));
      }
    }
  }

  /**
   * Display the player
   *
   * @param player (Player) the player
   */
  @Override
  public void displayPlayer(Player player) {
    Objects.requireNonNull(player);
    context.renderFrame(graphics -> {
      clearScreen(graphics);
      drawCenteredTitleText(graphics, player.getName(), 100);
      graphics.setColor(Color.BLACK);
      graphics.setFont(new Font("Arial", Font.BOLD, 20));
      graphics.draw(new Rectangle2D.Float(50, 150, 200, 100));
      graphics.drawString("Money: " + player.getMoney(), 60, 180);
      graphics.drawString("Score: " + player.getScore(), 60, 210);
      graphics.drawString("Earnings: " + player.getEarnings(), 60, 240);
      displayQuiltBoard(graphics, player.getQuiltBoard(), 300, SQUARE_SIZE);
    });
  }

  /**
   * Ask for the placement of a patch
   * Use arrow keys to move, r to rotate, f to flip, enter to place
   *
   * @param player  (Player) the player
   * @param patch   (Patch) the patch
   * @param x       (int) the x coordinate
   * @param y       (int) the y coordinate
   * @param message (String) the message
   * @return (Map < Patch, int[] >) the patch positions
   */
  private Map<Patch, int[]> askPlacement(Player player, Patch patch, int x, int y, String message) {
    Objects.requireNonNull(player);
    Objects.requireNonNull(patch);
    Patch finalPatch = patch;
    int finalY = y;
    int finalX = x;
    context.renderFrame(graphics -> {
      clearScreen(graphics);
      drawCenteredTitleText((graphics), "Place your patch", HEIGHT / 2 - HEIGHT / 4);
      drawCenteredSubTitleText((graphics), "Use arrow keys to move, r to rotate, f to flip, enter to place", HEIGHT / 2 - HEIGHT / 4 + 50);
      QuiltBoard copy = player.getQuiltBoard().copy();
      copy.addPatch(finalPatch, finalX, finalY);
      displayQuiltBoard(graphics, copy, HEIGHT / 2 - HEIGHT / 4 + 100, SQUARE_SIZE);
      graphics.setColor(Color.BLACK);
      graphics.setFont(TEXT_FONT);
      graphics.drawString(message, WIDTH / 2 - WIDTH / 4, HEIGHT / 2 - HEIGHT / 8);
    });
    for (; ; ) {
      Event event = context.pollOrWaitEvent(10);
      if (event == null) {
        continue;
      }
      if (event.getAction() == Action.KEY_PRESSED) {
        return switch (event.getKey()) {
          case R -> {
            patch = patch.rotate();
            yield askPlacement(player, patch, x, y, "");
          }
          case F -> {
            patch = patch.flip();
            yield askPlacement(player, patch, x, y, "");
          }
          case Z, UP -> {
            if (y > 0) {
              y--;
            }
            yield askPlacement(player, patch, x, y, "");
          }
          case S, DOWN -> {
            if (y < player.getQuiltBoard().getHeight() - patch.getHeight()) {
              y++;
            }
            yield askPlacement(player, patch, x, y, "");
          }
          case Q, LEFT -> {
            if (x > 0) {
              x--;
            }
            yield askPlacement(player, patch, x, y, "");
          }
          case D, RIGHT -> {
            if (x < player.getQuiltBoard().getWidth() - patch.getWidth()) {
              x++;
            }
            yield askPlacement(player, patch, x, y, "");
          }
          case UNDEFINED -> {
            if (player.getQuiltBoard().isValidPlacement(patch, x, y)) {
              Map<Patch, int[]> map = new HashMap<>();
              map.put(patch, new int[]{x, y});
              yield map;
            } else {
              yield askPlacement(player, patch, x, y, "Invalid placement");
            }
          }
          default -> askPlacement(player, patch, x, y, "");
        };

      }
    }
  }

  /**
   * Print the next nbPatches patches
   *
   * @param circlePatches (CirclePatches) the circle patches
   * @param nbPatch       (int) the number of patches to print
   * @param title         (String) the title
   * @param message       (String) the message
   */
  private void printNextPatches(CirclePatches circlePatches, int nbPatch, String title, String message) {
    Objects.requireNonNull(circlePatches);
    context.renderFrame(graphics -> {
      clearScreen(graphics);
      drawCenteredTitleText(graphics, title, 100);
      drawCenteredSubTitleText(graphics, message, 150);
      int j = 0;
      for (var patch : circlePatches.getNextPatches(nbPatch)) {
        var path = Path.of("src/fr/uge/patchwork/display/assets/" + patch.imageId() + ".png");
        try {
          var image = ImageIO.read(path.toFile());
          int width = image.getWidth();
          int height = image.getHeight();
          graphics.drawImage(image, (int) WIDTH / 2 - (int) WIDTH / 4 + j * (int) WIDTH / 6, 200, width / 4, height / 4, null);
          j++;
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Print all the patches of the circle patches in a circle
   *
   * @param circlePatches (CirclePatches) the circle patches
   */
  private void printAllPatches(CirclePatches circlePatches) {
    Objects.requireNonNull(circlePatches);
    context.renderFrame(graphics -> {
      clearScreen(graphics);
      drawCenteredTitleText(graphics, "All patches", 100);
      drawCenteredSubTitleText(graphics, "The patches are displayed on a circle clockwise. The red dot represents the neutral token", 150);
      List<Patch> patches = circlePatches.getPatches();
      float x = WIDTH / 2;
      float y = HEIGHT / 2;
      float radius = 300;
      float angle = 0;
      float angleStep = (float) (2 * Math.PI / patches.size());
      for (var patch : patches) {
        var path = Path.of("src/fr/uge/patchwork/display/assets/" + patch.imageId() + ".png");
        try {
          var image = ImageIO.read(path.toFile());
          int width = image.getWidth();
          int height = image.getHeight();
          graphics.drawImage(image, (int) (x + radius * Math.cos(angle)) - width / 10, (int) (y + radius * Math.sin(angle)) - height / 10, width / 10, height / 10, null);
          angle += angleStep;
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      int neutral = circlePatches.getNeutralToken() - 1;
      graphics.setColor(Color.RED);
      graphics.fillOval((int) (x + radius * Math.cos(angleStep * neutral)) - 10, (int) (y + radius * Math.sin(angleStep * neutral)) - 10, 10, 10);
    });
  }

  /**
   * Draw actions on the screen
   *
   * @param player  (Player) the player
   * @param options (List<Action>) the actions
   */
  private void drawActions(Player player, List<String> options) {
    Objects.requireNonNull(player);
    Objects.requireNonNull(options);
    context.renderFrame(graphics -> {
      drawCenteredTitleText(graphics, player.getName() + " choose an action", HEIGHT / 2);
      for (int j = 0; j < options.size(); j++) {
        graphics.setColor(Color.WHITE);
        graphics.fill(new Rectangle2D.Float(WIDTH / 2 - 100, HEIGHT / 2 + SQUARE_SIZE + j * SQUARE_SIZE, 200, 40));
        graphics.setColor(Color.BLACK);
        graphics.draw(new Rectangle2D.Float(WIDTH / 2 - 100, HEIGHT / 2 + SQUARE_SIZE + j * SQUARE_SIZE, 200, 40));
        graphics.drawString(options.get(j), WIDTH / 2 - 100 + 10, HEIGHT / 2 + SQUARE_SIZE + j * SQUARE_SIZE + 30);
      }
      displayQuiltBoard(graphics, player.getQuiltBoard(), HEIGHT / 2 + SQUARE_SIZE + options.size() * SQUARE_SIZE + 30, 20);
      graphics.setColor(Color.WHITE);
      graphics.fill(new Rectangle2D.Float(WIDTH - 100, HEIGHT / 2 + SQUARE_SIZE, 100, 40));
      graphics.setColor(Color.BLACK);
      graphics.draw(new Rectangle2D.Float(WIDTH - 100, HEIGHT / 2 + SQUARE_SIZE, 100, 40));
      graphics.drawString("Patches", WIDTH - 100 + 10, HEIGHT / 2 + SQUARE_SIZE + 30);
    });
  }

  /**
   * Ask the player to choose an action based on a list of options
   *
   * @param player        (Player) the player
   * @param circlePatches (CirclePatches) the circle of patches
   * @param nbPatch       (int) the number of patches to display
   * @param options       (List < String >) the list of options
   * @return (Optional < Map < Patch, int[] > >) the action chosen by the player
   * (empty if the player passed)
   * (the patch and the position chosen by the player if the player bought a patch)
   */
  @Override
  public Optional<Map<Patch, int[]>> chooseAction(Player player, CirclePatches circlePatches, int nbPatch, List<String> options) {
    Objects.requireNonNull(player);
    Objects.requireNonNull(circlePatches);
    Objects.requireNonNull(options);
    int numberOfPatch = circlePatches.getNbPatches(nbPatch);
    Patch chosenPatch = null;
    printNextPatches(circlePatches, nbPatch, "Next " + numberOfPatch + " patches", "You have " + player.getMoney() + " money");
    drawActions(player, options);
    int boughtPatch = 0;
    for (; ; ) {
      Event event = context.pollOrWaitEvent(10);
      if (event == null) {
        continue;
      }
      if (event.getAction() == Action.POINTER_UP) {
        Point2D.Float location = event.getLocation();
        for (int i = 0; i < 2; i++) {
          if (location.x > WIDTH / 2 - 100 && location.x < WIDTH / 2 + 100 && location.y > HEIGHT / 2 + SQUARE_SIZE + i * SQUARE_SIZE && location.y < HEIGHT / 2 + 90 + i * SQUARE_SIZE) {
            if (i == 0) {
              if (circlePatches.isEmpty() || options.size() == 1) {
                return Optional.empty();
              }
              boughtPatch = 1;

            } else {
              return Optional.empty();
            }
          }
        }
        if (location.x > WIDTH - 100 && location.x < WIDTH && location.y > HEIGHT / 2 + SQUARE_SIZE && location.y < HEIGHT / 2 + SQUARE_SIZE + 40) {
          printAllPatches(circlePatches);
          askContinue();
          return chooseAction(player, circlePatches, nbPatch, options);
        }
        if (boughtPatch == 1) {
          break;
        }
      }
    }

    printNextPatches(circlePatches, numberOfPatch, "Choose a patch", "You have " + player.getMoney() + " money");
    context.renderFrame(graphics -> displayQuiltBoard(graphics, player.getQuiltBoard(), HEIGHT / 2 - 100, SQUARE_SIZE));
    String result = null;

    for (; ; ) {
      Event event1 = context.pollOrWaitEvent(10);
      if (event1 == null) {
        continue;
      }
      if (event1.getAction() == Action.POINTER_UP) {
        Point2D.Float location1 = event1.getLocation();
        for (int k = 0; k < numberOfPatch; k++) {
          if (location1.x > WIDTH / 2 - WIDTH / 4 + k * WIDTH / 6 && location1.x < WIDTH / 2 - WIDTH / 4 + k * WIDTH / 6 + WIDTH / 6 && location1.y > 200 && location1.y < 200 + HEIGHT / 6) {
            chosenPatch = circlePatches.getNextPatches(numberOfPatch).get(k);
            result = player.checkPatchChoice(circlePatches, numberOfPatch, k + 1);
            if (result != null) {
              printNextPatches(circlePatches, numberOfPatch, "Choose a patch", result);
              context.renderFrame(graphics -> displayQuiltBoard(graphics, player.getQuiltBoard(), HEIGHT / 2 - 100, SQUARE_SIZE));
            } else {
              break;
            }
          }
        }
        if (chosenPatch != null && result == null) {
          break;
        }
      }
    }
    int[] coords = player.getQuiltBoard().getFirstValidPosition(chosenPatch);
    if (coords == null) {
      return Optional.empty();
    }
    var map = askPlacement(player, chosenPatch, coords[0], coords[1], "");
    return Optional.of(map);
  }

  /**
   * Display the button found message
   */
  @Override
  public void buttonFound() {
    context.renderFrame(graphics -> {
      drawCenteredTitleText(graphics, "You found a button!", 150);
      drawCenteredSubTitleText(graphics, "You will earn 1 button for each buttons of your quilt board", 200);
    });
  }

  /**
   * Display the leather patch found message and ask the player to place it
   *
   * @param player       (Player) the player
   * @param leatherPatch (LeatherPatch) the leather patch
   * @return (Map < Patch, int[] >) the patch and the position chosen by the player
   */
  @Override
  public Map<Patch, int[]> leatherPatchFound(Player player, Patch leatherPatch) {
    context.renderFrame(graphics -> {
      drawCenteredSubTitleText(graphics, "You found a leather patch!", 150);
      drawCenteredTitleText(graphics, "You can place it on your board for free", 200);
    });
    context.pollOrWaitEvent(1000);
    int[] coords = player.getQuiltBoard().getFirstValidPosition(leatherPatch);
    if (coords == null) {
      return null;
    }
    return askPlacement(player, leatherPatch, coords[0], coords[1], "Place the leather patch");
  }

  /**
   * Display the player after a move
   *
   * @param player (Player) the player
   */
  @Override
  public void displayPlayerAfterMove(Player player) {
    displayPlayer(player);
  }

  /**
   * Display the next player
   *
   * @param player (Player) the player
   */
  @Override
  public void printNextPlayer(Player player) {
    context.renderFrame(graphics -> drawCenteredSubTitleText(graphics, "Next player is " + player.getName(), 150));
  }

  /**
   * Display the special tile found message
   *
   * @param player      (Player) the player
   * @param specialTile (SpecialTile) the special tile
   */
  @Override
  public void specialTileFound(Player player, SpecialTile specialTile) {
    context.renderFrame(graphics -> drawCenteredSubTitleText(graphics, "You found a special tile of " + specialTile.getSize() + "x" + specialTile.getSize() + "!", HEIGHT - 200));
  }

  /**
   * Display the winner
   *
   * @param player (Player) the player
   */
  @Override
  public void displayWinner(Player player) {
    context.renderFrame(graphics -> {
      clearScreen(graphics);
      drawCenteredTitleText(graphics, player.getName() + " won the game!", HEIGHT / 2);
    });
    askContinue();
    context.exit(0);
  }
}

