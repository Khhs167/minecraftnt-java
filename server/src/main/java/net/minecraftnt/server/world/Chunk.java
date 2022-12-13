package net.minecraftnt.server.world;

import net.minecraftnt.MinecraftntData;
import net.minecraftnt.Registries;
import net.minecraftnt.rendering.*;
import net.minecraftnt.util.FaceFlags;
import net.minecraftnt.util.Identifier;
import net.minecraftnt.util.maths.Transformation;
import net.minecraftnt.util.maths.Vector3;
import net.minecraftnt.util.maths.VoxelPosition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;

public class Chunk {
    public static final Logger LOGGER = LogManager.getLogger(Chunk.class);
    public static final int CHUNK_WIDTH = 16;
    public static final int CHUNK_HEIGHT = 256;

    private final short[][][] map = new short[CHUNK_WIDTH][CHUNK_HEIGHT][CHUNK_WIDTH];
    private final float[][][] illuminationMap = new float[CHUNK_WIDTH][CHUNK_HEIGHT][CHUNK_WIDTH];
    private final HashMap<Short, Identifier> blockMap = new HashMap<>();
    private final HashMap<Identifier, Short> idMap = new HashMap<>();
    public Mesh mesh;
    public Transformation transformation;
    public boolean dirty = false;
    public World world;
    private final ChunkPosition position;

    public ChunkPosition getPosition() {
        return position;
    }

    public Chunk(ChunkPosition position) {
        this.position = position;
        blockMap.put((short)0, Block.AIR);
        idMap.put(Block.AIR, (short)0);
        transformation = new Transformation();
    }

    public HashMap<Short, Identifier> getBlockMap() {
        return blockMap;
    }

    public void setBlockID(Short id, Identifier identifier) {
        blockMap.put(id, identifier);
        idMap.put(identifier, id);
    }

    public boolean isInside(int x, int y, int z) {
        return  x >= 0 && x < CHUNK_WIDTH &&
                y >= 0 && y < CHUNK_HEIGHT &&
                z >= 0 && z < CHUNK_WIDTH;
    }

    public Short getID(int x, int y, int z) {
        if(!isInside(x, y, z))
            return idMap.get(world.getBlock(position.getX() * CHUNK_WIDTH + x, y, position.getY() * CHUNK_WIDTH + z));

        return map[x][y][z];
    }

    public Identifier getBlockID(int x, int y, int z) {
        if(!isInside(x, y, z))
            world.getBlock(position.getX() * CHUNK_WIDTH + x, y, position.getY() * CHUNK_WIDTH + z);

        return blockMap.get(getID(x, y, z));
    }

    public Block getBlock(int x, int y, int z) {
        return Registries.BLOCKS.get(getBlockID(x, y, z));
    }

    public boolean getSolid(int x, int y, int z) {
        Block block = getBlock(x, y, z);
        if(block == null)
            return false;
        return block.isSolid();
    }

    public void setBlock(int x, int y, int z, Identifier identifier) {
        if(!blockMap.containsValue(identifier)) {
            short id = (short) (blockMap.size());
            blockMap.put(id, identifier);
            idMap.put(identifier, id);
        }
        map[x][y][z] = idMap.get(identifier);
    }

    public void setBlock(int x, int y, int z, Short id) {
        map[x][y][z] = id;
    }

    private final VoxelPosition[] faceChecks = {
            new VoxelPosition(0, 0, -1),
            new VoxelPosition(1, 0, 0),
            new VoxelPosition(0, 0, 1),
            new VoxelPosition(-1, 0, 0),
            new VoxelPosition(0, 1, 0),
            new VoxelPosition(0, -1, 0),
    };

    public void generateMesh() {
        if(!MinecraftntData.isClient())
            return;

        //LOGGER.debug("Regenerating chunk mesh");

        ArrayList<Vertex> vertices = new ArrayList<>();

        for(int x = 0; x < CHUNK_WIDTH; x++){
            for(int z = 0; z < CHUNK_WIDTH; z++){
                for(int y = 0; y < CHUNK_HEIGHT; y++){
                    if(!getSolid(x, y, z))
                        continue;
                    FaceFlags faces = new FaceFlags();

                    for(int i = 0; i < 6; i++){
                        VoxelPosition checkPos = faceChecks[i];
                        if(!getSolid(x + checkPos.getX(), y + checkPos.getY(), z + checkPos.getZ()))
                            faces.setFace(FaceFlags.FACES_LIST[i]);
                    }

                    vertices.addAll(Registries.SHAPE_GENERATOR.get(ShapeGenerator.BLOCK).generateShape(faces, new Vector3(x, y, z), getBlock(x, y, z)));
                }
            }
        }

        mesh.setVertices(vertices.toArray(new Vertex[0]));
    }

    public float getIllumination(int x, int y, int z) {
        if(!isInside(x, y, z))
            return -1; // TODO: World check lol
        return illuminationMap[x][y][z];
    }
}
