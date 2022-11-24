package net.minecraftnt.rendering;

import net.minecraftnt.Registries;
import net.minecraftnt.util.Identifier;
import net.minecraftnt.util.Registry;

import java.lang.reflect.InvocationTargetException;

public abstract class Renderer {
    private static Renderer instance;
    public static Renderer get(){
        if(instance == null)
            create(RenderAPI.OPENGL);
        return instance;
    }

    public static Renderer create(Identifier identifier) {

        RenderAPI api = Registries.RENDER_API.get(identifier);

        instance = api.renderer;

        return instance;

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
