package net.minecraftnt.server.world;

import net.minecraftnt.util.Identifier;

public abstract class BiomeGenerator {

    public static final Identifier GRASS = new Identifier("minecraftnt", "biome.grass");

    public abstract Chunk generateChunk(int x, int z, Chunk chunk);
}
