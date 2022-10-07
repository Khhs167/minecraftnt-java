package net.minecraftnt.client.ui.fonts;

import net.minecraftnt.client.rendering.Mesh;
import net.minecraftnt.util.Transform;
import net.minecraftnt.util.Vector2;
import net.minecraftnt.util.Vector3;

public class Character {
    private final int id;
    private Mesh mesh;

    public static final int ATLAS_WIDTH = 32;
    public static final float ATLAS_TEXTURE_UV_SIZE = 1f / (float)ATLAS_WIDTH;
    public static final float ATLAS_TEXTURE_UV_Y_SCALE = 1.25f;
    public static final float ATLAS_TEXTURE_UV_Y_SIZE = ATLAS_TEXTURE_UV_SIZE * ATLAS_TEXTURE_UV_Y_SCALE;
    public static final int FONT_START  = 32;
    public static final int FONT_LEN    = 256 - FONT_START;
    public static final int FONT_WIDTH = 32;
    public static final int FONT_HEIGHT = 64;


    private Vector2 convertUV(Vector2 uv, int textureID){

        float y = textureID / ATLAS_WIDTH;
        float x = textureID - (y * ATLAS_WIDTH);

        x *= ATLAS_TEXTURE_UV_SIZE;
        y *= ATLAS_TEXTURE_UV_Y_SIZE;

        y = 1f - y - ATLAS_TEXTURE_UV_Y_SIZE;

        return new Vector2(x + uv.getX() * ATLAS_TEXTURE_UV_SIZE, -(y + uv.getY() * ATLAS_TEXTURE_UV_Y_SIZE));
    }


    public Character(int id){
        this.id = id;
        mesh = new Mesh();

        mesh.vertices = new Vector3[]{
                new Vector3(0, 0, 0),
                new Vector3(1, 0, 0),
                new Vector3(0, ATLAS_TEXTURE_UV_Y_SCALE, 0),
                new Vector3(1, ATLAS_TEXTURE_UV_Y_SCALE, 0),
        };

        mesh.uv = new Vector2[]{
                convertUV(new Vector2(0f, 0f), id - FONT_START),
                convertUV(new Vector2(1f, 0f), id - FONT_START),
                convertUV(new Vector2(0f, 1f), id - FONT_START),
                convertUV(new Vector2(1f, 1f), id - FONT_START)
        };

        mesh.triangles = new int[]{
                0, 1, 2,
                1, 3, 2
        };

        mesh.buildMesh();
    }

    public void render(Transform transform){

        mesh.renderNoPrep();
    }
}
