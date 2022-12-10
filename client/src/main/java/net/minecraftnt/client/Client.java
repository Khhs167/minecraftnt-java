package net.minecraftnt.client;

import net.minecraftnt.ModLoader;
import net.minecraftnt.nbt.NBTReader;
import net.minecraftnt.nbt.NBTWriter;
import net.minecraftnt.nbt.nodes.NBTCompoundNode;
import net.minecraftnt.networking.*;
import net.minecraftnt.rendering.Renderer;
import net.minecraftnt.rendering.Shader;
import net.minecraftnt.server.Server;
import net.minecraftnt.server.data.resources.Resources;
import net.minecraftnt.server.packets.ChunkRequestPacket;
import net.minecraftnt.server.packets.ChunksPacket;
import net.minecraftnt.server.packets.ConnectPacket;
import net.minecraftnt.server.world.Chunk;
import net.minecraftnt.threading.BalancedThreadPool;
import net.minecraftnt.threading.WeightedRunnable;
import net.minecraftnt.util.Identifier;
import net.minecraftnt.util.maths.Matrix4;
import net.minecraftnt.util.maths.Transformation;
import net.minecraftnt.util.maths.Vector3;
import net.minecraftnt.client.platform.Window;
import net.minecraftnt.server.world.WorldGenerator;
import net.minecraftnt.server.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.random.RandomGenerator;

public class Client implements Runnable{
    private final Window window;
    private final static Logger LOGGER = LogManager.getLogger("Client");
    public Client() {
        window = new Window(1280, 720, "Minecraftn't");
    }

    private World world;
    private int render_distance = 2;

    public void handlePacket(Packet packet) {
        if(packet.getTypeIdentifier() == ChunksPacket.IDENTIFIER) {
            ChunksPacket chunksPacket = (ChunksPacket)packet;
            Chunk chunk = chunksPacket.getChunk();
            LOGGER.info("Received chunk at " + chunk.getPosition());
            world.setChunk(chunk);
            return;
        }
        LOGGER.error("Unhandled packet " + packet.getTypeIdentifier());
    }

    @Override
    public void run() {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);


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

        //Renderer.create(RenderAPI.OPENGL);

        world = new World();

        Transformation cameraTransform = new Transformation();

        cameraTransform.setPosition(new Vector3(5,  50, -15));
        //cameraTransform.setRotationX(45);

        for(int x = -render_distance; x <= render_distance; x++) {
            for(int z = -render_distance; z <= render_distance; z++) {
                try {
                    LOGGER.info("Requesting chunk {}, {}", x, z);
                    client.send(new ChunkRequestPacket(x, z));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        while (window.stepFrame()) {

            if (window.getInputDevice().isDown(25))
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
                cameraTransform.rotate(new Vector3(-window.getFrameTime() * 50, 0, 0));

            Renderer.shaderProviderC().bind(Shader.DEFAULT);


            Renderer.shaderProviderC().setProjection(Matrix4.perspective(90, window.getAspectRatio(), 0.01f, 1000f));
            Renderer.shaderProviderC().setView(cameraTransform.getMatrix());

            Renderer.shaderProviderC().setFloat("time", window.getTime());

            Renderer.textureProviderC().bind(new Identifier("minecraftnt", "terrain"), 0);

            world.render();

            BalancedThreadPool.getGlobalInstance().ping();

            try {
                client.ping();
                Packet c;
                while ((c = client.get()) != null)
                    handlePacket(c);
            } catch (Exception e) {
                LOGGER.error("Packet Error: " + e);
            }
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
