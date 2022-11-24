package net.minecraftnt.rendering;

import net.minecraftnt.util.Identifier;

public class RenderAPI {

    public static final Identifier OPENGL = new Identifier("minecraftnt", "renderer.opengl");

    public int id;
    public Renderer renderer;

    public RenderAPI(int id, Renderer renderer) {
        this.id = id;
        this.renderer = renderer;
    }
}
