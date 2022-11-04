package net.minecraftnt.client.rendering;

import java.util.LinkedList;

import static org.lwjgl.opengl.GL33C.*;

public class RectangleMesh {
    private final int VAO;
    private final int VBO;

    public LinkedList<Quad> quads = new LinkedList<>();
    private int quads_baked = 0;


    public RectangleMesh() {

        VAO = glGenVertexArrays();
        VBO = glGenBuffers();

        updateMesh();

    }

    public void updateMesh() {

        float[] vertex_data = new float[Quad.SIZE * quads.size()];

        for(int i = 0; i < quads.size(); i++){
            vertex_data[i * Quad.SIZE + Quad.POSITION_OFFSET] = quads.get(i).position.getX();
            vertex_data[i * Quad.SIZE + Quad.POSITION_OFFSET + 1] = quads.get(i).position.getY();
            vertex_data[i * Quad.SIZE + Quad.POSITION_OFFSET + 2] = quads.get(i).position.getZ();

            vertex_data[i * Quad.SIZE + Quad.SIZE_OFFSET] = quads.get(i).size.getX();
            vertex_data[i * Quad.SIZE + Quad.SIZE_OFFSET + 1] = quads.get(i).size.getY();

            vertex_data[i * Quad.SIZE + Quad.UV_ORIGIN_OFFSET] = quads.get(i).uvOrigin.getX();
            vertex_data[i * Quad.SIZE + Quad.UV_ORIGIN_OFFSET + 1] = quads.get(i).uvOrigin.getY();

            vertex_data[i * Quad.SIZE + Quad.UV_SIZE_OFFSET] = quads.get(i).uvSize.getX();
            vertex_data[i * Quad.SIZE + Quad.UV_SIZE_OFFSET + 1] = quads.get(i).uvSize.getY();

            vertex_data[i * Quad.SIZE + Quad.ORIENTATION_OFFSET] = quads.get(i).orientation;
        }


        glBindVertexArray(VAO);

        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertex_data, GL_STATIC_DRAW);

        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, Quad.SIZE * Quad.VALUE_SIZE, Quad.POSITION_OFFSET * Quad.VALUE_SIZE);

        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, Quad.SIZE * Quad.VALUE_SIZE, Quad.SIZE_OFFSET * Quad.VALUE_SIZE);

        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, Quad.SIZE * Quad.VALUE_SIZE, Quad.UV_ORIGIN_OFFSET * Quad.VALUE_SIZE);

        glEnableVertexAttribArray(3);
        glVertexAttribPointer(3, 2, GL_FLOAT, false, Quad.SIZE * Quad.VALUE_SIZE, Quad.UV_SIZE_OFFSET * Quad.VALUE_SIZE);

        glEnableVertexAttribArray(4);
        glVertexAttribPointer(4, 1, GL_FLOAT, false, Quad.SIZE * Quad.VALUE_SIZE, Quad.ORIENTATION_OFFSET * Quad.VALUE_SIZE);

        quads_baked = quads.size();
    }

    public void render() {
        glBindVertexArray(VAO);
        glDrawArrays(GL_POINTS, 0, quads_baked);
    }
}
