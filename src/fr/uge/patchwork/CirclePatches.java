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

  public String displayNextPatches(int numberOfPatches) {
    StringBuilder sb = new StringBuilder();
    if (numberOfPatches > patches.size()) {
      numberOfPatches = patches.size();
    }
    sb.append("Next " + numberOfPatches + " patches:\n\n");
    for (var patch : getNextPatches(numberOfPatches)) {
      sb.append(patch).append("\n");
    }
    return sb.substring(0, sb.length() - 1);
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