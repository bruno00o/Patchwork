package fr.uge.patchwork;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CirclePatches {
  private final ArrayList<Patch> patches;
  private int neutralToken;

  public CirclePatches() {
    this.patches = new ArrayList<>();
    this.neutralToken = 0;
  }

  public void load(Path path) throws IOException {
    Objects.requireNonNull(path);
    try (var reader = Files.newBufferedReader(path)) {
      String line;
      while ((line = reader.readLine()) != null) {
        var tokens = line.split(":");
        var patch = new Patch(tokens[3], Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));
        patches.add(patch);
      }
    }
  }

  public void shuffle() {
    Collections.shuffle(patches);
  }

  private int smallestPatchIndex() {
    int smallestIndex = 0;
    for (int i = 1; i < patches.size(); i++) {
      if (patches.get(i).getNumberOfBlocks() < patches.get(smallestIndex).getNumberOfBlocks()) {
        smallestIndex = i;
      }
    }
    return smallestIndex;
//      int smallestPatch = patches.stream().mapToInt(Patch::getNumberOfBlocks).min().orElseThrow();
//      return patches.indexOf(patches.stream().filter(patch -> patch.getNumberOfBlocks() == smallestPatch).findFirst().orElseThrow());
  }

  public void placeNeutralToken() {
    int smallestPatchIndex = smallestPatchIndex();
    if (smallestPatchIndex == patches.size() - 1) {
      neutralToken = 0;
    } else {
      neutralToken = smallestPatchIndex + 1;
    }
  }

  public List<Patch> getNextPatches(int n) {
    var limit = n;
    if (n > patches.size()) {
      limit = patches.size();
    }
    return Stream.concat(patches.stream().skip(neutralToken), patches.stream().limit(neutralToken + 1))
            .limit(limit)
            .collect(Collectors.toList());
  }

  public static int getMaxHeight(List<Patch> patches) {
    return patches.stream().mapToInt(Patch::getHeight).max().orElseThrow();
  }

  public String indexLineString(int numberOfPatches) {
    StringBuilder sb = new StringBuilder();
    sb.append("-".repeat(12 * numberOfPatches + 22)).append("\n");
    sb.append("| Index              |").append(" ".repeat(5));
    for (int i = 0; i < numberOfPatches; i++) {
      sb.append(i + 1).append(" ".repeat(5)).append("|").append(" ".repeat(5));
    }
    sb.append("\n");
    sb.append("-".repeat(12 * numberOfPatches + 22)).append("\n");
    return sb.toString();
  }

  public String priceLineString(int numberOfPatches) {
    StringBuilder sb = new StringBuilder();
    sb.append("| Price              |").append(" ".repeat(5));
    for (var patch : getNextPatches(numberOfPatches)) {
      sb.append(patch.price());
      if (patch.price() < 10) {
        sb.append(" ".repeat(5)).append("|").append(" ".repeat(5));
      } else {
        sb.append(" ".repeat(4)).append("|").append(" ".repeat(5));
      }
    }
    sb.append("\n");
    return sb.toString();
  }

  public String blocksLineString(int numberOfPatches) {
    StringBuilder sb = new StringBuilder();
    sb.append("| Number of blocks   |").append(" ".repeat(5));
    for (var patch : getNextPatches(numberOfPatches)) {
      sb.append(patch.getNumberOfBlocks());
      if (patch.getNumberOfBlocks() < 10) {
        sb.append(" ".repeat(5)).append("|").append(" ".repeat(5));
      } else {
        sb.append(" ".repeat(4)).append("|").append(" ".repeat(5));
      }
    }
    sb.append("\n");
    return sb.toString();
  }

  public String earningsLineString(int numberOfPatches) {
    StringBuilder sb = new StringBuilder();
    sb.append("| Earnings           |").append(" ".repeat(5));
    for (var patch : getNextPatches(numberOfPatches)) {
      sb.append(patch.earnings());
      if (patch.earnings() < 10) {
        sb.append(" ".repeat(5)).append("|").append(" ".repeat(5));
      } else {
        sb.append(" ".repeat(4)).append("|").append(" ".repeat(5));
      }
    }
    sb.append("\n");
    return sb.toString();
  }

  public String formatLineString(int numberOfPatches) {
    StringBuilder sb = new StringBuilder();
    sb.append("-".repeat(12 * numberOfPatches + 22)).append("\n");
    for (int i = 0; i < getMaxHeight(getNextPatches(numberOfPatches)); i++) {
      if (i == 0) {
        sb.append("| Format             |");
      } else {
        sb.append("|                    |");
      }
      for (int j = 0; j < numberOfPatches; j++) {
        var patch = getNextPatches(numberOfPatches).get(j);
        if (i < patch.getHeight()) {
          int spaces = 11 - patch.getFormatLine(i).length();
          int backSpaces = spaces / 2;
          int frontSpaces = spaces - backSpaces;
          sb.append(" ".repeat(frontSpaces)).append(patch.getFormatLine(i)).append(" ".repeat(backSpaces)).append("|");
        } else {
          sb.append(" ".repeat(11)).append("|");
        }
      }
      sb.append("\n");
    }
    sb.append("-".repeat(12 * numberOfPatches + 22)).append("\n");
    return sb.toString();
  }

  public String displayNextPatches(int numberOfPatches) {
    StringBuilder sb = new StringBuilder();
    if (numberOfPatches > patches.size()) {
      numberOfPatches = patches.size();
    }
    sb.append("Next ").append(numberOfPatches).append(" patches:\n\n");
    sb.append(indexLineString(numberOfPatches));
    sb.append(priceLineString(numberOfPatches));
    sb.append(blocksLineString(numberOfPatches));
    sb.append(earningsLineString(numberOfPatches));
    sb.append(formatLineString(numberOfPatches));
    return sb.toString();
  }

  public void removePatch(Patch patch) {
    patches.remove(patch);
  }

  public int size() {
    return patches.size();
  }

  public boolean isEmpty() {
    return patches.isEmpty();
  }

  @Override
  public String toString() {
    var sb = new StringBuilder();
    for (var patch : patches) {
      sb.append(patch).append("\n");
    }
    return sb.toString();
  }
}