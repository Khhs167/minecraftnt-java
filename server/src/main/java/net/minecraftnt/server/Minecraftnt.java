package net.minecraftnt.server;

import net.minecraftnt.networking.NBTPacket;
import net.minecraftnt.networking.Packet;
import net.minecraftnt.networking.PacketListener;
import net.minecraftnt.server.packets.ChunkRequestPacket;
import net.minecraftnt.server.packets.ChunksPacket;
import net.minecraftnt.server.packets.ConnectPacket;
import net.minecraftnt.server.world.Chunk;
import net.minecraftnt.server.world.World;
import net.minecraftnt.server.world.WorldGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Minecraftnt {
    private static final Logger LOGGER = LogManager.getLogger(Minecraftnt.class);
    private static Minecraftnt theMinecraftnt;

    public static Minecraftnt getTheMinecraftnt() {
        return theMinecraftnt;
    }

    private final PacketListener packetListener;
    private World currentWorld;

    public Minecraftnt(PacketListener listener) {
        if(theMinecraftnt != null)
            LOGGER.warn("Minecraftnt instance already exists!");
        theMinecraftnt = this;
        this.packetListener = listener;
    }

    public void load() {
        currentWorld = new World(WorldGenerator.OVERWORLD);
    }

    public void update() {

    }

    private void handleNBTPacket(NBTPacket packet) {

    }

    public void handlePacket(Packet packet) {

        if(packet.getTypeIdentifier() == NBTPacket.IDENTIFIER) {
            handleNBTPacket((NBTPacket)packet);
            return;
        }

        if(packet.getTypeIdentifier() == ConnectPacket.IDENTIFIER) {
            LOGGER.info("New client connected!");
            LOGGER.info("Sending chunk at 0,0");
            Chunk chunk = currentWorld.getChunk(0, 0);
            packetListener.send(packet.getSender(), new ChunksPacket(chunk));
            return;
        }

        if(packet.getTypeIdentifier() == ChunkRequestPacket.IDENTIFIER) {
            ChunkRequestPacket requestPacket = (ChunkRequestPacket) packet;
            Chunk chunk = currentWorld.getChunk(requestPacket.x, requestPacket.z);

            LOGGER.info("Sending chunk at {}, {}", requestPacket.x, requestPacket.z);
            packetListener.send(packet.getSender(), new ChunksPacket(chunk));
            return;
        }

        LOGGER.error("No packet handler found, sending back");
        packetListener.send(packet.getSender(), packet);
    }
}
