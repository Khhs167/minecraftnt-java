package server.world;

import client.rendering.Mesh;
import client.rendering.Shader;
import client.voxels.VoxelInformation;
import server.Minecraft;
import server.blocks.Block;
import server.blocks.BlockFace;
import util.*;
import util.registries.Registry;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.SynchronousQueue;

import static server.Minecraft.TERRAIN_ATLAS_TEXTURE_SIZE;

public class Chunk {
    public static final int CHUNK_WIDTH  = 16;
    public static final int CHUNK_HEIGHT = 256;

    private Vector2I pos;
    private Transform transform;
    private short[] voxels = new short[CHUNK_WIDTH * CHUNK_HEIGHT * CHUNK_WIDTH];
    private float[][] lightning = new float[CHUNK_WIDTH * CHUNK_HEIGHT * CHUNK_WIDTH][6];
    private float[] illumination = new float[CHUNK_WIDTH * CHUNK_HEIGHT * CHUNK_WIDTH];
    private ArrayList<Identifier> map = new ArrayList<>();

    private Mesh mesh;

    public Vector2I getPos() {
        return pos;
    }

    public Chunk(Vector2I pos){
        mesh = new Mesh();
        this.pos = pos;
        this.transform = new Transform(new Vector3(pos.getX() * CHUNK_WIDTH, 0, pos.getY() * CHUNK_WIDTH));
    }

    private int get3DIndex(int x, int y, int z){
        return (CHUNK_WIDTH * CHUNK_HEIGHT * z) + (CHUNK_WIDTH * y) + x;
    }

    public void setVoxel(Vector3I pos, Identifier type){
        if(!map.contains(type))
            map.add(type);
        voxels[get3DIndex(pos.getX(), pos.getY(), pos.getZ())] = (short)map.indexOf(type);
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

        return getID(voxels[get3DIndex(x, worldPos.getY(), z)]);
    }

    public Vector3I getLocalPos(int worldX, int worldY, int worldZ){
        int x = worldX - CHUNK_WIDTH * pos.getX();
        int z = worldZ - CHUNK_WIDTH * pos.getY();
        return new Vector3I(x, worldY, z);
    }

    public void setIllumination(int x, int y, int z, float s){
        illumination[get3DIndex(x, y, z)] = s;
    }

    public float getIllumination(Vector3I worldPos){
        int x = worldPos.getX() - CHUNK_WIDTH * pos.getX();
        int z = worldPos.getZ() - CHUNK_WIDTH * pos.getY();

        if(!isVoxelInChunk(x, worldPos.getY(), z))
            return 1f;

        return illumination[get3DIndex(x, worldPos.getY(), z)];
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

    public void rebuildLightmap(boolean rebuildGlobalIllumination){
        lightning = new float[CHUNK_WIDTH * CHUNK_HEIGHT * CHUNK_WIDTH][6];

        if(rebuildGlobalIllumination)
            rebuildGlobalIllumination();

        for (int x = 0; x < CHUNK_WIDTH; x++) {
            for (int y = 0; y < CHUNK_HEIGHT; y++) {
                for (int z = 0; z < CHUNK_WIDTH; z++) {
                    for (int p = 0; p < 6; p++) {
                        Vector3I checkPos = VoxelInformation.faceChecks[p].add(new Vector3I(x, y, z));
                        if(isVoxelInChunk(checkPos.getX(), checkPos.getY(), checkPos.getZ()))
                            lightning[get3DIndex(x, y, z)][p] = illumination[get3DIndex(checkPos.getX(), checkPos.getY(), checkPos.getZ())];
                        else
                            lightning[get3DIndex(x, y, z)][p] = Minecraft.getInstance().getWorld().getLightLevel(checkPos);
                    }
                }
            }
        }

        floodFillIllumination();
    }

    private void rebuildGlobalIllumination(){
        illumination = new float[CHUNK_WIDTH * CHUNK_HEIGHT * CHUNK_WIDTH];

        for (int x = 0; x < CHUNK_WIDTH; x++) {
            for (int z = 0; z < CHUNK_WIDTH; z++) {
                float currentStrength = 1f;
                for (int y = CHUNK_HEIGHT - 1; y >= 0; y--) {
                    if(currentStrength <= 0) {
                        break;
                    }
                    enqueueFloodFillIllumination(x, y, z, currentStrength);
                    currentStrength -= getBlock(voxels[get3DIndex(x, y, z)]).getOpacity();
                }
            }
        }

        floodFillIllumination();
    }

    public void enqueueFloodFillIllumination(int x, int y, int z, float strength){
        illuminationUpdateQueue.add(new illuminationUpdate(x, y, z, strength));
    }

    private class illuminationUpdate{
        public int x, y, z;
        public float strength;

        public illuminationUpdate(int x, int y, int z, float strength){
            this.strength = strength;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    private Queue<illuminationUpdate> illuminationUpdateQueue = new LinkedList<>();

    public void floodFillIllumination(){
        long tile = System.currentTimeMillis();
        while(!illuminationUpdateQueue.isEmpty()) {
            illuminationUpdate update = illuminationUpdateQueue.poll();
            int x = update.x;
            int y = update.y;
            int z = update.z;
            float strength = update.strength;

            if (!isVoxelInChunk(x, y, z))
                continue;

            if (illumination[get3DIndex(x, y, z)] >= strength)
                continue;

            if (strength <= 0)
                continue;

            illumination[get3DIndex(x, y, z)] = strength;
            float decrease = getBlock(voxels[get3DIndex(x, y, z)]).getOpacity();
            float illumination = getBlock(voxels[get3DIndex(x, y, z)]).getIllumination();

            for (int i = 0; i < 6; i++) {
                Vector3I faceCheck = VoxelInformation.faceChecks[i];
                enqueueFloodFillIllumination(x + faceCheck.getX(), y + faceCheck.getY(), z + faceCheck.getZ(), strength - decrease - 0.1f + illumination);
            }


        }

        System.out.println("Reflooding lightmaps took: " + (System.currentTimeMillis() - tile) + "ms");

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
                    Block block = getBlock(voxels[get3DIndex(x, y, z)]);
                    if (block.shouldBuildMesh()) {
                        Vector3 pos = new Vector3(x, y, z);
                        for (int p = 0; p < 6; p++) {
                            int textureID = block.getFaceTexture(VoxelInformation.getFace(p));
                            if(checkVoxel(VoxelInformation.faceChecks[p].add(pos.floor()))) {
                                for (int i = 0; i < 6; i++) {

                                    int triangleIndex = VoxelInformation.voxelTris[p][i];
                                    vertices.add(VoxelInformation.voxelVerts[triangleIndex].add(pos));
                                    triangles.add(vertexIndex);
                                    colors.add(lightning[get3DIndex(x, y, z)][p]);

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

        return getBlock(voxels[get3DIndex(pos.getX(), pos.getY(), pos.getZ())]).renderNeighbourFaces();

    }

    boolean isVoxelInChunk (int x, int y, int z) {

        if (x < 0 || x > CHUNK_WIDTH - 1 || y < 0 || y > CHUNK_HEIGHT - 1 || z < 0 || z > CHUNK_WIDTH - 1)
            return false;
        else return true;

    }

    public void render(){
        mesh.render(Registry.SHADERS.get(Shader.SHADER_BASE), transform);
    }
}
