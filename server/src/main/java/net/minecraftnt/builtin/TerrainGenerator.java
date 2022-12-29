package net.minecraftnt.builtin;

import net.minecraftnt.server.world.*;
import net.minecraftnt.util.Identifier;
import net.minecraftnt.util.noise.FastNoiseLite;
import net.minecraftnt.util.noise.FractalPerlin;

public class TerrainGenerator extends WorldGenerator {

    @Override
    public Identifier getBiomeGenerator(int x, int z) {
        return BiomeGenerator.GRASS;
    }

    public static class BiomeGen extends BiomeGenerator {
        private static final FractalPerlin perlin = new FractalPerlin();

        static {
            perlin.octaves = 8;
            perlin.sizes = 0.5f;
            perlin.fallOff = 0.5f;
        }

        @Override
        public Chunk generateChunk(int x, int z, Chunk c) {
            for (int cx = 0; cx < Chunk.CHUNK_WIDTH; cx++) {
                for (int cz = 0; cz < Chunk.CHUNK_WIDTH; cz++) {
                    int height = 40 + (int) (perlin.GetNoise(x * Chunk.CHUNK_WIDTH + cx, z * Chunk.CHUNK_WIDTH + cz) * 10);
                    for (int y = 0; y < height; y++) {
                        if (y < height - 3)
                            c.setBlock(cx, y, cz, Block.STONE);
                        else if (y < height - 1)
                            c.setBlock(cx, y, cz, Block.DIRT);
                        else
                            c.setBlock(cx, y, cz, Block.GRASS);
                    }


                }
            }

            return c;
        }
    }
}
