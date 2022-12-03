package fr.uge.patchwork;

import java.util.Objects;

/**
 * A patchwork is a set of rectangles that can be placed on a grid.
 * @param format (String) the format of the patch
 * @param price (int) the price of the patch
 * @param forwardBlocks (int) the number of blocks in the forward direction
 * @param earnings (int) the earnings of the patch
 */
public record Patch(String format, int price, int forwardBlocks, int earnings) {
  public Patch {
    Objects.requireNonNull(format);
    if (price < 0) {
      throw new IllegalArgumentException("Price must be positive");
    }
    if (forwardBlocks < 0) {
      throw new IllegalArgumentException("Forward blocks must be positive");
    }
    if (earnings < 0) {
      throw new IllegalArgumentException("Earnings must be positive");
    }
  }

  /**
   * return the number of blocks in the patch.
   * @return (int) the number of '*' on the patch
   */
  public int getNumberOfBlocks() {
    int count = 0;
    for (int i = 0; i < format.length(); i++) {
      if (format.charAt(i) == '*') {
        count++;
      }
    }
    return count;
  }

  /**
   * get the format of the patch in a 2D array.
   * @param line (int) the number of lines of the patch
   * @return (String) the format of the patch
   */
  public String getFormatLine(int line) {
    var lines = format.replace(".", " ").split(",");
    return lines[line];
  }

  /**
   * Max length of the patch.
   * @return (int) the max length of the patch
   */
  private int maxLenLinePiece() {
    var lines = format.split(",");
    int max = 0;
    for (var line : lines) {
      if (line.length() > max) {
        max = line.length();
      }
    }
    return max;
  }

  /**
   * rotate the patch 90°
   * @return (Patch) the patch rotated
   */
  public Patch rotatePiece() {
    var newFormat = new StringBuilder();
    var lines = format.split(",");
    var maxLen = maxLenLinePiece();
    for (int i = 0; i < maxLen; i++) {
      for (int j = lines.length - 1; j >= 0; j--) {
        if (i < lines[j].length()) {
          newFormat.append(lines[j].charAt(i));
        } else {
          newFormat.append(' ');
        }
      }
      newFormat.append(',');
    }
    return new Patch(newFormat.toString(), price, forwardBlocks, earnings);
  }

  /**
   * flip the patch horizontally
   * @return (Patch) the patch flipped
   */
  public Patch flipPiece() {
    var newFormat = new StringBuilder();
    var lines = format.split(",");
    var maxLen = maxLenLinePiece();
    for (int i = 0; i < lines.length; i++) {
      if (lines[i].length() < maxLen) {
        for (int j = 0; j < maxLen - lines[i].length(); j++) {
          newFormat.append(" ");
        }
      }
      for (int j = lines[i].length() - 1; j >= 0; j--) {
        newFormat.append(lines[i].charAt(j));
      }
      newFormat.append(",");
    }
    return new Patch(newFormat.toString(), price, forwardBlocks, earnings);
  }

  /**
   * Get height of the patch
   * @return the number of lines in the format
   */
  public int getHeight() {
    return format.split(",").length;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Price: ").append(price).append(", Earnings: ").append(earnings).append(", Forward blocks: ").append(forwardBlocks).append("\nFormat:\n");
    var lines = format.replace(".", " ").split(",");
    for (var line : lines) {
      sb.append(line).append("\n");
    }
    return sb.toString();
  }
}

