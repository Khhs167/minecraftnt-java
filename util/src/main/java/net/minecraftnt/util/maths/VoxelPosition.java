package net.minecraftnt.util.maths;

public class VoxelPosition {
    private final int x, y, z;

    public VoxelPosition(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public VoxelPosition add(VoxelPosition other) {
        return new VoxelPosition(this.x + other.x, this.y + other.y, this.z + other.z);
    }
}
