package net.minecraftnt.client.rendering;

import net.minecraftnt.rendering.Mesh;

import java.lang.reflect.InvocationTargetException;

public abstract class Renderer {
    private static Renderer instance;
    public static Renderer get(){
        if(instance == null)
            create(RenderAPI.OPENGL);
        return instance;
    }

    public static Renderer create(RenderAPI api) {
        try {
            instance = (Renderer) api.renderer.getDeclaredConstructor().newInstance();
            return instance;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract ShaderProvider shaderProvider();
    public abstract TextureProvider textureProvider();
    public abstract MeshProvider meshProvider();

    public static ShaderProvider shaderProviderC() {
        return get().shaderProvider();
    }

    public static Mesh createMeshC() {
        return get().meshProvider().createMesh();
    }
    public static void updateMeshC(Mesh mesh) {
        get().meshProvider().updateMesh(mesh);
    }
    public static void renderMeshC(Mesh mesh) {
         get().meshProvider().render(mesh);
    }

    public static TextureProvider textureProviderC() {
        return get().textureProvider();
    }
}
