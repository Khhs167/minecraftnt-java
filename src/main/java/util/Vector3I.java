package util;

public class Vector3I {
    private int x, y, z = 0;

    public Vector3I(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3I(){

    }

    public int getX() {
        return x;
    }

    public int getY(){
        return y;
    }

    public int getZ() { return z; }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) { this.z = z; }

    public Vector3I clone(){
        return new Vector3I(x, y, z);
    }

    public Vector3I multiply(float v){
        Vector3I t = clone();
        t.x *= v;
        t.y *= v;
        t.z *= v;
        return t;
    }

    public Vector3I add(Vector3I v){
        Vector3I t = clone();
        t.x += v.getX();
        t.y += v.getY();
        t.z += v.getZ();
        return t;
    }

    public Vector3I onlyX(){
        return new Vector3I(x, 0, 0);
    }

    public Vector3I onlyY(){
        return new Vector3I(0, y, 0);
    }

    public Vector3I onlyZ(){
        return new Vector3I(0, 0, z);
    }

    public Vector2I xz() { return  new Vector2I(x, z); }

    public Vector3I negate(){
        return multiply(-1);
    }

    public float length(){
        return (float)Math.sqrt(lengthSquared());
    }

    private float lengthSquared() {
        return x * x + y * y + z * z;
    }

    public Vector3I normalize() {
        float l = length();
        Vector3I t = clone();
        t.x /= l;
        t.y /= l;
        t.z /= l;

        return t;
    }
}
