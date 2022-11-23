package net.minecraftnt.builtin;

import net.minecraftnt.rendering.TextureAtlasLocation;
import net.minecraftnt.util.FaceFlags;
import net.minecraftnt.util.Identifier;
import net.minecraftnt.world.Block;

public class Grass extends Block {
    @Override
    public Identifier getAtlasID(FaceFlags.Faces face) {
        switch (face) {
            case FACE_TOP -> {
                return TextureAtlasLocation.GRASS_TOP;
            }
            case FACE_BOTTOM -> {
                return TextureAtlasLocation.DIRT;
            }
            default -> {
                return TextureAtlasLocation.GRASS_SIDE;
            }
        }
    }
}
