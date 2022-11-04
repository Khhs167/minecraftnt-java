package net.minecraftnt.client;

import net.minecraftnt.client.platform.Window;
import net.minecraftnt.client.rendering.Quad;
import net.minecraftnt.client.rendering.RectangleMesh;
import net.minecraftnt.client.rendering.Shader;
import net.minecraftnt.client.rendering.Texture;
import net.minecraftnt.utility.Identifier;
import net.minecraftnt.utility.maths.Matrix4;
import net.minecraftnt.utility.maths.Transformation;
import net.minecraftnt.utility.maths.Vector2;
import net.minecraftnt.utility.maths.Vector3;

public class Client implements Runnable{
    private final Window window;

    public Client() {
        window = new Window(800, 600, "Minecraftn't");
    }

    @Override
    public void run() {

        Shader.load(new Identifier("minecraftnt", "default"));
        Texture.load(new Identifier("minecraftnt", "vroom"));
        RectangleMesh mesh = new RectangleMesh();

        mesh.quads.add(new Quad(new Vector3(), new Vector2(1), 0));


        Transformation meshTransform = new Transformation();
        Transformation cameraTransform = new Transformation();

        cameraTransform.setRotation(new Vector3(45, 0, 0));
        cameraTransform.setPosition(new Vector3(0, 2, -2));

        mesh.updateMesh();

        while (window.stepFrame()){

            meshTransform.rotate(new Vector3(0, 2f, 0));

            Shader.bind(new Identifier("minecraftnt", "default"));

            Shader.setProjection(Matrix4.perspective(60, window.getAspectRatio(), 0.01f, 100f));
            Shader.setModel(meshTransform.getMatrix());
            Shader.setView(cameraTransform.getMatrix());
            //Shader.setView(Matrix4.lookAt(new Vector3(0, 0, -2), new Vector3(0), new Vector3(0, 1, 0)));

            Texture.bind(new Identifier("minecraftnt", "vroom"), 0);
            mesh.render();
        }

        window.dispose();
        Shader.REGISTRY.execute("dispose", Shader.class);
    }

    public static void main(String[] args){
        Client client = new Client();
        client.run();
    }
}
