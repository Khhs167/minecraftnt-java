package net.minecraftnt.rendering;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class Mesh {
    protected Vertex[] vertices = new Vertex[0];
    protected float[] vertex_data = new float[0];
    protected final int size = 6;

    public void setVertices(Vertex[] vertices) {
        this.vertices = vertices;


        vertex_data = new float[size * vertices.length];

        for(int i = 0; i < vertices.length; i++){
            vertex_data[i * size] = vertices[i].pos().getX();
            vertex_data[i * size + 1] = vertices[i].pos().getY();
            vertex_data[i * size + 2] = vertices[i].pos().getZ();

            vertex_data[i * size + 3] = vertices[i].uv().getX();
            vertex_data[i * size + 4] = vertices[i].uv().getY();

            vertex_data[i * size + 5] = vertices[i].lighting();
        }

    }

    public Lock lock = new ReentrantLock();
}
