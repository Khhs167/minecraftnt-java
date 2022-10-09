package net.minecraftnt.client.rendering;

import net.minecraftnt.client.ClientMainHandler;
import net.minecraftnt.util.Vector3;
import net.minecraftnt.util.registries.Registry;
import org.lwjgl.system.MemoryUtil;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL30.*;

/**
 * A rendering API for quick debug rendering. Less powerful than glBegin/glEnd and designed for OpenGL 3.3
 */
public final class DirectRenderer {

    private static final Logger LOGGER = LogManager.getLogger(DirectRenderer.class);

    /**
     * A render type for {@link #drEndDraw}. Please do not use these values, but instead the predetermined in the {@link DirectRenderer} class
     */
    private enum DirectRenderType {
        DIRECTRENDERER_LINES(GL_LINES),
        DIRECTRENDERER_TRIANGLES(GL_TRIANGLES),
        DIRECTRENDERER_POINTS(GL_POINTS);

        private int gl_value;

        DirectRenderType(int gl_value) {
            this.gl_value = gl_value;
        }
    }

    /**
     * Render using lines, 2 vertices per primitive
     */
    public static final DirectRenderType DIRECTRENDERER_LINES = DirectRenderType.DIRECTRENDERER_LINES;

    /**
     * Render using triangles, 3 vertices per primitive
     */
    public static final DirectRenderType DIRECTRENDERER_TRIANGLES = DirectRenderType.DIRECTRENDERER_TRIANGLES;

    /**
     * Render using points, 1 vertex per primitive
     */
    public static final DirectRenderType DIRECTRENDERER_POINTS = DirectRenderType.DIRECTRENDERER_POINTS;

    private static int VAO = -1;
    private static int VBO = -1;

    private static ArrayList<Float> VERTICES;
    private static int vertexCount;
    private static final VertexInfo vertex = new VertexInfo();

    private static void checkContext(){
        if(VAO == -1) {
            LOGGER.fatal("Attempt to call without context");
            throw new IllegalStateException("Call to invalid DirectRenderer context");
        }
    }


    /**
     * Initialize the DirectRenderer context
     */
    public static void drInitialize(){

        LOGGER.info("Initializing DirectRenderer context");

        VAO = glGenVertexArrays();
        VBO = glGenBuffers();
        VERTICES = new ArrayList<>();
        glEnable(GL_LINE_SMOOTH);
    }

    /**
     * Begin a new mesh.
     */
    public static void drBeginDraw(){
        checkContext();
        VERTICES.clear();
        vertexCount = 0;
    }

    /**
     * End drawing the mesh and flush to screen
     * @param render_mode The render mode to use
     */
    public static void drEndDraw(DirectRenderType render_mode) {

        float[] vertexBuffer = new float[VERTICES.size()];
        int vertexBufferPointer = 0;
        for(int i = 0; i < VERTICES.size(); i++){
            vertexBuffer[vertexBufferPointer] = VERTICES.get(i);
            vertexBufferPointer++;
        }

        glBindVertexArray(VAO);
        glBindBuffer(GL_ARRAY_BUFFER, VBO);

        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_DYNAMIC_DRAW);


        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, VertexInfo.BYTES, 0);

        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, VertexInfo.BYTES, VertexInfo.COLOR_OFFSET_BYTES);

        Shader program = Registry.SHADERS.get(Shader.SHADER_DIRECT);


        program.bind();

        FloatBuffer matrix4buffer = MemoryUtil.memAllocFloat(16);

        ClientMainHandler.getInstance().getCamera().getProjectionMatrix().get(matrix4buffer);
        glUniformMatrix4fv(program.getUniformLocation("mat_projection"), false, matrix4buffer);

        ClientMainHandler.getInstance().getCamera().getViewMatrix().get(matrix4buffer);
        glUniformMatrix4fv(program.getUniformLocation("mat_view"), false, matrix4buffer);

        glBindVertexArray(VAO);
        glDrawArrays(render_mode.gl_value, 0, vertexCount);
        glBindVertexArray(0);
        program.unbind();

        MemoryUtil.memFree(matrix4buffer);
    }

    /**
     * Begin a new vertex, with default {@link #drVertexPosition positon} and {@link #drVertexColour colour}
     */
    public static void drBeginVertex() {
        // Do nothing
        return;
    }

    /**
     * Set the position of the current vertex
     * @param pos The position to set it to
     */
    public static void drVertexPosition(Vector3 pos){
        vertex.position = pos;
    }

    /**
     * Set the position of the current vertex.
     * Internally calls {@link #drVertexPosition(Vector3)}, and creates a new object.
     * In order to increase speeds, creating the Vector3 object helps when using the same coords many times
     * @param x The X position to set to
     * @param y The Y position to set to
     * @param z The Z position to set to
     */
    public static void drVertexPosition(float x, float y, float z){
        drVertexPosition(new Vector3(x, y, z));
    }

    /**
     * Set the colour of the current vertex
     * @param col The colour to set it to
     */
    public static void drVertexColour(Vector3 col){
        vertex.color = col;
    }

    /**
     * Set the colour of the current vertex.
     * Internally calls {@link #drVertexColour(Vector3)}, and creates a new object.
     * In order to increase speeds, creating the Vector3 object helps when using the same coords many times
     * @param r The red level to set to
     * @param g The green level to set to
     * @param b The blue level to set to
     */
    public static void drVertexColour(float r, float g, float b){
        drVertexColour(new Vector3(r, g, b));
    }

    /**
     * End the current vertex and flush to draw queue.
     */
    public static void drEndVertex() {
        VERTICES.add(vertex.position.getX());
        VERTICES.add(vertex.position.getY());
        VERTICES.add(vertex.position.getZ());

        VERTICES.add(vertex.color.getX());
        VERTICES.add(vertex.color.getY());
        VERTICES.add(vertex.color.getZ());

        vertexCount++;
    }

    public static void drClose() {
        glDeleteVertexArrays(VAO);
        glDeleteBuffers(VBO);
    }

    /**
     * Private class to keep track of vertices.
     * Do not use
     */
    private static class VertexInfo {

        public static final int VERTEX_FLOAT_COUNT = 6;
        public static final int BYTES = VERTEX_FLOAT_COUNT * Float.BYTES;
        public static final int COLOR_OFFSET = 3;
        public static final int COLOR_OFFSET_BYTES = COLOR_OFFSET * Float.BYTES;

        public Vector3 position = Vector3.zero();
        public Vector3 color = Vector3.zero();
    }
}
