package net.minecraftnt.rendering;

import com.google.gson.Gson;
import net.minecraftnt.Registries;
import net.minecraftnt.server.data.resources.Resources;
import net.minecraftnt.util.Identifier;
import net.minecraftnt.util.maths.Vector2;

import java.util.Map;

public class TextureAtlasLocation {
    public static final Identifier NULL = new Identifier("minecraftnt", "atlas.null");
    public static final Identifier GRASS_TOP = new Identifier("minecraftnt", "atlas.grass.top");
    public static final Identifier GRASS_SIDE = new Identifier("minecraftnt", "atlas.grass.side");
    public static final Identifier DIRT = new Identifier("minecraftnt", "atlas.dirt");
    public static final Identifier STONE = new Identifier("minecraftnt", "atlas.stone");


    private final Vector2 origin, max;

    public Vector2 getMax() {
        return max;
    }

    public Vector2 getOrigin() {
        return origin;
    }

    public static void loadLocations(String path) {
        String json = Resources.readString("data/" + path + ".json");
        Gson gson = new Gson();

        AtlasMap map = gson.fromJson(json, AtlasMap.class);

        Vector2 tileSize = new Vector2(1f / map.size.x, 1f / map.size.y);

        for(var tile : map.tiles.keySet()){
            AtlasPosition position = map.tiles.get(tile);
            Vector2 origin = new Vector2(tileSize.getX() * position.x, tileSize.getY() * position.y);
            TextureAtlasLocation location = new TextureAtlasLocation(origin, origin.add(tileSize));
            Registries.TEXTURE_ATLAS_LOC.register(new Identifier(map.namespace, tile), location);
        }

    }

    private TextureAtlasLocation(Vector2 origin, Vector2 max) {
        this.origin = origin;
        this.max = max;
    }

    private static class AtlasMap {
        public String namespace;
        public AtlasPosition size;

        public Map<String, AtlasPosition> tiles;

    }

    private static class AtlasPosition {
        public float x, y;

    }

}
