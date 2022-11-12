package net.minecraftnt.util.maths;

public class Vector2 {
    private final float x, y;

    public Vector2() {
        this(0);
    }

    public Vector2(float v) {
        this(v, v);
    }

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
