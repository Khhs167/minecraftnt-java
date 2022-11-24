package net.minecraftnt.rendering;

import net.minecraftnt.rendering.Shader;
import net.minecraftnt.util.Identifier;
import net.minecraftnt.util.maths.Matrix4;

public abstract class ShaderProvider {
    public abstract Shader load(Identifier identifier);
    public abstract boolean bind(Identifier identifier);
    public abstract boolean setFloat(String name, float value);
    public abstract boolean setProjection(Matrix4 matrix);
    public abstract boolean setView(Matrix4 matrix);
    public abstract boolean setModel(Matrix4 matrix);
    public abstract void dispose();
}
