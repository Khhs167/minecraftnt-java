package net.minecraftnt.world;

import net.minecraftnt.util.Identifier;

public interface IBiomeGenerator {

    public static final Identifier GRASS = new Identifier("minecraftnt", "biome.grass");

    Chunk generateChunk(int x, int z);
}
