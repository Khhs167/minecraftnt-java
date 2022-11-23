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

    public Vector2 clone(){
        return new Vector2(x, y);
    }

    public Vector2 subtract(Vector2 other){
        return add(other.negated());
    }

    public Vector2 negated() {
        return new Vector2(-x, -y);
    }

    public Vector2 add(Vector2 other){
        return new Vector2(this.x + other.x, this.y + other.y);
    }

    public Vector2 divide(float v){
        return new Vector2(this.x / v, this.y / v);
    }

    public Vector2 multiply(float v){
        return new Vector2(this.x * v, this.y * v);
    }

    public float lengthSquared() {
        return (x * x) + (y * y);
    }

    public float length() {
        return (float)Math.sqrt(lengthSquared());
    }

    public Vector2 normalized() {
        return this.divide(this.length());
    }
}
