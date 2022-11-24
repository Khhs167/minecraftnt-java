package net.minecraftnt;

import net.minecraftnt.rendering.*;
import net.minecraftnt.util.Registry;
import net.minecraftnt.world.Block;
import net.minecraftnt.world.IBiomeGenerator;
import net.minecraftnt.world.IWorldGenerator;

public class Registries {
    public static final Registry<ShapeGenerator> SHAPE_GENERATOR = new Registry<>();
    public static final Registry<Shader> SHADER = new Registry<>();
    public static final Registry<Texture> TEXTURE = new Registry<>();
    public static final Registry<TextureAtlasLocation> TEXTURE_ATLAS_LOC = new Registry<>();
    public static final Registry<Block> BLOCKS = new Registry<>();
    public static final Registry<RenderAPI> RENDER_API = new Registry<>();
    public static final Registry<IBiomeGenerator> BIOME_GENERATOR = new Registry<>();
    public static final Registry<IWorldGenerator> WORLD_GENERATOR = new Registry<>();
}
