package net.minecraftnt;

import net.minecraftnt.entities.Entity;
import net.minecraftnt.entities.Pawn;
import net.minecraftnt.rendering.*;
import net.minecraftnt.util.Registry;
import net.minecraftnt.server.world.Block;
import net.minecraftnt.server.world.BiomeGenerator;
import net.minecraftnt.server.world.WorldGenerator;

public class Registries {
    public static final Registry<ShapeGenerator> SHAPE_GENERATOR = new Registry<>();
    public static final Registry<Shader> SHADER = new Registry<>();
    public static final Registry<Texture> TEXTURE = new Registry<>();
    public static final Registry<TextureAtlasLocation> TEXTURE_ATLAS_LOC = new Registry<>(TextureAtlasLocation.NULL);
    public static final Registry<Block> BLOCKS = new Registry<>();
    public static final Registry<RenderAPI> RENDER_API = new Registry<>();
    public static final Registry<BiomeGenerator> BIOME_GENERATOR = new Registry<>();
    public static final Registry<WorldGenerator> WORLD_GENERATOR = new Registry<>();
    public static final Registry<Entity> ENTITIES = new Registry<>();
    public static final Registry<Pawn> PAWNS = new Registry<>();
}
