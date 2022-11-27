package fr.uge.patchwork;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

public class Pieces {
  private final ArrayList<Piece> pieces;

  public Pieces() {
    pieces = new ArrayList<>();
  }

  public Piece get(int index) {
    return pieces.get(index);
  }

  public int getId(int index) {
    return pieces.get(index).id();
  }

  public void deletePiece(int index) {
    pieces.remove(index - 1);
  }

  public String nextThree(int index) {
    var builder = new StringBuilder();
    for (var i = index; i < index + 3; i++) {
      builder.append("N°").append(i+1).append("\n").append(pieces.get(i)).append("\n");
    }
    return builder.toString();
  }

  public void load() {
    int id = 0;
    try (var reader = Files.newBufferedReader(Path.of("pieces/pieces.txt"))) {
      String line;
      while ((line = reader.readLine()) != null) {
        var tokens = line.split(":");
        var piece = new Piece(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), tokens[3], id);
        pieces.add(piece);
        id++;
      }
      Collections.shuffle(pieces);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public String toString() {
    return pieces.toString();
  }

}
