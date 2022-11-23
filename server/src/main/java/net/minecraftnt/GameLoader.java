package net.minecraftnt;

import net.minecraftnt.api.ModLoadingImplementation;
import net.minecraftnt.builtin.Air;
import net.minecraftnt.builtin.Dirt;
import net.minecraftnt.builtin.Grass;
import net.minecraftnt.builtin.Stone;
import net.minecraftnt.util.Identifier;
import net.minecraftnt.world.Block;

public class GameLoader extends ModLoadingImplementation {
    @Override
    public void loadShared() {
        Registries.BLOCKS.register(Block.AIR, new Air());
        Registries.BLOCKS.register(Block.GRASS, new Grass());
        Registries.BLOCKS.register(Block.DIRT, new Dirt());
        Registries.BLOCKS.register(Block.STONE, new Stone());
    }
}
