package net.minecraftnt.client.rendering.gl;

import net.minecraftnt.client.rendering.MeshProvider;
import net.minecraftnt.rendering.Mesh;

import static org.lwjgl.opengl.GL33C.*;

public class GLMeshProvider extends MeshProvider {

    @Override
    public void updateMesh(Mesh mesh) {
        ((GLMesh)mesh).updateMesh();
    }

    @Override
    public void render(Mesh mesh) {
        ((GLMesh)mesh).render();
    }

    public GLMesh createMesh() {
        GLMesh mesh = new GLMesh(glGenVertexArrays(), glGenBuffers());
        mesh.updateMesh();
        return mesh;
    }


}
