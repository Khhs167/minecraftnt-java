package net.minecraftnt.builtin;

import net.minecraftnt.rendering.Renderer;
import net.minecraftnt.util.Identifier;
import net.minecraftnt.util.maths.FastNoiseLite;
import net.minecraftnt.world.Block;
import net.minecraftnt.world.Chunk;
import net.minecraftnt.world.IBiomeGenerator;
import net.minecraftnt.world.IWorldGenerator;

public class TerrainGenerator implements IWorldGenerator, IBiomeGenerator {

    FastNoiseLite fastNoiseLite = new FastNoiseLite();

    @Override
    public Chunk generateChunk(int x, int z) {
        Chunk c = new Chunk();


        for(int cx = 0; cx < Chunk.CHUNK_WIDTH; cx++){
            for(int cz = 0; cz < Chunk.CHUNK_WIDTH; cz++){
                int height = 40 + (int)(fastNoiseLite.GetNoise(x * Chunk.CHUNK_WIDTH + cx, z * Chunk.CHUNK_WIDTH + cz) * 10);
                for(int y = 0; y < height; y++){
                    if(y < height - 3)
                        c.setBlock(cx, y, cz, Block.STONE);
                    else if(y < height - 1)
                        c.setBlock(cx, y, cz, Block.DIRT);
                    else
                        c.setBlock(cx, y, cz, Block.GRASS);
                }


            }
        }

        return c;
    }

    @Override
    public Identifier getBiomeGenerator(int x, int z) {
        return IBiomeGenerator.GRASS;
    }
}
