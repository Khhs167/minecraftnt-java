package net.minecraftnt.client.rendering;

import java.util.LinkedList;

import static org.lwjgl.opengl.GL33C.*;

public class RectangleMesh {
    private final int VAO;
    private final int VBO;

    public Quad[] quads = new Quad[0];
    private int quads_baked = 0;


    public RectangleMesh() {

        VAO = glGenVertexArrays();
        VBO = glGenBuffers();

        updateMesh();

    }

    private void setCorner(int start, int quad, int corner, float[] data) {
        data[start] = quads[quad].positions()[0].getX();
        data[start + 1] = quads[corner].positions()[0].getY();
        data[start + 2] = quads[corner].positions()[0].getZ();
        data[start + 3] = quads[corner].uv()[0].getX();
        data[start + 4] = quads[corner].uv()[0].getY();
        data[start + 5] = quads[corner].lighting();
    }

    public void updateMesh() {
        final int size = 6 * 6;
        float[] vertex_data = new float[size * quads.length];

        for(int i = 0; i < quads.length; i++){

            // Triangle 1

            setCorner(i * size, i, 0, vertex_data);
            setCorner(i * size + 6, i, 1, vertex_data);
            setCorner(i * size + 12, i, 4, vertex_data);


            // Triangle 2

            setCorner(i * size + 18, i, 0, vertex_data);
            setCorner(i * size + 24, i, 1, vertex_data);
            setCorner(i * size + 30, i, 4, vertex_data);
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

        quads_baked = quads.length;
    }

    public void render() {
        glBindVertexArray(VAO);
        glDrawArrays(GL_TRIANGLES, 0, quads_baked);
    }
}
