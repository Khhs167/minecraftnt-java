package net.minecraftnt.client.rendering;

import java.util.LinkedList;
import java.util.List;

import static org.lwjgl.opengl.GL33C.*;

public class RectangleMesh {
    private final int VAO;
    private final int VBO;

    public LinkedList<Quad> quads = new LinkedList<>();


    public RectangleMesh() {

        VAO = glGenVertexArrays();
        VBO = glGenBuffers();

        updateMesh();

    }

    public void updateMesh() {

        float[] vertex_data = new float[6 * quads.size()];

        for(int i = 0; i < quads.size(); i++){
            vertex_data[i * 6] = quads.get(i).position.getX();
            vertex_data[i * 6 + 1] = quads.get(i).position.getY();
            vertex_data[i * 6 + 2] = quads.get(i).position.getZ();

            vertex_data[i * 6 + 3] = quads.get(i).size.getX();
            vertex_data[i * 6 + 4] = quads.get(i).size.getY();

            vertex_data[i * 6 + 5] = quads.get(i).orientation;
        }


        glBindVertexArray(VAO);

        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertex_data, GL_STATIC_DRAW);

        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);

        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);

        glEnableVertexAttribArray(2);
        glVertexAttribIPointer(2, 1, GL_INT, 6 * Float.BYTES, 5 * Float.BYTES);
    }

    public void render() {
        glBindVertexArray(VAO);
        glDrawArrays(GL_POINTS, 0, 4);
    }
}
