package fr.uge.patchwork.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Class for a quilt board
 */
public class QuiltBoard {
  private final int width;
  private final int height;
  private final ArrayList<ArrayList<QuiltSquare>> quiltBoard;

  /**
   * Init a quilt board
   *
   * @param width  (int) width of the quilt board
   * @param height (int) height of the quilt board
   */
  public QuiltBoard(int width, int height) {
    if (width < 0 || height < 0) {
      throw new IllegalArgumentException("Width and height must be positive");
    }
    this.width = width;
    this.height = height;
    this.quiltBoard = new ArrayList<>();
    for (int i = 0; i < height; i++) {
      ArrayList<QuiltSquare> row = new ArrayList<>();
      for (int j = 0; j < width; j++) {
        row.add(new QuiltSquare(false, null));
      }
      this.quiltBoard.add(row);
    }
  }


  /**
   * Return true if the patch can be placed on the quilt board
   *
   * @param patch (Patch) patch to place
   * @return (boolean)
   */
  public boolean canAddPatch(Patch patch) {
    Objects.requireNonNull(patch);
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 2; j++) {
        for (int k = 0; k < height; k++) {
          for (int l = 0; l < width; l++) {
            if (isValidPlacement(patch, k, l)) {
              return true;
            }
          }
        }
        patch = patch.flip();
      }
      patch = patch.rotate();
    }
    return false;
  }

  /**
   * Return the coordinates of the first valid placement of the patch
   * Return null if there is no valid placement
   *
   * @param patch (Patch) patch to place
   * @return (int[]) coordinates of the first valid placement
   */
  public int[] getFirstValidPosition(Patch patch) {
    Objects.requireNonNull(patch);
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 2; j++) {
        for (int k = 0; k < height; k++) {
          for (int l = 0; l < width; l++) {
            if (isValidPlacement(patch, k, l)) {
              return new int[]{k, l};
            }
          }
        }
        patch = patch.flip();
      }
      patch = patch.rotate();
    }
    return null;
  }

  /**
   * Return true if the patch can be placed on the quilt board at the given coordinates
   *
   * @param patch (Patch) patch to place
   * @param x     (int) x coordinate
   * @param y     (int) y coordinate
   * @return (boolean)
   */
  public boolean isValidPlacement(Patch patch, int x, int y) {
    Objects.requireNonNull(patch);
    for (int i = 0; i <= patch.getHeight(); i++) {
      for (int j = 0; j <= patch.getWidth(); j++) {
        if (patch.isSquareFilled(i, j)) {
          if (x + j >= width || y + i >= height) {
            return false;
          }
          if (!quiltBoard.get(x + j).get(y + i).isEmpty()) {
            return false;
          }
        }
      }
    }
    return true;
  }

  /**
   * Return true if one patch in the list can be placed on the quilt board
   * Return false if every patch in the list can't be placed on the quilt board
   *
   * @param patches (List<Patch>) list of patches to place
   * @param player  (Player) player who wants to place the patches
   * @return (boolean)
   */
  public boolean canAddPatches(List<Patch> patches, Player player) {
    Objects.requireNonNull(patches);
    Objects.requireNonNull(player);
    for (Patch patch : patches) {
      if (canAddPatch(patch) && patch.price() <= player.getMoney()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Place the patch on the quilt board at the given coordinates
   *
   * @param patch (Patch) patch to place
   * @param x     (int) x coordinate
   * @param y     (int) y coordinate
   */
  public void addPatch(Patch patch, int x, int y) {
    Objects.requireNonNull(patch);
    patch = patch.setCoords(x, y);
    for (int i = 0; i <= patch.getHeight(); i++) {
      for (int j = 0; j <= patch.getWidth(); j++) {
        if (patch.isSquareFilled(i, j) && x + j < width && y + i < height) {
          quiltBoard.get(x + j).set(y + i, new QuiltSquare(true, patch));
        }
      }
    }
  }

  /**
   * Return true if the quilt board contains a square of squareSize x squareSize with all squares filled
   *
   * @param squareSize (int) size of the square
   * @return (boolean)
   */
  public boolean containsSquare(int squareSize) {
    if (squareSize <= 0 || squareSize > 9) {
      throw new IllegalArgumentException("squareSize must be between 1 and 9 inclusive");
    }
    for (int i = 0; i <= 9 - squareSize; i++) {
      for (int j = 0; j <= 9 - squareSize; j++) {
        boolean squareFound = true;
        for (int k = i; k < i + squareSize; k++) {
          for (int l = j; l < j + squareSize; l++) {
            if (!quiltBoard.get(k).get(l).isFilled()) {
              squareFound = false;
              break;
            }
          }
          if (!squareFound) {
            break;
          }
        }
        if (squareFound) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Return the number of empty squares
   *
   * @return (int)
   */
  public int nbEmptySquares() {
    return quiltBoard.stream()
            .flatMap(ArrayList::stream)
            .filter(QuiltSquare::isEmpty)
            .mapToInt(i -> 1)
            .sum();
  }

  /**
   * Return a map of all patches on the quilt board with their coordinates (top left corner)
   *
   * @return (Map < Patch, int[] >) map of patches and their coordinates
   */
  public HashMap<Patch, int[]> getPatchPositions() {
    HashMap<Patch, int[]> patchPositions = new HashMap<>();
    List<Patch> patches = getAllPatches();
    for (Patch patch : patches) {
      if (!patchPositions.containsKey(patch)) {
        patchPositions.put(patch, new int[]{patch.x(), patch.y()});
      }
    }
    return patchPositions;
  }

  /**
   * Return all patches on the quilt board
   *
   * @return (List < Patch >) list of patches
   */
  public List<Patch> getAllPatches() {
    return quiltBoard.stream()
            .flatMap(ArrayList::stream)
            .filter(QuiltSquare::isFilled)
            .map(QuiltSquare::getPatch)
            .toList();
  }

  /**
   * Return a string representation of the quilt board
   *
   * @return (String)
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(" \\  0 1 2 3 4 5 6 7 8\n");
    sb.append("  \\ _ _ _ _ _ _ _ _ _\n");
    for (int i = 0; i < height; i++) {
      sb.append(i).append(" | ");
      for (int j = 0; j < width; j++) {
        sb.append(quiltBoard.get(j).get(i).toString()).append(" ");
      }
      sb.append("\n");
    }
    return sb.toString();
  }

  /**
   * Return a copy of the quilt board
   * @return (QuiltBoard)
   */
  public QuiltBoard copy() {
    QuiltBoard copy = new QuiltBoard(width, height);
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        copy.quiltBoard.get(i).set(j, quiltBoard.get(i).get(j).copy());
      }
    }
    return copy;
  }

  /**
   * Return the height of the quilt board
   * @return (int)
   */
  public int getHeight() {
    return height;
  }

  
  public int getWidth() {
    return width;
  }
}
