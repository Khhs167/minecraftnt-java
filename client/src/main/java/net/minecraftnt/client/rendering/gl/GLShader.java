package net.minecraftnt.client.rendering.gl;

import net.minecraftnt.rendering.Shader;

public class GLShader extends Shader {
    private final int id;

    public int getId() {
        return id;
    }

    public GLShader(int id) {
        this.id = id;
    }
}
