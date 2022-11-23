package net.minecraftnt.client.rendering.gl;

import net.minecraftnt.client.rendering.MeshProvider;
import net.minecraftnt.client.rendering.Renderer;
import net.minecraftnt.client.rendering.ShaderProvider;
import net.minecraftnt.client.rendering.TextureProvider;

public class GLRenderer extends Renderer {

    public GLRenderer() {
        shaderProvider = new GLShaderProvider();
        textureProvider = new GLTextureProvider();
        meshProvider = new GLMeshProvider();
    }

    private final ShaderProvider shaderProvider;
    @Override
    public ShaderProvider shaderProvider() {
        return shaderProvider;
    }

    private final TextureProvider textureProvider;
    @Override
    public TextureProvider textureProvider() {
        return textureProvider;
    }

    private final MeshProvider meshProvider;
    @Override
    public MeshProvider meshProvider() {
        return meshProvider;
    }
}
