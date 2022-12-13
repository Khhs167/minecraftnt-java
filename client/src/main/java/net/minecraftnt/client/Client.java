package net.minecraftnt.client;

import net.minecraftnt.InputCommand;
import net.minecraftnt.ModLoader;
import net.minecraftnt.builtin.entities.CameraFlightPawn;
import net.minecraftnt.entities.Pawn;
import net.minecraftnt.nbt.NBTReader;
import net.minecraftnt.nbt.NBTWriter;
import net.minecraftnt.nbt.nodes.NBTCompoundNode;
import net.minecraftnt.networking.*;
import net.minecraftnt.rendering.Mesh;
import net.minecraftnt.rendering.Renderer;
import net.minecraftnt.rendering.Shader;
import net.minecraftnt.rendering.Vertex;
import net.minecraftnt.server.Server;
import net.minecraftnt.server.data.resources.Resources;
import net.minecraftnt.server.packets.*;
import net.minecraftnt.server.world.Chunk;
import net.minecraftnt.server.world.ChunkPosition;
import net.minecraftnt.threading.BalancedThreadPool;
import net.minecraftnt.threading.WeightedRunnable;
import net.minecraftnt.util.Identifier;
import net.minecraftnt.util.maths.Matrix4;
import net.minecraftnt.util.maths.Transformation;
import net.minecraftnt.util.maths.Vector2;
import net.minecraftnt.util.maths.Vector3;
import net.minecraftnt.client.platform.Window;
import net.minecraftnt.server.world.WorldGenerator;
import net.minecraftnt.server.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.random.RandomGenerator;

public class Client implements Runnable{
    private final Window window;
    private final static Logger LOGGER = LogManager.getLogger("Client");
    public Client() {
        window = new Window(1280, 720, "Minecraftn't");
    }

    private World world;
    private int render_distance = 1;
    private Transformation cameraTransform = new Transformation();
    private ChunkPosition previousCameraChunk = new ChunkPosition(0, 0);
    private Pawn currentPawn = new CameraFlightPawn();
    private Queue<InputCommand> inputCommands = new LinkedList<>();
    private float positionLagTimer = 0;

    public void handlePacket(Packet packet) {
        //LOGGER.info(packet.getTypeIdentifier());
        if(packet.getTypeIdentifier() == ChunksPacket.IDENTIFIER) {
            ChunksPacket chunksPacket = (ChunksPacket)packet;
            Chunk chunk = chunksPacket.getChunk();
            world.setChunk(chunk);
            return;
        }

        if(packet.getTypeIdentifier() == CameraTransformPacket.IDENTIFIER) {
            Transformation newTransform = ((CameraTransformPacket)packet).transformation;
            if(newTransform.getPosition().subtract(cameraTransform.getPosition()).lengthSquared() > 100f && positionLagTimer > 10f) {
                //cameraTransform.setPosition(newTransform.getPosition());
                positionLagTimer = 0f;
            }

            return;
        }

        LOGGER.error("Unhandled packet " + packet.getTypeIdentifier());
    }

    @Override
    public void run() {
        BalancedThreadPool.getGlobalInstance().start();

        ThreadDownloadResources threadDownloadResources = new ThreadDownloadResources();
        threadDownloadResources.start();

        ModLoader modLoader = new ModLoader();
        modLoader.loadMods(true);

        LOGGER.info("Connecting to server");
        PacketClient client = new PacketClient();

        try {
            client.connect("localhost", 25569);
            LOGGER.info("Established connection");
            LOGGER.info("Sending request");
            client.send(new ConnectPacket());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Transformation transformation = new Transformation();

        //Renderer.create(RenderAPI.OPENGL);

        world = new World();

        cameraTransform.setPosition(new Vector3(5,  50, -15));
        //cameraTransform.setRotationX(45);

        for(int x = -render_distance; x <= render_distance; x++) {
            for(int z = -render_distance; z <= render_distance; z++) {
                client.send(new ChunkRequestPacket(x, z));
            }
        }

        while (window.stepFrame()) {

            if (window.getInputDevice().isDown(25))
                inputCommands.add(new InputCommand("forward"));
            if (window.getInputDevice().isDown(39))
                inputCommands.add(new InputCommand("backward"));

            if (window.getInputDevice().isDown(40))
                inputCommands.add(new InputCommand("right"));
            if (window.getInputDevice().isDown(38))
                inputCommands.add(new InputCommand("left"));

            /*if (window.getInputDevice().isDown(25))
                cameraTransform.move(cameraTransform.forward().multiply(window.getFrameTime() * 10));
            if (window.getInputDevice().isDown(39))
                cameraTransform.move(cameraTransform.forward().negated().multiply(window.getFrameTime() * 10));

            if (window.getInputDevice().isDown(40))
                cameraTransform.move(cameraTransform.right().multiply(window.getFrameTime()));
            if (window.getInputDevice().isDown(38))
                cameraTransform.move(cameraTransform.right().negated().multiply(window.getFrameTime()));

            if (window.getInputDevice().isDown(37))
                cameraTransform.move(cameraTransform.up().negated().multiply(window.getFrameTime()));

            if (window.getInputDevice().isDown(113))
                cameraTransform.rotate(new Vector3(0, -window.getFrameTime() * 50, 0));
            if (window.getInputDevice().isDown(114))
                cameraTransform.rotate(new Vector3(0, window.getFrameTime() * 50, 0));

            if (window.getInputDevice().isDown(116))
                cameraTransform.rotate(new Vector3(window.getFrameTime() * 50, 0, 0));
            if (window.getInputDevice().isDown(111))
                cameraTransform.rotate(new Vector3(-window.getFrameTime() * 50, 0, 0));*/


            while (!inputCommands.isEmpty()) {
                InputCommand c = inputCommands.poll();
                currentPawn.registerInput(c);
                client.send(new InputPacket(c));
            }

            cameraTransform = currentPawn.transform(cameraTransform, window.getFrameTime());

            ChunkPosition cameraChunkPos = new ChunkPosition((int) Math.floor(cameraTransform.getPosition().getX() / Chunk.CHUNK_WIDTH), (int) Math.floor(cameraTransform.getPosition().getZ() / Chunk.CHUNK_WIDTH));

            if (!cameraChunkPos.equals(previousCameraChunk)) {
                previousCameraChunk = cameraChunkPos;

                if (!world.hasChunk(cameraChunkPos)) {
                    client.send(new ChunkRequestPacket(cameraChunkPos.getX(), cameraChunkPos.getY()));
                }
            }

            Renderer.shaderProviderC().bind(Shader.DEFAULT);


            Renderer.shaderProviderC().setProjection(Matrix4.perspective(90, window.getAspectRatio(), 0.01f, 1000f));
            Renderer.shaderProviderC().setView(cameraTransform.getMatrix());

            Renderer.shaderProviderC().setFloat("time", window.getTime());

            Renderer.textureProviderC().bind(new Identifier("minecraftnt", "terrain"), 0);

            world.render();

            BalancedThreadPool.getGlobalInstance().ping();

            try {
                client.ping();
            } catch (IOException e) {
                LOGGER.error("Packet Error: " + e);
            }

            Packet c;
            while ((c = client.get()) != null)
                handlePacket(c);

            positionLagTimer += window.getFrameTime();
        }

        BalancedThreadPool.getGlobalInstance().kill();
        //world.save();
        window.dispose();
        client.close();
        Renderer.shaderProviderC().dispose();
    }

    public static void main(String[] args) {
        LOGGER.info("System information:");
        LOGGER.info("JVM: {} jre-{} jvm-{}", System.getProperty("java.runtime.name"), System.getProperty("java.runtime.version"), System.getProperty("java.vm.version"));
        LOGGER.info("OS: {} {} {}", System.getProperty("os.name"), System.getProperty("os.version"), System.getProperty("os.arch"));
        LOGGER.info("Probability of success: {}%", RandomGenerator.getDefault().nextInt(50, 100));
        LOGGER.info("Available cores: {}, using {} for threading", Runtime.getRuntime().availableProcessors(), BalancedThreadPool.GLOBAL_SIZE);

        Server server = new Server();
        Thread serverThread = new Thread(server);
        serverThread.setName("Server");
        serverThread.start();

        Client client = new Client();
        client.run();

        server.kill();
    }
}
