package fr.uge.patchwork;

import java.util.Objects;

public record Piece(int price, int blocks, int earnings, String format, int id) {

  public Piece {
    Objects.requireNonNull(format);

    if (price < 0) {
      throw new IllegalArgumentException("Price must be positive");
    }
    if (blocks < 0) {
      throw new IllegalArgumentException("Blocks must be positive");
    }
    if (earnings < 0) {
      throw new IllegalArgumentException("Earnings must be positive");
    }
    if (format == null) {
      throw new IllegalArgumentException("Format must not be null");
    }
  }

  /**
   * Return the maximum length of the piece
   *
   * @return
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
   * Rotate the piece 90° clockwise
   *
   * @return
   */
  public Piece rotate90() {
    var newFormat = new StringBuilder();
    var lines = format.split(",");
    var maxLen = maxLenLinePiece();
    for (int i = 0; i < maxLen; i++) {
      for (int j = lines.length - 1; j >= 0; j--) {
        if (i < lines[j].length()) {
          newFormat.append(lines[j].charAt(i));
        } else {
          newFormat.append(" ");
        }
      }
      newFormat.append(",");
    }
    return new Piece(price, blocks, earnings, newFormat.toString(), id);
  }

  /**
   * Flip the piece
   *
   * @return
   */
  public Piece flip() {
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
    return new Piece(price, blocks, earnings, newFormat.toString(), id);
  }

  @Override
  public String toString() {

    var tokens = format.split(",");
    var builder = new StringBuilder();

    for (var i = 0; i < tokens.length; i++) {
      builder.append(tokens[i]).append("\n");
    }

    return "id : " + id + "\nprice : " + price + ",\nblocks : " + blocks +
            ",\nearnings : " + earnings + ",\nformat : \n" + builder.toString();
  }

}
