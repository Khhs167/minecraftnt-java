package net.minecraftnt.utility.maths;

public class Vector3 {
    private final float x, y, z;

    public Vector3() {
        this(0);
    }

    public Vector3(Vector2 v, float e) {
        this(v.getX(), v.getY(), e);
    }

    public Vector3(float v) {
        this(v, v, v);
    }

    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public Vector3 clone(){
        return new Vector3(x, y, z);
    }

    public Vector3 subtract(Vector3 other){
        return add(other.negated());
    }

    public Vector3 negated() {
        return new Vector3(-x, -y, -z);
    }

    public Vector3 add(Vector3 other){
        return new Vector3(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    public Vector3 divide(float v){
        return new Vector3(this.x / v, this.y / v, this.z / v);
    }

    public Vector3 multiply(float v){
        return new Vector3(this.x * v, this.y * v, this.z * v);
    }

    public float lengthSquared() {
        return (x * x) + (y * y) + (z * z);
    }

    public float length() {
        return (float)Math.sqrt(lengthSquared());
    }

    public Vector3 normalized() {
        return this.divide(this.length());
    }

    public static Vector3 cross(Vector3 left, Vector3 right){
        float X = (left.y * right.z) - (left.z * right.y);
        float Y = (left.z * right.x) - (left.x * right.z);
        float Z = (left.x * right.y) - (left.y * right.x);
        return new Vector3(X, Y, Z);
    }

    public static float dot(Vector3 left, Vector3 right)
    {
        return (left.x * right.x) + (left.y * right.y) + (left.z * right.z);
    }

}
