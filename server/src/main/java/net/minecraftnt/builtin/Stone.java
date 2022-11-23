package net.minecraftnt.builtin;

import net.minecraftnt.rendering.TextureAtlasLocation;
import net.minecraftnt.util.FaceFlags;
import net.minecraftnt.util.Identifier;
import net.minecraftnt.world.Block;

public class Stone extends Block {
    @Override
    public Identifier getAtlasID(FaceFlags.Faces face) {
        return TextureAtlasLocation.STONE;
    }


}
