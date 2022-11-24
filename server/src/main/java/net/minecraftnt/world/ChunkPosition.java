package net.minecraftnt.world;

public class ChunkPosition {
    private final int x;
    private final int y;

    public ChunkPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }
}
