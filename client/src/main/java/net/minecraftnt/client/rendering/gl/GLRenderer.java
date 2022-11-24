package net.minecraftnt.client.rendering.gl;

import net.minecraftnt.rendering.MeshProvider;
import net.minecraftnt.rendering.Renderer;
import net.minecraftnt.rendering.ShaderProvider;
import net.minecraftnt.rendering.TextureProvider;

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
