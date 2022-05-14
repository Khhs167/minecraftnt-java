package client.rendering;

import org.lwjgl.system.MemoryUtil;
import server.Minecraft;
import util.Transform;
import util.Vector2;
import util.Vector3;


import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL33.*;

public class Mesh {
    private int vao;
    private int vbo;
    private int ebo;

    private int bakedTris = 0;
    public Vector3[] vertices;
    public Vector2[] uv;
    public Float[] lightning = new Float[0];
    public int[] triangles;

    public Mesh(){
        vertices = new Vector3[0];
        triangles = new int[0];
        vao = glGenVertexArrays();
        vbo = glGenBuffers();
        ebo = glGenBuffers();

    }

    public void buildMesh(){
        bakedTris = triangles.length;
        float[] vertexBuffer = Vertex.GenerateFloatBuffer(this);

        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_DYNAMIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, triangles, GL_DYNAMIC_DRAW);

        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, Vertex.BYTES, 0);

        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, Vertex.BYTES, Vertex.UV_OFFSET_BYTES);

        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 1, GL_FLOAT, false, Vertex.BYTES, Vertex.COLOR_OFFSET_BYTES);

        glBindVertexArray(0);
    }

    public void render(Shader program, Transform position){
        program.bind();

        FloatBuffer matrix4buffer = MemoryUtil.memAllocFloat(16);

        Minecraft.getInstance().getCamera().getProjectionMatrix().get(matrix4buffer);
        glUniformMatrix4fv(program.getUniformLocation("mat_projection"), false, matrix4buffer);

        position.getMatrix().get(matrix4buffer);
        glUniformMatrix4fv(program.getUniformLocation("mat_world"), false, matrix4buffer);

        Minecraft.getInstance().getCamera().getViewMatrix().get(matrix4buffer);
        glUniformMatrix4fv(program.getUniformLocation("mat_view"), false, matrix4buffer);

        glBindVertexArray(vao);
        glDrawElements(GL_TRIANGLES, triangles.length, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
        program.unbind();

        MemoryUtil.memFree(matrix4buffer);
    }

    public void renderNoPrep(){
        glBindVertexArray(vao);
        glDrawElements(GL_TRIANGLES, triangles.length, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }
}
