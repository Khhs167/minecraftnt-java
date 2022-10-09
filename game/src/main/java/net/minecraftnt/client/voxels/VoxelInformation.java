package net.minecraftnt.client.voxels;

import net.minecraftnt.server.blocks.BlockFace;
import net.minecraftnt.util.Vector2;
import net.minecraftnt.util.Vector3;
import net.minecraftnt.util.Vector3I;

public class VoxelInformation {
    public static final int[][] voxelTris = new int[][]{

            {0, 3, 1, 1, 3, 2}, // Back Face
            {5, 6, 4, 4, 6, 7}, // Front Face
            {3, 7, 2, 2, 7, 6}, // Top Face
            {1, 5, 0, 0, 5, 4}, // Bottom Face
            {4, 7, 0, 0, 7, 3}, // Left Face
            {1, 2, 5, 5, 2, 6} // Right Face

    };

    public static BlockFace getFace(int face){
        switch (face){
            case 0:
                return BlockFace.BACK_FACE;
            case 1:
                return BlockFace.FRONT_FACE;
            case 2:
                return BlockFace.TOP_FACE;
            case 3:
                return BlockFace.BOTTOM_FACE;
            case 4:
                return BlockFace.LEFT_FACE;
            case 5:
                return BlockFace.RIGHT_FACE;
        }
        return BlockFace.TOP_FACE;
    }

    public static final Vector3[] voxelVerts =new Vector3[]{
            new Vector3(0.0f, 0.0f, 0.0f),
                new Vector3(1.0f, 0.0f, 0.0f),
                new Vector3(1.0f, 1.0f, 0.0f),
                new Vector3(0.0f, 1.0f, 0.0f),
                new Vector3(0.0f, 0.0f, 1.0f),
                new Vector3(1.0f, 0.0f, 1.0f),
                new Vector3(1.0f, 1.0f, 1.0f),
                new Vector3(0.0f, 1.0f, 1.0f),

    };

    public static final Vector2[] voxelUvs = new Vector2[]{
        new Vector2(0.0f, 0.0f),
                new Vector2(0.0f, 1.0f),
                new Vector2(1.0f, 0.0f),
                new Vector2(1.0f, 0.0f),
                new Vector2(0.0f, 1.0f),
                new Vector2(1.0f, 1.0f)

    };

    public static final Vector3I[] faceChecks = new Vector3I[] {

        new Vector3I(0, 0, -1),
                new Vector3I(0, 0, 1),
                new Vector3I(0, 1, 0),
                new Vector3I(0, -1, 0),
                new Vector3I(-1, 0, 0),
                new Vector3I(1, 0, 0)

    };
}
