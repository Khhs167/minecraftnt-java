package net.minecraftnt.client.rendering;

public class Vertex {
    public static final int VERTEX_FLOAT_COUNT = 8;
    public static final int BYTES = VERTEX_FLOAT_COUNT * Float.BYTES;
    public static final int UV_OFFSET = 3;
    public static final int UV_OFFSET_BYTES = UV_OFFSET * Float.BYTES;
    public static final int COLOR_OFFSET = UV_OFFSET + 2;
    public static final int COLOR_OFFSET_BYTES = COLOR_OFFSET * Float.BYTES;

    public static float[] GenerateFloatBuffer(Mesh mesh){
        float[] buffer = new float[mesh.vertices.length * VERTEX_FLOAT_COUNT];

        for (int v = 0; v < mesh.vertices.length; v++){
            buffer[VERTEX_FLOAT_COUNT * v] = mesh.vertices[v].getX();
            buffer[VERTEX_FLOAT_COUNT * v + 1] = mesh.vertices[v].getY();
            buffer[VERTEX_FLOAT_COUNT * v + 2] = mesh.vertices[v].getZ();

            buffer[VERTEX_FLOAT_COUNT * v + UV_OFFSET] = mesh.uv[v].getX();
            buffer[VERTEX_FLOAT_COUNT * v + UV_OFFSET + 1] = mesh.uv[v].getY();

            if(mesh.lightning.length > 0) {
                buffer[VERTEX_FLOAT_COUNT * v + COLOR_OFFSET] = mesh.lightning[v];
            }
            else {
                buffer[VERTEX_FLOAT_COUNT * v + COLOR_OFFSET] = 0;
                buffer[VERTEX_FLOAT_COUNT * v + COLOR_OFFSET + 1] = 0;
                buffer[VERTEX_FLOAT_COUNT * v + COLOR_OFFSET + 1] = 0;
            }
        }

        return buffer;
    }
}
