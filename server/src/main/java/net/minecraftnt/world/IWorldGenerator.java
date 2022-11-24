package net.minecraftnt.world;

import net.minecraftnt.util.Identifier;

public interface IWorldGenerator {
    public static final Identifier OVERWORLD = new Identifier("minecraftnt", "world.overworld");
    Identifier getBiomeGenerator(int x, int z);
}
