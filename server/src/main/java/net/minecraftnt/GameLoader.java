package net.minecraftnt;

import net.minecraftnt.api.ModLoadingImplementation;
import net.minecraftnt.builtin.TerrainGenerator;
import net.minecraftnt.builtin.blocks.Air;
import net.minecraftnt.builtin.blocks.Dirt;
import net.minecraftnt.builtin.blocks.Grass;
import net.minecraftnt.builtin.blocks.Stone;
import net.minecraftnt.networking.NBTPacket;
import net.minecraftnt.networking.Packet;
import net.minecraftnt.networking.PacketFactory;
import net.minecraftnt.networking.PacketListener;
import net.minecraftnt.server.packets.ChunkRequestPacket;
import net.minecraftnt.server.packets.ChunksPacket;
import net.minecraftnt.server.packets.ConnectPacket;
import net.minecraftnt.server.world.Block;
import net.minecraftnt.server.world.BiomeGenerator;
import net.minecraftnt.server.world.WorldGenerator;
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
        Registries.WORLD_GENERATOR.register(WorldGenerator.OVERWORLD, new TerrainGenerator());
        Registries.BIOME_GENERATOR.register(BiomeGenerator.GRASS, new TerrainGenerator.BiomeGen());

        LOGGER.info("Registering packet types");
        Packet.FACTORY_REGISTRY.register(NBTPacket.IDENTIFIER, new PacketFactory<>(new NBTPacket()));
        Packet.FACTORY_REGISTRY.register(ConnectPacket.IDENTIFIER, new PacketFactory<>(new ConnectPacket()));
        Packet.FACTORY_REGISTRY.register(ChunksPacket.IDENTIFIER, new PacketFactory<>(new ChunksPacket()));
        Packet.FACTORY_REGISTRY.register(ChunkRequestPacket.IDENTIFIER, new PacketFactory<>(new ChunkRequestPacket()));
    }
}
