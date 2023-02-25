package fr.uge.patchwork.game;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Class for the circle of patches
 */
public class CirclePatches {

  private final ArrayList<Patch> patches;
  private int neutralToken;

  /**
   * Init the circle of patches
   */
  public CirclePatches() {
    this.patches = new ArrayList<>();
    this.neutralToken = 0;
  }

  /**
   * Load the circle of patches from a file
   * FORMAT:
   * - each line represents a patch
   * - id:price:forwardBlocks:earnings:format:imageId
   *
   * @param path (Path) path to the file
   * @throws IOException if the file cannot be read
   */
  public void load(Path path) throws IOException {
    Objects.requireNonNull(path);
    try (var reader = Files.newBufferedReader(path)) {
      patches.addAll(reader.lines()
              .map(line -> line.split(":"))
              .map(tokens -> new Patch(Integer.parseInt(tokens[0]), tokens[4], Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), 0, false, -1, -1, Integer.parseInt(tokens[5])))
              .toList());
    }
  }

  /**
   * Return the list of patches
   *
   * @return (List < Patch >)
   */
  public List<Patch> getPatches() {
    return new ArrayList<>(patches);
  }

  /**
   * Return the neutral token position
   *
   * @return (int)
   */
  public int getNeutralToken() {
    return neutralToken;
  }

  /**
   * Shuffle the circle of patches
   */
  public void shuffle() {
    Collections.shuffle(patches);
  }

  /**
   * Return the smallest patch in the circle
   *
   * @return (Patch) the smallest patch
   */
  private Patch getSmallestPatch() {
    return patches.stream().min(Comparator.comparingInt(Patch::getNumberOfBlocks)).orElseThrow();
  }

  /**
   * Return the index of the smallest patch in the circle
   *
   * @return (int) the index of the smallest patch
   */
  private int getSmallestPatchIndex() {
    return patches.indexOf(getSmallestPatch());
  }

  /**
   * Place the neutral token on the smallest patch
   */
  public void placeNeutralToken() {
    int smallestPatchIndex = getSmallestPatchIndex();
    neutralToken = smallestPatchIndex == patches.size() - 1 ? 0 : smallestPatchIndex + 1;
  }

  /**
   * Return the list of the next n patches
   *
   * @param n (int) number of patches to return
   * @return (List < Patch >)
   */
  public List<Patch> getNextPatches(int n) {
    List<Patch> nextPatches = new ArrayList<>();
    int j;
    for (int i = neutralToken; i < neutralToken + n; i++) {
      if (i >= patches.size()) {
        j = i - patches.size();
      } else {
        j = i;
      }
      nextPatches.add(patches.get(j));
    }
    return nextPatches;
  }

  /**
   * Return the max height of a list of patches
   *
   * @param patches (List < Patch >) list of patches
   * @return (int) max height
   */
  public static int getMaxHeight(List<Patch> patches) {
    Objects.requireNonNull(patches);
    return patches.stream().mapToInt(Patch::getHeight).max().orElseThrow();
  }

  /**
   * Return the index of a patch in the circle
   *
   * @param patch (Patch) the patch
   * @return (int) the index of the patch
   */
  private int getPatchIndex(Patch patch) {
    Objects.requireNonNull(patch);
    if (patches.contains(patch)) {
      return patches.indexOf(patch);
    }
    for (var p : patches) {
      if (patch.id() == p.id()) {
        return patches.indexOf(p);
      }
    }
    return -1;
  }

  /**
   * Remove a patch from the circle
   *
   * @param patch (Patch) the patch to remove
   */
  public void removePatch(Patch patch) {
    Objects.requireNonNull(patch);
    int index = getPatchIndex(patch);
    neutralToken = index;
    patches.remove(index);
    if (neutralToken == patches.size()) {
      neutralToken = 0;
    }
  }

  /**
   * Return true if the circle is empty
   *
   * @return (boolean)
   */
  public boolean isEmpty() {
    return patches.isEmpty();
  }

  /**
   * Check if the numbers of patches wanted are available, if not, return the max number of patches available
   *
   * @param nbPatch (int) number of patches wanted
   * @return (int) number of patches available
   */
  public int getNbPatches(int nbPatch) {
    return Math.min(patches.size(), nbPatch);
  }
}