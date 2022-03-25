package server.world.generators;

import util.Identifier;
import util.Vector2I;

public interface IRWorldGenerator {
    public static final Identifier IDENTIFIER_FLAT = new Identifier("minecraft", "flat_world");
    public static final Identifier IDENTIFIER_OVERWORLD = new Identifier("minecraft", "overworld");

    public server.world.Chunk getChunk(Vector2I pos);
}
