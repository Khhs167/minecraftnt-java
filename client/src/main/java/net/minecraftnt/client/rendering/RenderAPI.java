package net.minecraftnt.client.rendering;

import net.minecraftnt.client.rendering.gl.GLRenderer;

public enum RenderAPI {
    OPENGL(0, GLRenderer.class);
    public int id;
    public Class<?> renderer;

    RenderAPI(int id, Class<?> renderer) {
        this.id = id;
        this.renderer = renderer;
    }
}
