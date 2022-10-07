package net.minecraftnt.server.world.generators;

import net.minecraftnt.server.external.FastNoiseLite;
import net.minecraftnt.server.blocks.Block;
import net.minecraftnt.server.world.Chunk;
import net.minecraftnt.util.Vector2I;
import net.minecraftnt.util.Vector3I;

public class OverWorldGenerator implements IRWorldGenerator{
    private static final FastNoiseLite noise = new FastNoiseLite();
    @Override
    public Chunk getChunk(Vector2I pos) {
        noise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);

        noise.SetFrequency(0.01f);
        noise.SetFractalLacunarity(2f);
        noise.SetFractalGain(0.5f);

        noise.SetFractalType(FastNoiseLite.FractalType.FBm);

        Chunk chunk = new Chunk(pos);
        for(int x = 0; x < Chunk.CHUNK_WIDTH; x++){
            for(int z = 0; z < Chunk.CHUNK_WIDTH; z++){
                float noiseStrength = noise.GetNoise(x + pos.getX() * Chunk.CHUNK_WIDTH, z + pos.getY() * Chunk.CHUNK_WIDTH);
                float height = 40 + noiseStrength * 20;
                for (int y = 0; y < Chunk.CHUNK_HEIGHT; y++){
                    if(y <= 1 + noiseStrength) {
                        chunk.setVoxel(new Vector3I(x, y, z), Block.IDENTIFIER_BEDROCK);
                    } else if(y < height - 4){
                        chunk.setVoxel(new Vector3I(x, y, z), Block.IDENTIFIER_STONE);
                    } else if(y < height - 1){
                        chunk.setVoxel(new Vector3I(x, y, z), Block.IDENTIFIER_DIRT);
                    } else if(y < height){
                        chunk.setVoxel(new Vector3I(x, y, z), Block.IDENTIFIER_GRASS);
                    } else {
                        chunk.setVoxel(new Vector3I(x, y, z), Block.IDENTIFIER_AIR);
                    }
                }
            }
        }
        return chunk;
    }
}
