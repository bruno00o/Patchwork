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
