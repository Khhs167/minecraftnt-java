package net.minecraftnt.server.world.generators;

import net.minecraftnt.server.blocks.Block;
import net.minecraftnt.server.world.*;
import net.minecraftnt.util.Vector2I;
import net.minecraftnt.util.Vector3I;

public class FlatWorldGen implements IRWorldGenerator{

    @Override
    public Chunk getChunk(Vector2I pos) {
        Chunk chunk = new Chunk(pos);
        for(int x = 0; x < Chunk.CHUNK_WIDTH; x++){
            for(int z = 0; z < Chunk.CHUNK_WIDTH; z++){
                for (int y = 0; y < Chunk.CHUNK_HEIGHT; y++){
                    if(y <= 4)
                        chunk.setVoxel(new Vector3I(x, y, z), Block.IDENTIFIER_COBBLESTONE);
                    else if(y == 5)
                        chunk.setVoxel(new Vector3I(x, y, z), Block.IDENTIFIER_GRASS);
                    else
                        chunk.setVoxel(new Vector3I(x, y, z), Block.IDENTIFIER_AIR);
                }
            }
        }
        chunk.rebuildMesh();
        return chunk;
    }
}
