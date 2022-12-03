package fr.uge.patchwork;

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

  public int getNumberOfBlocks() {
    // count '*' in format use streams
    int count = 0;
    for (int i = 0; i < format.length(); i++) {
      if (format.charAt(i) == '*') {
        count++;
      }
    }
    return count;
  }

  public String getFormatLine(int line) {
    var lines = format.split(",");
    return lines[line];
  }

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

  public int getHeight() {
    return format.split(",").length;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Price: ").append(price).append(", Earnings: ").append(earnings).append(", Forward blocks: ").append(forwardBlocks).append("\nFormat:\n");
    var lines = format.split(",");
    for (var line : lines) {
      sb.append(line).append("\n");
    }
    return sb.toString();
  }
}

