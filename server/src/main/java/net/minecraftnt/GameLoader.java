package net.minecraftnt;

import net.minecraftnt.api.ModLoadingImplementation;
import net.minecraftnt.builtin.TerrainGenerator;
import net.minecraftnt.builtin.blocks.Air;
import net.minecraftnt.builtin.blocks.Dirt;
import net.minecraftnt.builtin.blocks.Grass;
import net.minecraftnt.builtin.blocks.Stone;
import net.minecraftnt.world.Block;
import net.minecraftnt.world.IBiomeGenerator;
import net.minecraftnt.world.IWorldGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameLoader extends ModLoadingImplementation {
    public static final Logger LOGGER = LogManager.getLogger(GameLoader.class);
    @Override
    public void loadShared() {
        LOGGER.info("Loading blocks...");
        Registries.BLOCKS.register(Block.AIR, new Air());
        Registries.BLOCKS.register(Block.GRASS, new Grass());
        Registries.BLOCKS.register(Block.DIRT, new Dirt());
        Registries.BLOCKS.register(Block.STONE, new Stone());

        LOGGER.info("Loading world generation");
        TerrainGenerator terrainGenerator = new TerrainGenerator();
        Registries.WORLD_GENERATOR.register(IWorldGenerator.OVERWORLD, terrainGenerator);
        Registries.BIOME_GENERATOR.register(IBiomeGenerator.GRASS, terrainGenerator);
    }
}
