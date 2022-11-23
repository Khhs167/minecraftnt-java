package net.minecraftnt.world;

import net.minecraftnt.MinecraftntData;
import net.minecraftnt.Registries;
import net.minecraftnt.builtin.Grass;
import net.minecraftnt.rendering.Mesh;
import net.minecraftnt.rendering.ShapeGenerator;
import net.minecraftnt.rendering.Vertex;
import net.minecraftnt.util.FaceFlags;
import net.minecraftnt.util.Identifier;
import net.minecraftnt.util.maths.Vector3;
import net.minecraftnt.util.maths.VoxelPosition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Chunk {

    public static final int CHUNK_WIDTH = 16;
    public static final int CHUNK_HEIGHT = 256;

    private short[][][] map = new short[CHUNK_WIDTH][CHUNK_HEIGHT][CHUNK_WIDTH];
    private HashMap<Short, Identifier> blockMap = new HashMap<>();
    private HashMap<Identifier, Short> idMap = new HashMap<>();
    public Mesh mesh;

    public Chunk() {
        blockMap.put((short)0, Block.AIR);
        idMap.put(Block.AIR, (short)0);
    }

    private boolean isInside(int x, int y, int z) {
        return  x >= 0 && x < CHUNK_WIDTH &&
                y >= 0 && y < CHUNK_HEIGHT &&
                z >= 0 && z < CHUNK_WIDTH;
    }

    public Short getID(int x, int y, int z) {
        if(!isInside(x, y, z))
            return 0;

        return map[x][y][z];
    }

    public Block getBlock(int x, int y, int z) {
        if(!isInside(x, y, z))
            return new Block() {
            };

        return Registries.BLOCKS.get(blockMap.get(getID(x, y, z)));
    }

    public boolean getSolid(int x, int y, int z) {
        if(!isInside(x, y, z))
            return false;

        return getBlock(x, y, z).isSolid();
    }

    public void setBlock(int x, int y, int z, Identifier identifier) {
        if(!blockMap.containsValue(identifier)) {
            short id = (short) (blockMap.size());
            blockMap.put(id, identifier);
            idMap.put(identifier, id);
        }
        map[x][y][z] = idMap.get(identifier);
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

        ArrayList<Vertex> vertices = new ArrayList<>();

        for(int x = 0; x < CHUNK_WIDTH; x++){
            for(int z = 0; z < CHUNK_WIDTH; z++){
                for(int y = 0; y < CHUNK_HEIGHT; y++){
                    if(!getSolid(x, y, z))
                        continue;
                    ArrayList<FaceFlags.Faces> faces = new ArrayList<>();

                    for(int i = 0; i < 6; i++){
                        VoxelPosition facePos = new VoxelPosition(x, y, z).add(faceChecks[i]);
                        if(!getSolid(facePos.getX(), facePos.getY(), facePos.getZ()))
                            faces.add(FaceFlags.FACES_LIST[i]);
                    }

                    vertices.addAll(Registries.SHAPE_GENERATOR.get(ShapeGenerator.BLOCK).generateShape(new FaceFlags(faces), new Vector3(x, y, z), getBlock(x, y, z)));
                }
            }
        }

        mesh.vertices = vertices.toArray(new Vertex[0]);
    }
}
