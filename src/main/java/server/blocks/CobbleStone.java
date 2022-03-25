package server.blocks;

public class CobbleStone extends Block {

    @Override
    public int getFaceTexture(BlockFace face) {
        return 16;
    }

    @Override
    public float getIllumination() {
        return 1.5f;
    }
}
