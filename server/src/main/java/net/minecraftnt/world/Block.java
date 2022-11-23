package net.minecraftnt.world;

import net.minecraftnt.rendering.ShapeGenerator;
import net.minecraftnt.rendering.TextureAtlasLocation;
import net.minecraftnt.util.FaceFlags;
import net.minecraftnt.util.Identifier;

public abstract class Block {

    public static final Identifier DIRT = new Identifier("minecraftnt", "dirt");
    public static final Identifier GRASS = new Identifier("minecraftnt", "grass");
    public static final Identifier AIR = new Identifier("minecraftnt", "air");
    public static final Identifier STONE = new Identifier("minecraftnt", "stone");


    public Identifier getShapeGenerator() {
        return ShapeGenerator.BLOCK;
    }

    public Identifier getAtlasID(FaceFlags.Faces face) {
        return TextureAtlasLocation.NULL;
    }

    public boolean isSolid() {
        return true;
    }
}
