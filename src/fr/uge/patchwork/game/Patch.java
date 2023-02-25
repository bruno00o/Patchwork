package fr.uge.patchwork.game;

import java.util.Arrays;
import java.util.Objects;

/**
 * Record for a patch
 *
 * @param id            (int) id of the patch (unique, defined in the file)
 * @param format        (String) format of the patch (defined in the file)
 * @param price         (int) price of the patch (defined in the file)
 * @param forwardBlocks (int) number of forward blocks (defined in the file)
 * @param earnings      (int) earnings of the patch (defined in the file)
 * @param angle         (int) define it to 0 by default
 * @param isFlipped     (boolean) define it to false by default
 * @param x             (int) define it to -1 by default
 * @param y             (int) define it to -1 by default
 * @param imageId       (int) id of the image (defined in the file)
 */
public record Patch(int id, String format, int price, int forwardBlocks, int earnings, int angle, boolean isFlipped,
                    int x, int y, int imageId) {
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
    if (angle < 0 || angle > 3) {
      throw new IllegalArgumentException("Angle must be between 0 and 3");
    }
  }

  /**
   * Set the position of the patch and return a new patch
   *
   * @param x (int) x position
   * @param y (int) y position
   * @return (Patch) new patch with the new position set
   */
  public Patch setCoords(int x, int y) {
    return new Patch(id, format, price, forwardBlocks, earnings, angle, isFlipped, x, y, imageId);
  }

  /**
   * Return the number of blocks of the patch
   *
   * @return (int) the number of '*' on the patch
   */
  public int getNumberOfBlocks() {
    return (int) format.chars().filter(c -> c == '*').count();
  }

  /**
   * Get the format of the patch in a 2D array.
   *
   * @param line (int) the number of lines of the patch
   * @return (String) the format of the patch
   */
  public String getFormatLine(int line) {
    var lines = format.replace(".", " ").split(",");
    return lines[line];
  }

  /**
   * Max length of the patch.
   *
   * @return (int) the max length of the patch
   */
  private int maxLenLinePiece() {
    return Arrays.stream(format.split(",")).mapToInt(String::length).max().orElse(0);
  }

  /**
   * Rotate the patch and return a new patch
   *
   * @return (Patch) new patch with the new angle set
   */
  public Patch rotate() {
    var newFormat = new StringBuilder();
    var lines = format.split(",");
    var maxLen = maxLenLinePiece();
    for (int i = 0; i < maxLen; i++) {
      newFormat.append(getColumn(lines, i));
      newFormat.append(',');
    }
    return new Patch(id, newFormat.toString(), price, forwardBlocks, earnings, (angle + 1) % 4, isFlipped, x, y, imageId);
  }

  /**
   * Get the column of the patch
   *
   * @param lines    (String[]) the lines of the patch
   * @param colIndex (int) the index of the column
   * @return (String) the column of the patch
   */
  private String getColumn(String[] lines, int colIndex) {
    Objects.requireNonNull(lines);
    StringBuilder column = new StringBuilder();
    for (int j = lines.length - 1; j >= 0; j--) {
      if (colIndex < lines[j].length()) {
        column.append(lines[j].charAt(colIndex));
      } else {
        column.append(' ');
      }
    }
    return column.toString();
  }

  /**
   * Flip the patch and return a new patch
   *
   * @return (Patch) new patch with the new isFlipped set
   */
  public Patch flip() {
    var newFormat = new StringBuilder();
    var lines = format.split(",");
    var maxLen = maxLenLinePiece();
    for (var line : lines) {
      newFormat.append(getFlippedLine(line, maxLen));
      newFormat.append(',');
    }
    return new Patch(id, newFormat.toString(), price, forwardBlocks, earnings, angle, !isFlipped, x, y, imageId);
  }

  /**
   * Get the flipped line of the patch
   *
   * @param line   (String) the line of the patch
   * @param maxLen (int) the max length of the patch
   * @return (String) the flipped line
   */
  private String getFlippedLine(String line, int maxLen) {
    Objects.requireNonNull(line);
    StringBuilder flippedLine = new StringBuilder();
    if (line.length() < maxLen) {
      flippedLine.append(" ".repeat(maxLen - line.length()));
    }
    for (int j = line.length() - 1; j >= 0; j--) {
      flippedLine.append(line.charAt(j));
    }
    return flippedLine.toString();
  }

  /**
   * Return the number of blocks of the patch
   *
   * @return (int) the height of the patch
   */
  public int getHeight() {
    return format.split(",").length;
  }

  /**
   * Return the number of blocks of the patch
   *
   * @return (int) the width of the patch
   */
  public int getWidth() {
    return maxLenLinePiece();
  }

  /**
   * Return true if the patch is filled at the position (x, y) of the patch
   *
   * @param x (int) x position
   * @param y (int) y position
   * @return (boolean)
   */
  public boolean isSquareFilled(int x, int y) {
    var lines = format.split(",");
    if (x >= lines.length) {
      return false;
    }
    var line = lines[x];
    if (y >= line.length()) {
      return false;
    }
    return line.charAt(y) == '*';
  }


  @Override
  public int hashCode() {
    return Objects.hash(id, format, price, forwardBlocks, earnings, angle, isFlipped, x, y, imageId);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Patch other = (Patch) obj;
    return id == other.id;
  }

  /**
   * Return the string representation of the patch
   *
   * @return (String)
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Price: ")
            .append(price)
            .append(", Earnings: ")
            .append(earnings)
            .append(", Forward blocks: ")
            .append(forwardBlocks)
            .append("\nFormat:\n");
    var lines = format.replace(".", " ").split(",");
    for (var line : lines) {
      sb.append(line).append("\n");
    }
    return sb.toString();
  }
}