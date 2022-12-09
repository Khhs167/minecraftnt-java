package net.minecraftnt.server.world;

import java.util.Objects;

public class ChunkPosition {
    private final int x;
    private final int y;
    private final int hash;

    public ChunkPosition(int x, int y) {
        this.x = x;
        this.y = y;
        this.hash = Objects.hash(x, y);
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ChunkPosition other){
            return hash == other.hash;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public String toString() {
        return x + ", " + y;
    }
}
