package net.minecraftnt.client;

import net.minecraftnt.ModLoader;
import net.minecraftnt.rendering.RenderAPI;
import net.minecraftnt.rendering.Renderer;
import net.minecraftnt.rendering.Shader;
import net.minecraftnt.util.Identifier;
import net.minecraftnt.util.maths.FastNoiseLite;
import net.minecraftnt.util.maths.Matrix4;
import net.minecraftnt.util.maths.Transformation;
import net.minecraftnt.util.maths.Vector3;
import net.minecraftnt.client.platform.Window;
import net.minecraftnt.world.Block;
import net.minecraftnt.world.Chunk;
import net.minecraftnt.world.IWorldGenerator;
import net.minecraftnt.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.random.RandomGenerator;

public class Client implements Runnable{
    private final Window window;
    private final static Logger LOGGER = LogManager.getLogger("Client");
    public Client() {
        window = new Window(1280, 720, "Minecraftn't");
    }

    @Override
    public void run() {
        ModLoader modLoader = new ModLoader();
        modLoader.loadMods();

        //Renderer.create(RenderAPI.OPENGL);

        World world = new World(IWorldGenerator.OVERWORLD);

        Transformation cameraTransform = new Transformation();

        cameraTransform.setPosition(new Vector3(5,  50, -15));
        //cameraTransform.setRotationX(45);

        while (window.stepFrame()){

            if(window.getInputDevice().isDown(25))
                cameraTransform.move(cameraTransform.forward().multiply(window.getFrameTime() * 10));
            if(window.getInputDevice().isDown(39))
                cameraTransform.move(cameraTransform.forward().negated().multiply(window.getFrameTime() * 10));

            if(window.getInputDevice().isDown(40))
                cameraTransform.move(cameraTransform.right().multiply(window.getFrameTime()));
            if(window.getInputDevice().isDown(38))
                cameraTransform.move(cameraTransform.right().negated().multiply(window.getFrameTime()));

            if(window.getInputDevice().isDown(37))
                cameraTransform.move(cameraTransform.up().negated().multiply(window.getFrameTime()));

            if(window.getInputDevice().isDown(113))
                cameraTransform.rotate(new Vector3(0, -window.getFrameTime() * 50, 0));
            if(window.getInputDevice().isDown(114))
                cameraTransform.rotate(new Vector3(0, window.getFrameTime() * 50, 0));

            if(window.getInputDevice().isDown(116))
                cameraTransform.rotate(new Vector3(window.getFrameTime() * 50, 0, 0));
            if(window.getInputDevice().isDown(111))
                cameraTransform.rotate(new Vector3(-window.getFrameTime() * 50, 0, 0));

            Renderer.shaderProviderC().bind(Shader.DEFAULT);


            Renderer.shaderProviderC().setProjection(Matrix4.perspective(90, window.getAspectRatio(), 0.01f, 1000f));
            Renderer.shaderProviderC().setView(cameraTransform.getMatrix());

            Renderer.shaderProviderC().setFloat("time", window.getTime());

            Renderer.textureProviderC().bind(new Identifier("minecraftnt", "terrain"), 0);

            world.render();
        }


        window.dispose();
        Renderer.shaderProviderC().dispose();
    }

    public static void main(String[] args){
        LOGGER.info("System information:");
        LOGGER.info("JVM: {} jre-{} jvm-{}", System.getProperty("java.runtime.name"), System.getProperty("java.runtime.version"), System.getProperty("java.vm.version"));
        LOGGER.info("OS: {} {} {}", System.getProperty("os.name"), System.getProperty("os.version"), System.getProperty("os.arch"));
        LOGGER.info("Probability of success: {}%", RandomGenerator.getDefault().nextInt(30, 70));

        Client client = new Client();
        client.run();
    }
}
