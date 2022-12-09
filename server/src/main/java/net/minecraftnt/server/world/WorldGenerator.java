package net.minecraftnt.server.world;

import net.minecraftnt.util.Identifier;

public abstract class WorldGenerator {
    public static final Identifier OVERWORLD = new Identifier("minecraftnt", "world.overworld");
    public abstract Identifier getBiomeGenerator(int x, int z);
}
