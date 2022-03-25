package server.blocks;

public class Air extends Block{
    @Override
    public boolean shouldBuildMesh() {
        return false;
    }

    @Override
    public boolean renderNeighbourFaces() {
        return true;
    }

    @Override
    public int getFaceTexture(BlockFace face) {
        return 0;
    }

    @Override
    public float getOpacity() {
        return 0;
    }

    @Override
    public boolean hasCollisions() {
        return false;
    }
}
