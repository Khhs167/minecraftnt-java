package net.minecraftnt.rendering;

import net.minecraftnt.rendering.Mesh;
import net.minecraftnt.rendering.Vertex;

public abstract class MeshProvider {
    public abstract void updateMesh(Mesh mesh);
    public abstract void render(Mesh mesh);
    public abstract Mesh createMesh();
}
