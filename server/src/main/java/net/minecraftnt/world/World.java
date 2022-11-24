package net.minecraftnt.world;

import net.minecraftnt.MinecraftntData;
import net.minecraftnt.Registries;
import net.minecraftnt.rendering.Renderer;
import net.minecraftnt.util.Identifier;
import net.minecraftnt.ThreadedExecutor;
import net.minecraftnt.util.maths.Vector3;

import java.util.HashMap;

public class World {
    private final IWorldGenerator worldGenerator;
    private final HashMap<ChunkPosition, Chunk> chunks = new HashMap<ChunkPosition, Chunk>();

    private static final int CHUNK_PREGEN_RADIUS = 5;

    public World(Identifier generator) {
        worldGenerator = Registries.WORLD_GENERATOR.get(generator);
        generate();
        illuminate();
        if(MinecraftntData.isClient())
            bake();
    }

    // Generate the terrain
    public void generate() {
        for(int x = -CHUNK_PREGEN_RADIUS; x < CHUNK_PREGEN_RADIUS; x++){
            for(int z = -CHUNK_PREGEN_RADIUS; z < CHUNK_PREGEN_RADIUS; z++){
                generate(x, z);
            }
        }
    }

    public void generate(int x, int z) {
        Chunk chunk = Registries.BIOME_GENERATOR.get(worldGenerator.getBiomeGenerator(x, z)).generateChunk(x, z);
        chunks.put(new ChunkPosition(x, z), chunk);
        chunk.transformation.setPosition(new Vector3(x * Chunk.CHUNK_WIDTH, 0, z * Chunk.CHUNK_WIDTH));
    }

    // Illuminate the terrain
    public void illuminate() {

    }

    // Bake the meshes
    public void bake() {
        ThreadedExecutor chunkGenerationExecutor = new ThreadedExecutor("ChunkBuilder");
        for(Chunk chunk : chunks.values()) {
            chunkGenerationExecutor.enqueue(() -> {
                chunk.mesh.lock.lock();
                chunk.generateMesh();
                chunk.dirty = true;
                chunk.mesh.lock.unlock();
            });

        }

        chunkGenerationExecutor.enqueue(chunkGenerationExecutor::kill);
        chunkGenerationExecutor.start();
    }

    // Render the chunks
    public void render() {
        for(Chunk chunk : chunks.values()){
            if(chunk.dirty) {
                chunk.mesh.lock.lock();
                Renderer.updateMeshC(chunk.mesh);
                chunk.dirty = false;
                chunk.mesh.lock.unlock();
            }

            // Our GPU-side chunk will never change.
            Renderer.shaderProviderC().setModel(chunk.transformation.getMatrix());
            Renderer.renderMeshC(chunk.mesh);

        }
    }
}