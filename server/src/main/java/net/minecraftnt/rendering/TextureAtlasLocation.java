package net.minecraftnt.rendering;

import com.google.gson.Gson;
import net.minecraftnt.Registries;
import net.minecraftnt.server.data.resources.Resources;
import net.minecraftnt.util.Identifier;
import net.minecraftnt.util.maths.Vector2;

import java.util.Map;

public class TextureAtlasLocation {
    public static final Identifier NULL = new Identifier("minecraftnt", "blocks/null");
    public static final Identifier GRASS_TOP = new Identifier("minecraftnt", "blocks/grass_top");
    public static final Identifier GRASS_SIDE = new Identifier("minecraftnt", "blocks/grass_side");
    public static final Identifier DIRT = new Identifier("minecraftnt", "blocks/dirt");
    public static final Identifier STONE = new Identifier("minecraftnt", "blocks/stone");


    private final Vector2 origin, max;

    public Vector2 getMax() {
        return max;
    }

    public Vector2 getOrigin() {
        return origin;
    }

    public TextureAtlasLocation(Vector2 origin, Vector2 max) {
        this.origin = origin;
        this.max = max;
    }


    private static class AtlasPosition {
        public float x, y;

    }

}
