package net.minecraftnt.client.rendering;

import static org.lwjgl.opengl.GL33C.*;

public class RectangleMesh {
    private final int VAO;
    private final int VBO;

    private static final float[] VERTICES = {
            -0.5f,  0.5f, 0.0f, 0.1f, 0.1f, 0.0f, // top-left
            0.5f,  0.5f, 0.0f, 0.1f, 0.2f, 0.0f,  // top-right
            0.5f, -0.5f, 0.0f, 0.2f, 0.1f, 0.0f,  // bottom-right
            -0.5f, -0.5f, 0.0f, 0.2f, 0.2f,  0.0f // bottom-left
    };


    public RectangleMesh() {

        VAO = glGenVertexArrays();
        VBO = glGenBuffers();

        updateMesh();

    }

    public void updateMesh() {

        glBindVertexArray(VAO);

        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, VERTICES, GL_STATIC_DRAW);

        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);

        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);

        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 1, GL_INT, false, 6 * Float.BYTES, 5 * Float.BYTES);
    }

    public void render() {
        glBindVertexArray(VAO);
        glDrawArrays(GL_POINTS, 0, 4);
    }
}
