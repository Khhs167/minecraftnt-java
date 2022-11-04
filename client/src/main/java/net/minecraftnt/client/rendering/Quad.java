package net.minecraftnt.client.rendering;

import net.minecraftnt.util.maths.Vector2;
import net.minecraftnt.util.maths.Vector3;

public class Quad {

    public static final int SIZE = (3 + 2 + 2 + 2 + 1);
    public static final int POSITION_OFFSET = 0;
    public static final int SIZE_OFFSET = 3;
    public static final int UV_ORIGIN_OFFSET = SIZE_OFFSET + 2;
    public static final int UV_SIZE_OFFSET = UV_ORIGIN_OFFSET + 2;
    public static final int ORIENTATION_OFFSET = UV_SIZE_OFFSET + 2;
    public static final int VALUE_SIZE = Float.BYTES;

    public Vector3 position;
    public Vector2 size;
    public Vector2 uvOrigin;
    public Vector2 uvSize;
    public int orientation;

    public Quad(Vector3 position, Vector2 size, int orientation) {
        this(position, size, new Vector2(), new Vector2(1), orientation);
    }

    public Quad(Vector3 position, Vector2 size, Vector2 uvOrigin, Vector2 uvSize, int orientation){
        this.position = position;
        this.size = size;
        this.uvOrigin = uvOrigin;
        this.uvSize = uvSize;
        this.orientation = orientation;
    }
}
