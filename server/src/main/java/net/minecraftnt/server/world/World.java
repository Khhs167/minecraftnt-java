package net.minecraftnt.server.world;

import net.minecraftnt.MinecraftntData;
import net.minecraftnt.Registries;
import net.minecraftnt.saving.WorldIO;
import net.minecraftnt.rendering.Renderer;
import net.minecraftnt.threading.BalancedThreadPool;
import net.minecraftnt.util.Identifier;
import net.minecraftnt.util.maths.Vector3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class World {
    public static final Logger LOGGER = LogManager.getLogger(World.class);
    private WorldGenerator worldGenerator;
    private final HashMap<ChunkPosition, Chunk> chunks = new HashMap<>();
    private static final int CHUNK_PREGEN_RADIUS = 8;
    private final Lock lock = new ReentrantLock();

    //private static final ThreadedExecutor EXECUTOR = new ThreadedExecutor("WorldChunkThread");
    private static final BalancedThreadPool EXECUTOR = BalancedThreadPool.getGlobalInstance();

    private final WorldIO worldIO;

    public Identifier getBlock(int x, int y, int z) {

        if (y < 0 || y >= Chunk.CHUNK_HEIGHT)
            return Block.AIR;

        int cx = Math.floorDiv(x, Chunk.CHUNK_WIDTH);
        int cz = Math.floorDiv(z, Chunk.CHUNK_WIDTH);

        Chunk chunk = chunks.get(new ChunkPosition(cx, cz));

        if (chunk == null)
            return Block.AIR;

        return chunk.getBlockID(x - cx * Chunk.CHUNK_WIDTH, y, z - cz * Chunk.CHUNK_WIDTH);
    }

    public World(Identifier generator) {
        EXECUTOR.start();

        worldIO = new WorldIO("test");

        worldGenerator = Registries.WORLD_GENERATOR.get(generator);
        generate();
    }

    public World() {
        EXECUTOR.start();

        worldIO = new WorldIO("test");
    }

    public void setChunk(Chunk chunk) {
        chunks.put(chunk.getPosition(), chunk);
        chunk.world = this;
        chunk.transformation.setPosition(new Vector3(chunk.getPosition().getX() * Chunk.CHUNK_WIDTH, 0,chunk.getPosition().getY() * Chunk.CHUNK_WIDTH));
        chunk.mesh = Renderer.createMeshC();
        rebuildNeighbours(chunk.getPosition().getX(), chunk.getPosition().getY());
    }


    // Generate the terrain
    public void generate() {
        LOGGER.info("Generating world");
        for (int x = -CHUNK_PREGEN_RADIUS; x < CHUNK_PREGEN_RADIUS; x++) {
            for (int z = -CHUNK_PREGEN_RADIUS; z < CHUNK_PREGEN_RADIUS; z++) {
                Chunk c = new Chunk(new ChunkPosition(x, z));
                generateDirect(x, z, c);
            }
        }
    }

    private static final boolean LOAD_WORLD = false;

    public Chunk loadChunk(int x, int z, Chunk c) {
        Chunk chunk = null;
        if (LOAD_WORLD)
            chunk = worldIO.loadChunk(new ChunkPosition(x, z));
        if (chunk == null)
            chunk = Registries.BIOME_GENERATOR.get(worldGenerator.getBiomeGenerator(x, z)).generateChunk(x, z, c);
        return chunk;
    }

    public void save() {
        LOGGER.info("Saving world...");
        for (ChunkPosition pos : chunks.keySet())
            worldIO.saveChunk(chunks.get(pos), pos);
        LOGGER.info("Saved world!");
    }

    public void generate(int x, int z) {
        Chunk c = new Chunk(new ChunkPosition(x, z));
        EXECUTOR.enqueue(() -> {
            generateDirect(x, z, c);
        }, 60);
    }

    public void generateDirect(int x, int z, Chunk c) {
        Chunk chunk = loadChunk(x, z, c);
        chunk.transformation.setPosition(new Vector3(x * Chunk.CHUNK_WIDTH, 0, z * Chunk.CHUNK_WIDTH));
        chunk.world = this;

        lock.lock();
        chunks.put(new ChunkPosition(x, z), chunk);
        lock.unlock();
    }

    public void rebuildNeighbours(int x, int y) {
        rebuild(x, y);
        rebuild(x - 1, y);
        rebuild(x + 1, y);
        rebuild(x, y - 1);
        rebuild(x, y + 1);
    }

    public void rebuild(int x, int y) {
        Chunk chunk = chunks.get(new ChunkPosition(x, y));

        if(chunk == null)
            return;

        EXECUTOR.enqueue(() -> {
            chunk.mesh.lock.lock();
            chunk.generateMesh();
            chunk.dirty = true;
            chunk.mesh.lock.unlock();
        }, 10);
    }

    // Illuminate the terrain
    public void illuminate() {

    }

    int genX = 0;

    // Render the chunks
    public void render() {
        lock.lock();
        for (Chunk chunk : chunks.values()) {
            if (chunk.mesh == null) {
                chunk.mesh = Renderer.createMeshC();
                BalancedThreadPool.getGlobalInstance().enqueue(() -> {
                    rebuild(chunk.getPosition().getX(), chunk.getPosition().getY());
                });
            }
            if (chunk.dirty) {
                chunk.mesh.lock.lock();
                Renderer.updateMeshC(chunk.mesh);
                chunk.dirty = false;
                chunk.mesh.lock.unlock();
            }

            // Our GPU-side chunk will never change.

            Renderer.shaderProviderC().setModel(chunk.transformation.getMatrix());
            Renderer.renderMeshC(chunk.mesh);


        }
        lock.unlock();
    }

    public Chunk getChunk(int x, int z) {
        Chunk c = chunks.get(new ChunkPosition(x, z));
        if(c == null) {
            generateDirect(x, z, new Chunk(new ChunkPosition(x, z)));
            c = chunks.get(new ChunkPosition(x, z));
        }
        return c;
    }
}