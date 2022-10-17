package net.minecraftnt.util;

public class Vector3 {
    private float x = 0, y = 0, z = 0;

    public static Vector3 zero(){
        return new Vector3();
    }

    public static Vector3 one(){
        return new Vector3(1, 1, 1);
    }

    public static Vector3 up(){
        return new Vector3(0, 1, 0);
    }

    public static Vector3 right(){
        return new Vector3(1, 0, 0);
    }

    public static Vector3 forward(){
        return new Vector3(0, 0, 1);
    }

    public static Vector3 down(){
        return new Vector3(0, -1, 0);
    }

    public static Vector3 left(){
        return new Vector3(-1, 0, 0);
    }

    public static Vector3 backward(){
        return new Vector3(0, 0, -1);
    }

    public Vector3(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(){

    }

    public float getX() {
        return x;
    }

    public float getY(){
        return y;
    }

    public float getZ() { return z; }

    public Vector3 setX(float x) {
        this.x = x;
        return this;
    }

    public Vector3 setY(float y) {
        this.y = y;
        return this;
    }

    public Vector3 setZ(float z) {
        this.z = z;
        return this;
    }

    public Vector3 clone(){
        return new Vector3(x, y, z);
    }

    public Vector3 multiply(float v){
        Vector3 t = clone();
        t.x *= v;
        t.y *= v;
        t.z *= v;
        return t;
    }

    public Vector3 add(Vector3 v){
        Vector3 t = clone();
        t.x += v.getX();
        t.y += v.getY();
        t.z += v.getZ();
        return t;
    }

    public Vector3I floor(){
        return new Vector3I(
                (int)Math.floor(x),
                (int)Math.floor(y),
                (int)Math.floor(z));
    }

    public Vector3 onlyX(){
        return new Vector3(x, 0, 0);
    }

    public Vector3 onlyY(){
        return new Vector3(0, y, 0);
    }

    public Vector3 onlyZ(){
        return new Vector3(0, 0, z);
    }

    public Vector2 xz() { return  new Vector2(x, z); }

    public Vector3 negate(){
        return multiply(-1);
    }

    public float length(){
        return (float)Math.sqrt(lengthSquared());
    }

    private float lengthSquared() {
        return x * x + y * y + z * z;
    }

    public Vector3 normalize() {
        float l = length();
        Vector3 t = clone();
        t.x /= l;
        t.y /= l;
        t.z /= l;

        return t;
    }

    public Vector3 addX(float v){
        Vector3 o = this.clone();
        o.x += v;
        return o;
    }

    public Vector3 addY(float v){
        Vector3 o = this.clone();
        o.y += v;
        return o;
    }

    public Vector3 addZ(float v){
        Vector3 o = this.clone();
        o.z += v;
        return o;
    }

    public Vector3 sub(Vector3 v){
        return add(v);
    }

    public Vector3 absolute(){
        return new Vector3(Math.abs(this.x), Math.abs(this.y), Math.abs(this.z));
    }

    @Override
    public String toString() {
        return "Vector3{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
