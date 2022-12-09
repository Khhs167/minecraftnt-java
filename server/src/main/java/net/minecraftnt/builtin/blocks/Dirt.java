package net.minecraftnt.builtin.blocks;

import net.minecraftnt.rendering.TextureAtlasLocation;
import net.minecraftnt.util.FaceFlags;
import net.minecraftnt.util.Identifier;
import net.minecraftnt.server.world.Block;

public class Dirt extends Block {
    @Override
    public Identifier getAtlasID(FaceFlags.Faces face) {
        return TextureAtlasLocation.DIRT;
    }
}
