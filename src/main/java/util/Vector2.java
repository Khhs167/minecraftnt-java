package util;

public class Vector2 {
    private float x, y = 0;

    public Vector2(float x, float y){
        this.x = x;
        this.y = y;
    }

    public Vector2(){

    }

    public float getX() {
        return x;
    }

    public float getY(){
        return y;
    }

    public void setX(float x) { this.x = x; }

    public void setY(float y) { this.y = y; }

    public Vector2 clone(){
        return new Vector2(x, y);
    }

    public Vector2 multiply(float v){
        Vector2 t = clone();
        t.x *= v;
        t.y *= v;
        return t;
    }

    public Vector2 add(Vector2 v){
        Vector2 t = clone();
        t.x += v.getX();
        t.y += v.getY();
        return t;
    }

    public Vector2I floor(){
        return new Vector2I(
                (int)Math.floor(x),
                (int)Math.floor(y));
    }

    public Vector2 negate(){
        return multiply(-1);
    }

    public float length(){
        return (float)Math.sqrt(lengthSquared());
    }

    private float lengthSquared() {
        return x * x + y * y;
    }

    public Vector3 Vec3XZ(){
        return new Vector3(x, 0, y);
    }

    public Vector2 normalize() {
        float l = length();
        Vector2 t = clone();
        t.x /= l;
        t.y /= l;

        return t;
    }
}
