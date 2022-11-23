package net.minecraftnt;

import net.minecraftnt.rendering.Shader;
import net.minecraftnt.rendering.ShapeGenerator;
import net.minecraftnt.rendering.Texture;
import net.minecraftnt.rendering.TextureAtlasLocation;
import net.minecraftnt.util.Registry;
import net.minecraftnt.world.Block;

public class Registries {
    public static final Registry<ShapeGenerator> SHAPE_GENERATOR = new Registry<>();
    public static final Registry<Shader> SHADER = new Registry<>();
    public static final Registry<Texture> TEXTURE = new Registry<>();
    public static final Registry<TextureAtlasLocation> TEXTURE_ATLAS_LOC = new Registry<>();
    public static final Registry<Block> BLOCKS = new Registry<>();
}
