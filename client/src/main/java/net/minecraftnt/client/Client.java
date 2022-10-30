package net.minecraftnt.client;

import net.minecraftnt.client.platform.Window;
import net.minecraftnt.client.rendering.RectangleMesh;
import net.minecraftnt.client.rendering.Shader;
import net.minecraftnt.server.Server;

public class Client implements Runnable{
    private final Window window;

    public Client() {
        window = new Window(800, 600, "Minecraftn't");
    }

    @Override
    public void run() {

        Shader testShader = Shader.loadFromName("default");
        RectangleMesh mesh = new RectangleMesh();

        while (window.stepFrame()){
            testShader.bind();
            mesh.render();
        }

        testShader.dispose();
        window.dispose();
    }

    public static void main(String[] args){
        Client client = new Client();
        client.run();
    }
}
