package fr.uge.patchwork;

import java.util.Objects;

public record Piece(int price, int blocks, int earnings, String format, int id) {

  public Piece {
    Objects.requireNonNull(format);
    Objects.requireNonNull(price);
    Objects.requireNonNull(blocks);
    Objects.requireNonNull(earnings);
    Objects.requireNonNull(id);

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

  public int getEarnings() {
    return earnings;
  }

  public int getBlocks() {
    return blocks;
  }

  public int getPrice() {
    return price;
  }

  public String getFormat() {
    return format;
  }

  @Override
  public String toString() {

    var tokens = format.split(",");
    var builder = new StringBuilder();

    for (var i = 0; i < tokens.length; i++) {
      builder.append(tokens[i]).append("\n");
    }
    
    return "price : " + price + ",\nblocks : " + blocks +
            ",\nearnings : " + earnings + ",\nformat : \n" + builder.toString();
  }

}
