package patchwork.game;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Objects;

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
   *
   * @return (int) the number of '*' on the patch
   */
  public int getNumberOfBlocks() {
    return (int) format.chars().filter(c -> c == '*').count();
  }

  /**
   * get the format of the patch in a 2D array.
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

  public Patch rotate() {
    var newFormat = new StringBuilder();
    var lines = format.split(",");
    var maxLen = maxLenLinePiece();
    for (int i = 0; i < maxLen; i++) {
      newFormat.append(getColumn(lines, i));
      newFormat.append(',');
    }
    return new Patch(newFormat.toString(), price, forwardBlocks, earnings);
  }

  private String getColumn(String[] lines, int colIndex) {
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

  public Patch flip() {
    var newFormat = new StringBuilder();
    var lines = format.split(",");
    var maxLen = maxLenLinePiece();
    for (int i = 0; i < lines.length; i++) {
      newFormat.append(getFlippedLine(lines[i], maxLen));
      newFormat.append(",");
    }
    return new Patch(newFormat.toString(), price, forwardBlocks, earnings);
  }

  private String getFlippedLine(String line, int maxLen) {
    StringBuilder flippedLine = new StringBuilder();
    if (line.length() < maxLen) {
      for (int j = 0; j < maxLen - line.length(); j++) {
        flippedLine.append(" ");
      }
    }
    for (int j = line.length() - 1; j >= 0; j--) {
      flippedLine.append(line.charAt(j));
    }
    return flippedLine.toString();
  }

  public int getHeight() {
    return format.split(",").length;
  }

  public int getWidth() {
    return maxLenLinePiece();
  }

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
