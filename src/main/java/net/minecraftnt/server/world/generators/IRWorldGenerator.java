package net.minecraftnt.server.world.generators;

import net.minecraftnt.server.world.Chunk;
import net.minecraftnt.util.Identifier;
import net.minecraftnt.util.Vector2I;

public interface IRWorldGenerator {
    Identifier IDENTIFIER_FLAT = new Identifier("minecraft", "flat_world");
    static Identifier IDENTIFIER_OVERWORLD = new Identifier("minecraft", "overworld");

    Chunk getChunk(Vector2I pos);
}
