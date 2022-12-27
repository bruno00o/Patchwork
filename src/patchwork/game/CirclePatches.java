package patchwork.game;

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
      patches.addAll(reader.lines()
              .map(line -> line.split(":"))
              .map(tokens -> new Patch(tokens[3], Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2])))
              .collect(Collectors.toList()));
    }
  }

//  public Patch getPatch(int index) {
//    return patches.get(index);
//  }

  public void shuffle() {
    Collections.shuffle(patches);
  }

  public Patch getSmallestPatch() {
    return patches.stream().min((p1, p2) -> p1.getNumberOfBlocks() - p2.getNumberOfBlocks()).orElseThrow();
  }

  public int getSmallestPatchIndex() {
    return patches.indexOf(getSmallestPatch());
  }

  public void placeNeutralToken() {
    int smallestPatchIndex = getSmallestPatchIndex();
    neutralToken = smallestPatchIndex == patches.size() - 1 ? 0 : smallestPatchIndex + 1;
  }

  public List<Patch> getNextPatches(int n) {
    int limit = Math.min(n, patches.size());
    return Stream.concat(
            patches.stream().skip(neutralToken),
            patches.stream().limit(neutralToken + 1)
    ).limit(limit).collect(Collectors.toList());
  }

  public static int getMaxHeight(List<Patch> patches) {
    return patches.stream().mapToInt(Patch::getHeight).max().orElseThrow();
  }

  public void removePatch(Patch patch) {
    patches.remove(patch);
  }

  public boolean isEmpty() {
    return patches.isEmpty();
  }

  public int getNbPatches(int nbPatch) {
    return patches.size() < nbPatch ? patches.size() : nbPatch;
  }
}
