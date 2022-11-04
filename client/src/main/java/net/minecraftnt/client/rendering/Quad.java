package net.minecraftnt.client.rendering;

import net.minecraftnt.utility.maths.Vector2;
import net.minecraftnt.utility.maths.Vector3;

public class Quad {
    public Vector3 position;
    public Vector2 size;
    public int orientation;

    public Quad(Vector3 position, Vector2 size, int orientation){
        this.position = position;
        this.size = size;
        this.orientation = orientation;
    }
}
