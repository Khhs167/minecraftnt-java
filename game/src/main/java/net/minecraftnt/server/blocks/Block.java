package net.minecraftnt.server.blocks;

import net.minecraftnt.server.physics.ColliderAABB;
import net.minecraftnt.util.Identifier;
import net.minecraftnt.util.Vector3;
import net.minecraftnt.util.registries.Registry;

public abstract class Block {
    public static Identifier IDENTIFIER_COBBLESTONE = new Identifier("minecraft", "cobblestone");
    public static Block      BLOCK_COBBLESTONE = Registry.BLOCKS.add(IDENTIFIER_COBBLESTONE, new CobbleStone());

    public static Identifier IDENTIFIER_AIR = new Identifier("minecraft", "air");
    public static Block      BLOCK_AIR = Registry.BLOCKS.add(IDENTIFIER_AIR, new Air());

    public static Identifier IDENTIFIER_GRASS = new Identifier("minecraft", "grass");
    public static Block      BLOCK_GRASS = Registry.BLOCKS.add(IDENTIFIER_GRASS, new Grass());

    public static Identifier IDENTIFIER_STONE = new Identifier("minecraft", "stone");
    public static Block      BLOCK_STONE = Registry.BLOCKS.add(IDENTIFIER_STONE, new Stone());

    public static Identifier IDENTIFIER_DIRT = new Identifier("minecraft", "dirt");
    public static Block      BLOCK_DIRT = Registry.BLOCKS.add(IDENTIFIER_DIRT, new Dirt());

    public static Identifier IDENTIFIER_BEDROCK= new Identifier("minecraft", "bedrock");
    public static Block      BLOCK_BEDROCK = Registry.BLOCKS.add(IDENTIFIER_BEDROCK, new Bedrock());


    public boolean shouldBuildMesh(){
        return true;
    }

    public boolean renderNeighbourFaces(){
        return false;
    }

    public boolean hasCollisions() { return true; }
    public float getOpacity() { return 1f; }
    public float getIllumination() { return 0f; }

    public abstract int getFaceTexture(BlockFace face);

    public ColliderAABB getBoundingBox(Vector3 pos){
        return new ColliderAABB(pos, Vector3.one());
    }
}
