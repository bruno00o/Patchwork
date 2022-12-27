package patchwork.game;

import patchwork.game.Player;

public class SpecialTile {
    private final int size;
    private final int earnings;
    private Player player;

    public SpecialTile(int size, int earnings, Player player) {
        this.size = size;
        this.earnings = earnings;
        this.player = player;
    }

    public int getSize() {
        return size;
    }

    public int getEarnings() {
        return earnings;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public boolean isOwned() {
        return player != null;
    }
}
