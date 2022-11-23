package net.minecraftnt.client.rendering.gl;

import net.minecraftnt.rendering.Texture;

public class GLTexture extends Texture {
    private final int id;

    public int getId() {
        return id;
    }

    public GLTexture(int id) {
        this.id = id;
    }
}
