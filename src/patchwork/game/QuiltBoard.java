package patchwork.game;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Stream;

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

  public boolean canAddPatch(Patch patch) {
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        if (quiltBoard.get(i).get(j).isEmpty()) {
          if (isValidPlacement(patch, i, j)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  public boolean isValidPlacement(Patch patch, int i, int j) {
    if (i + patch.getHeight() > height || j + patch.getWidth() > width) {
      return false;
    }
    for (int k = 0; k < patch.getHeight(); k++) {
      for (int l = 0; l < patch.getWidth(); l++) {
        if (patch.isSquareFilled(k, l) && !quiltBoard.get(j + k).get(i + l).isEmpty()) {
          return false;
        }
      }
    }
    return true;
  }

  public boolean canAddPatches(ArrayList<Patch> patches) {
    for (Patch patch : patches) {
      if (canAddPatch(patch)) {
        return true;
      }
    }
    return false;
  }

  public void addPatch(Patch patch, int x, int y) {
    if (!isValidPlacement(patch, x, y)) {
      throw new IllegalArgumentException("Patch can't be added to the quilt board");
    }
    for (int i = 0; i <= patch.getHeight(); i++) {
      for (int j = 0; j <= patch.getWidth(); j++) {
        if (patch.isSquareFilled(i, j)) {
          quiltBoard.get(x + j).set(y + i, new QuiltSquare(true, patch));
        }
      }
    }
  }

  public boolean containsSquare(int squareSize) {
    return quiltBoard.stream()
            .flatMap(ArrayList::stream)
            .filter(QuiltSquare::isFilled)
            .count() == squareSize * squareSize;
  }

  public int nbEmptySquares() {
    return quiltBoard.stream()
            .flatMap(ArrayList::stream)
            .filter(QuiltSquare::isEmpty)
            .mapToInt(i -> 1)
            .sum();
  }


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

  public QuiltBoard copy() {
    QuiltBoard copy = new QuiltBoard(width, height);
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        copy.quiltBoard.get(i).set(j, quiltBoard.get(i).get(j).copy());
      }
    }
    return copy;
  }

  public int getHeight() {
    return height;
  }

  public int getWidth() {
    return width;
  }
}
