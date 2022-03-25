package util;

public class Vector2I {
    private int x, y = 0;

    public Vector2I(int x, int y){
        this.x = x;
        this.y = y;
    }

    public Vector2I(){

    }

    public int getX() {
        return x;
    }

    public int getY(){
        return y;
    }

    public void setX(int x) { this.x = x; }

    public void setY(int y) { this.y = y; }

    public Vector2I clone(){
        return new Vector2I(x, y);
    }

    public Vector2I multiply(float v){
        Vector2I t = clone();
        t.x *= v;
        t.y *= v;
        return t;
    }

    public Vector2I add(Vector2I v){
        Vector2I t = clone();
        t.x += v.getX();
        t.y += v.getY();
        return t;
    }

    public Vector2I negate(){
        return multiply(-1);
    }
}
