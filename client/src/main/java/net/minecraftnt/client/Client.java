package net.minecraftnt.client;

import net.minecraftnt.client.platform.Window;
import net.minecraftnt.client.rendering.*;
import net.minecraftnt.util.FaceFlags;
import net.minecraftnt.util.Identifier;
import net.minecraftnt.util.maths.*;

public class Client implements Runnable{
    private final Window window;

    public Client() {
        window = new Window(800, 600, "Minecraftn't");
    }




    @Override
    public void run() {

        Shader.load(new Identifier("minecraftnt", "default"));
        Texture.load(new Identifier("minecraftnt", "vroom"));

        RectangleMesh mesh = VoxelGenerator.generateVoxelMesh(FaceFlags.ALL_FACES);


        mesh.updateMesh();


        Transformation meshTransform = new Transformation();
        Transformation cameraTransform = new Transformation();

        cameraTransform.setPosition(new Vector3(0, 3, -3));
        cameraTransform.setRotationX(45);

        float targetRot = 90;

        while (window.stepFrame()){
            meshTransform.rotate(new Vector3(window.getFrameTime() * 90));

            Shader.bind(new Identifier("minecraftnt", "default"));

            Shader.setProjection(Matrix4.perspective(90, window.getAspectRatio(), 0.01f, 100f));
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
