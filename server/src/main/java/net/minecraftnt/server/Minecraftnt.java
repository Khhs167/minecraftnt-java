package net.minecraftnt.server;

import net.minecraftnt.InputCommand;
import net.minecraftnt.builtin.entities.CameraFlightPawn;
import net.minecraftnt.entities.Pawn;
import net.minecraftnt.networking.NBTPacket;
import net.minecraftnt.networking.Packet;
import net.minecraftnt.networking.PacketListener;
import net.minecraftnt.server.packets.*;
import net.minecraftnt.server.world.Chunk;
import net.minecraftnt.server.world.World;
import net.minecraftnt.server.world.WorldGenerator;
import net.minecraftnt.util.maths.Transformation;
import net.minecraftnt.util.maths.Vector3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Minecraftnt {
    private static final Logger LOGGER = LogManager.getLogger(Minecraftnt.class);
    private static Minecraftnt theMinecraftnt;

    public static Minecraftnt getTheMinecraftnt() {
        return theMinecraftnt;
    }

    private final PacketListener packetListener;
    private World currentWorld;
    private final HashMap<Integer, Transformation> cameras = new HashMap<>();
    private final HashMap<Integer, Pawn> pawns = new HashMap<>();
    private long previousTimeMillis;
    private float deltaTime;
    private float tickTimer;
    private final float maxFramerate = 60;
    private int ticksPerSecond = 20;
    private String tickOnPrintMessage = null;

    private long currentTickFallback;

    public Minecraftnt(PacketListener listener) {
        if(theMinecraftnt != null)
            LOGGER.warn("Minecraftnt instance already exists!");
        theMinecraftnt = this;
        this.packetListener = listener;

    }

    public void load() {
        currentWorld = new World(WorldGenerator.OVERWORLD);
        previousTimeMillis = System.currentTimeMillis();
    }


    public void update() {
        long newFrameTime = System.currentTimeMillis();
        long elapsedTime = newFrameTime - previousTimeMillis;
        previousTimeMillis = newFrameTime;
        deltaTime = elapsedTime / 1000f;

        tickTimer += deltaTime;
        if(tickTimer >= 1f / ticksPerSecond) {
            if(tickOnPrintMessage != null)
                LOGGER.warn(tickOnPrintMessage);
            tickOnPrintMessage = null;

            tick();
            tickTimer = 0;
            currentTickFallback = 0;
        }

        if(elapsedTime < ((1f / maxFramerate) * 1000)) {
            long sleepNeed = (long)((1f / maxFramerate) * 1000) - elapsedTime;
            try {
                Thread.sleep(sleepNeed);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            long timeLag = elapsedTime - (long)((1f / maxFramerate) * 1000);
            currentTickFallback += timeLag;
            // 100ms is enough to call it lag
            if(timeLag > 100)
                tickOnPrintMessage = "Server is overloaded! Tick is running " + timeLag + "ms behind";
        }
    }

    private void tick() {
        //System.out.println("Tick");
        for(int id : cameras.keySet()) {
            Transformation transformation = pawns.get(id).transform(cameras.get(id), deltaTime);
            cameras.put(id, transformation);
            packetListener.send(id, new CameraTransformPacket(transformation));
        }
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
            Chunk chunk = currentWorld.getChunk(0, 0);
            packetListener.send(packet.getSender(), new ChunksPacket(chunk));

            Transformation cameraTransform = new Transformation();
            cameraTransform.setPosition(new Vector3(5,  50, -15));

            cameras.put(packet.getSender(), cameraTransform);
            pawns.put(packet.getSender(), new CameraFlightPawn());
            return;
        }

        if(packet.getTypeIdentifier() == InputPacket.IDENTIFIER) {
            InputCommand inputCommand = ((InputPacket)packet).command;
            pawns.get(packet.getSender()).registerInput(inputCommand);

            return;
        }

        if(packet.getTypeIdentifier() == ChunkRequestPacket.IDENTIFIER) {
            ChunkRequestPacket requestPacket = (ChunkRequestPacket) packet;
            Chunk chunk = currentWorld.getChunk(requestPacket.x, requestPacket.z);

            packetListener.send(packet.getSender(), new ChunksPacket(chunk));
            return;
        }

        LOGGER.error("No packet handler found for packet {}", packet.getTypeIdentifier());
    }
}
