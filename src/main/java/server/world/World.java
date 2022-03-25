package server.world;

import server.blocks.Block;
import server.entities.Entity;
import server.world.generators.IRWorldGenerator;
import util.*;
import util.registries.Registry;

import java.util.ArrayList;


public class World {
    public Chunk[][] chunks;

    private ArrayList<Entity> entities = new ArrayList<>();

    private final Vector2I BASE_WORLD_SIZE = new Vector2I(16, 16);

    public Entity createEntity(Vector3 position, Identifier identifier){
        try {
            Entity e = Registry.ENTITIES.get(identifier).getConstructor(Vector3.class).newInstance(position);
            entities.add(e);
            return e;
        } catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public World(){
    }

    public void generate(IRWorldGenerator generator){
        chunks = new Chunk[BASE_WORLD_SIZE.getX()][BASE_WORLD_SIZE.getY()];
        for (int x = 0; x < BASE_WORLD_SIZE.getX(); x++){
            for (int y = 0; y < BASE_WORLD_SIZE.getY(); y++){
                chunks[x][y] = generator.getChunk(new Vector2I(x, y));
            }
        }

        for (int x = 0; x < BASE_WORLD_SIZE.getX(); x++) {
            for (int y = 0; y < BASE_WORLD_SIZE.getY(); y++) {
                chunks[x][y].rebuildLightmap();
            }
        }

        for (int x = 0; x < BASE_WORLD_SIZE.getX(); x++) {
            for (int y = 0; y < BASE_WORLD_SIZE.getY(); y++) {
                chunks[x][y].rebuildMesh();
            }
        }
    }

    public Identifier getBlock(Vector3I pos){
        int cx = Math.floorDiv(pos.getX(), Chunk.CHUNK_WIDTH);
        int cy = Math.floorDiv(pos.getZ(), Chunk.CHUNK_WIDTH);

        if(cx < 0 || cx >= BASE_WORLD_SIZE.getX())
            return Block.IDENTIFIER_AIR;

        if(cx < 0 || cx >= BASE_WORLD_SIZE.getX())
            return Block.IDENTIFIER_AIR;

        if(cy < 0 || cy >= BASE_WORLD_SIZE.getY())
            return Block.IDENTIFIER_AIR;

        return chunks[cx][cy].getBlockIdentifier(pos);
    }

    public float getLightLevel(Vector3I pos){
        int cx = Math.floorDiv(pos.getX(), Chunk.CHUNK_WIDTH);
        int cy = Math.floorDiv(pos.getZ(), Chunk.CHUNK_WIDTH);

        if(cx < 0 || cx >= BASE_WORLD_SIZE.getX())
            return 1;

        if(cx < 0 || cx >= BASE_WORLD_SIZE.getX())
            return 1;

        if(cy < 0 || cy >= BASE_WORLD_SIZE.getY())
            return 1;

        return chunks[cx][cy].getIllumination(pos);
    }

    private final Vector2I[] neighbours = new Vector2I[] {
            new Vector2I(1, 0),
            new Vector2I(-1, 0),
            new Vector2I(0, 1),
            new Vector2I(0, -1),
    };

    public Chunk[] getNeighbourChunks(Vector2I chunkPos){
        var n = new ArrayList<Chunk>();
        for (int i = 0; i < neighbours.length; i++){
            Vector2I nPos = chunkPos.add(neighbours[i]);
            if(isValidChunkPos(nPos)){
                n.add( chunks[nPos.getX()][nPos.getY()]);
            }
        }

        return n.toArray(Chunk[]::new);
    }

    private boolean isValidChunkPos(Vector2I pos){
        if(pos.getX() < 0 || pos.getX() >= BASE_WORLD_SIZE.getX())
            return false;

        if(pos.getY() < 0 || pos.getY() >= BASE_WORLD_SIZE.getY())
            return false;

        return true;
    }

    public void setBlock(Vector3I pos, Identifier type){
        int cx = Math.floorDiv(pos.getX(), Chunk.CHUNK_WIDTH);
        int cy = Math.floorDiv(pos.getZ(), Chunk.CHUNK_WIDTH);

        if(cx < 0 || cx >= BASE_WORLD_SIZE.getX())
            return;

        if(cy < 0 || cy >= BASE_WORLD_SIZE.getY())
            return;

        int x = pos.getX() - Chunk.CHUNK_WIDTH * cy;
        int z = pos.getZ() - Chunk.CHUNK_WIDTH * cx;

        chunks[cx][cy].setVoxelWorld(pos, type);
        chunks[cx][cy].rebuildLightmap();
        chunks[cx][cy].rebuildMesh();

        Chunk[] n = getNeighbourChunks(new Vector2I(cx, cy));
        for (Chunk c: n) {
            c.rebuildLightmap();
            c.rebuildMesh();
        }
    }

    public void render(){
        for (int x = 0; x < chunks.length; x++){
            for (int y = 0; y < chunks[x].length; y++){
                chunks[x][y].render();
            }
        }
    }

    public int getEntityCount() {
        return entities.size();
    }

    public void update() {
        for (Entity e : entities) {
            e.update();
        }
    }
}
