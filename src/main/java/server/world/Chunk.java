package server.world;

import client.rendering.Mesh;
import client.voxels.VoxelInformation;
import server.Minecraft;
import server.blocks.Block;
import server.blocks.BlockFace;
import util.*;
import util.registries.Registry;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import static server.Minecraft.TERRAIN_ATLAS_TEXTURE_SIZE;

public class Chunk {
    public static final int CHUNK_WIDTH  = 16;
    public static final int CHUNK_HEIGHT = 256;

    private Vector2I pos;
    private Transform transform;
    private short[][][] voxels = new short[CHUNK_WIDTH][CHUNK_HEIGHT][CHUNK_WIDTH];
    private float[][][][] lightning = new float[CHUNK_WIDTH][CHUNK_HEIGHT][CHUNK_WIDTH][6];
    private float[][][] illumination = new float[CHUNK_WIDTH][CHUNK_HEIGHT][CHUNK_WIDTH];
    private ArrayList<Identifier> map = new ArrayList<>();

    private Mesh mesh;

    public Chunk(Vector2I pos){
        mesh = new Mesh();
        this.pos = pos;
        this.transform = new Transform(new Vector3(pos.getX() * CHUNK_WIDTH, 0, pos.getY() * CHUNK_WIDTH));

        for(int x = 0; x < Chunk.CHUNK_WIDTH; x++) {
            voxels[x] = new short[CHUNK_HEIGHT][];
            for (int y = 0; y < Chunk.CHUNK_HEIGHT; y++) {
                voxels[x][y] = new short[CHUNK_WIDTH];
            }
        }
    }

    public void setVoxel(Vector3I pos, Identifier type){
        if(!map.contains(type))
            map.add(type);
        voxels[pos.getX()][pos.getY()][pos.getZ()] = (short)map.indexOf(type);
    }

    private Identifier getID(short identifier)
    {
        return map.get(identifier);
    }

    public Identifier getBlockIdentifier(Vector3I worldPos){
        int x = worldPos.getX() - CHUNK_WIDTH * pos.getX();
        int z = worldPos.getZ() - CHUNK_WIDTH * pos.getY();

        if(!isVoxelInChunk(x, worldPos.getY(), z))
            return Block.IDENTIFIER_AIR;

        return getID(voxels[x][worldPos.getY()][z]);
    }

    public float getIllumination(Vector3I worldPos){
        int x = worldPos.getX() - CHUNK_WIDTH * pos.getX();
        int z = worldPos.getZ() - CHUNK_WIDTH * pos.getY();

        if(!isVoxelInChunk(x, worldPos.getY(), z))
            return 1f;

        return illumination[x][worldPos.getY()][z];
    }

    public void setVoxelWorld(Vector3I worldPos, Identifier identifier){
        int x = worldPos.getX() - CHUNK_WIDTH * pos.getX();
        int z = worldPos.getZ() - CHUNK_WIDTH * pos.getY();

        if(!isVoxelInChunk(x, worldPos.getY(), z))
            return;

        setVoxel(new Vector3I(x, worldPos.getY(), z), identifier);
    }

    private Block getBlock(short identifier)
    {
        return Registry.BLOCKS.get(getID(identifier));
    }

    public void rebuildLightmap(){
        lightning = new float[CHUNK_WIDTH][CHUNK_HEIGHT][CHUNK_WIDTH][6];
        illumination = new float[CHUNK_WIDTH][CHUNK_HEIGHT][CHUNK_WIDTH];

        for (float[][] arr: illumination) {
            for (float[] arr2: arr) {
                Arrays.fill(arr2, -1);
            }
        }

        for (int x = 0; x < CHUNK_WIDTH; x++) {
            for (int z = 0; z < CHUNK_WIDTH; z++) {
                float currentStrength = 1f;
                for (int y = CHUNK_HEIGHT - 1; y >= 0; y--) {
                    if(currentStrength <= 0) {
                        break;
                    }
                    floodFillIllumination(x, y, z, currentStrength, 0);
                    //illumination[x][y][z] = currentStrength;
                    currentStrength -= getBlock(voxels[x][y][z]).getOpacity();
                }
            }
        }

        for (int x = 0; x < CHUNK_WIDTH; x++) {
            for (int y = 0; y < CHUNK_HEIGHT; y++) {
                for (int z = 0; z < CHUNK_WIDTH; z++) {
                    for (int p = 0; p < 6; p++) {
                        Vector3I checkPos = VoxelInformation.faceChecks[p].add(new Vector3I(x, y, z));
                        if(isVoxelInChunk(checkPos.getX(), checkPos.getY(), checkPos.getZ()))
                            lightning[x][y][z][p] = illumination[checkPos.getX()][checkPos.getY()][checkPos.getZ()];
                        else
                            lightning[x][y][z][p] = Minecraft.getInstance().getWorld().getLightLevel(checkPos);
                    }
                }
            }
        }
    }

    private void floodFillIllumination(int x, int y, int z, float strength, int depth){

        if(!isVoxelInChunk(x, y, z))
            return;

        if(depth > 1000)
            return;

        if(illumination[x][y][z] >= strength)
            return;

        illumination[x][y][z] = strength;
        float decrease = getBlock(voxels[x][y][z]).getOpacity();
        float illumination = getBlock(voxels[x][y][z]).getIllumination();

        for(int i = 0; i < 6; i++) {
            Vector3I faceCheck = VoxelInformation.faceChecks[i];
            floodFillIllumination(x + faceCheck.getX(), y + faceCheck.getY(), z + faceCheck.getZ(), strength - decrease - 0.5f + illumination, depth + 1);
        }

    }


    public void rebuildMesh(){
        ArrayList<Vector3> vertices = new ArrayList<>();
        ArrayList<Vector2> uvs = new ArrayList<>();
        ArrayList<Integer> triangles = new ArrayList<Integer>();
        ArrayList<Float> colors = new ArrayList<Float>();

        int vertexIndex = 0;
        for (int x = 0; x < CHUNK_WIDTH; x++) {
            for (int y = 0; y < CHUNK_HEIGHT; y++) {
                for (int z = 0; z < CHUNK_WIDTH; z++) {
                    Block block = getBlock(voxels[x][y][z]);
                    if (block.shouldBuildMesh()) {
                        Vector3 pos = new Vector3(x, y, z);
                        for (int p = 0; p < 6; p++) {
                            int textureID = block.getFaceTexture(VoxelInformation.getFace(p));
                            if(checkVoxel(VoxelInformation.faceChecks[p].add(pos.floor()))) {
                                for (int i = 0; i < 6; i++) {

                                    int triangleIndex = VoxelInformation.voxelTris[p][i];
                                    vertices.add(VoxelInformation.voxelVerts[triangleIndex].add(pos));
                                    triangles.add(vertexIndex);
                                    colors.add(lightning[x][y][z][p]);

                                    uvs.add(convertUV(VoxelInformation.voxelUvs[i], textureID));

                                    vertexIndex++;

                                }
                            }
                        }
                    }
                }
            }
        }

        mesh.vertices = vertices.toArray(Vector3[]::new);
        mesh.triangles = triangles.stream().mapToInt(i -> i).toArray();
        mesh.lightning = colors.toArray(Float[]::new);
        mesh.uv = uvs.toArray(Vector2[]::new);

        mesh.buildMesh();
    }

    private Vector2 convertUV(Vector2 uv, int textureID){

        float y = textureID / Minecraft.TERRAIN_ATLAS_TEXTURES;
        float x = textureID - (y * Minecraft.TERRAIN_ATLAS_TEXTURES);

        x *= TERRAIN_ATLAS_TEXTURE_SIZE;
        y *= TERRAIN_ATLAS_TEXTURE_SIZE;

        y = 1f - y - TERRAIN_ATLAS_TEXTURE_SIZE;

        return new Vector2(x + uv.getX() * TERRAIN_ATLAS_TEXTURE_SIZE, -(y + uv.getY() * TERRAIN_ATLAS_TEXTURE_SIZE));
    }

    private boolean checkVoxel (Vector3I pos) {

        // If position is outside of this chunk...
        if (!isVoxelInChunk(pos.getX(), pos.getY(), pos.getZ()))
            return Registry.BLOCKS.get(Minecraft.getInstance().getWorld().getBlock(
                    new Vector3I(
                            this.pos.getX() * CHUNK_WIDTH + pos.getX(),
                            pos.getY(),
                            this.pos.getY() * CHUNK_WIDTH + pos.getZ()
                            ))).renderNeighbourFaces();

        return getBlock(voxels[pos.getX()][pos.getY()][pos.getZ()]).renderNeighbourFaces();

    }

    boolean isVoxelInChunk (int x, int y, int z) {

        if (x < 0 || x > CHUNK_WIDTH - 1 || y < 0 || y > CHUNK_HEIGHT - 1 || z < 0 || z > CHUNK_WIDTH - 1)
            return false;
        else return true;

    }

    public void render(){
        mesh.render(Minecraft.getInstance().getBaseShader(), transform);
    }
}
