package net.minecraftnt.client.rendering.gl;

import net.minecraftnt.rendering.Mesh;

import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11C.glDrawArrays;
import static org.lwjgl.opengl.GL15C.*;
import static org.lwjgl.opengl.GL20C.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20C.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;

public class GLMesh extends Mesh {
    private final int VAO;
    private final int VBO;
    private int vertices_baked = 0;

    public int getVertices_baked() {
        return vertices_baked;
    }

    public void setVertices_baked(int vertices_baked) {
        this.vertices_baked = vertices_baked;
    }

    public int getVAO() {
        return VAO;
    }

    public int getVBO() {
        return VBO;
    }

    public GLMesh(int vao, int vbo) {
        VAO = vao;
        VBO = vbo;
    }

    public void updateMesh() {
        final int size = 6;
        float[] vertex_data = new float[size * vertices.length];

        for(int i = 0; i < vertices.length; i++){
            vertex_data[i * size] = vertices[i].pos().getX();
            vertex_data[i * size + 1] = vertices[i].pos().getY();
            vertex_data[i * size + 2] = vertices[i].pos().getZ();

            vertex_data[i * size + 3] = vertices[i].uv().getX();
            vertex_data[i * size + 4] = vertices[i].uv().getY();

            vertex_data[i * size + 5] = vertices[i].lighting();
        }


        glBindVertexArray(VAO);

        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertex_data, GL_STATIC_DRAW);

        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, size * Float.BYTES, 0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, size * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 1, GL_FLOAT, false, size * Float.BYTES, 5 * Float.BYTES);

        vertices_baked = vertices.length;
    }

    public void render() {
        glBindVertexArray(VAO);
        glDrawArrays(GL_TRIANGLES, 0, vertices_baked);
    }
}
