package net.minecraftnt.server.blocks;

public class Grass extends Block{
    @Override
    public int getFaceTexture(BlockFace face) {
        switch (face){
            case TOP_FACE -> { return 0; }
            case BOTTOM_FACE -> { return 2; }
        }
        return 3;
    }
}
