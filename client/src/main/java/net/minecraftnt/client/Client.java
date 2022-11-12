package net.minecraftnt.client;

import net.minecraftnt.nbt.NBTReader;
import net.minecraftnt.nbt.NBTWriter;
import net.minecraftnt.nbt.exceptions.UnexpectedNBTNodeException;
import net.minecraftnt.nbt.nodes.NBTNode;
import net.minecraftnt.util.FaceFlags;
import net.minecraftnt.util.Identifier;
import net.minecraftnt.util.maths.Matrix4;
import net.minecraftnt.util.maths.Transformation;
import net.minecraftnt.util.maths.Vector3;
import net.minecraftnt.client.data.resources.Resources;
import net.minecraftnt.client.platform.Window;
import net.minecraftnt.client.rendering.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Client implements Runnable{
    private final Window window;

    public Client() {
        window = new Window(800, 600, "Minecraftn't");
    }




    @Override
    public void run() {

        Shader.load(new Identifier("net/minecraftnt", "default"));
        Texture.load(new Identifier("net/minecraftnt", "vroom"));

        RectangleMesh mesh = VoxelGenerator.generateVoxelMesh(FaceFlags.ALL_FACES);

        System.out.println("Updating mesh");
        mesh.updateMesh();
        System.out.println("Updated mesh");


        Transformation meshTransform = new Transformation();
        Transformation cameraTransform = new Transformation();

        cameraTransform.setPosition(new Vector3(0, 3, -3));
        cameraTransform.setRotationX(45);

        float targetRot = 90;

        while (window.stepFrame()){
            //System.out.println(1f / window.getFrameTime());
            meshTransform.rotate(new Vector3(window.getFrameTime() * 90));

            Shader.bind(new Identifier("net/minecraftnt", "default"));

            Shader.setProjection(Matrix4.perspective(90, window.getAspectRatio(), 0.01f, 100f));
            Shader.setModel(meshTransform.getMatrix());
            Shader.setView(cameraTransform.getMatrix());
            //Shader.setView(Matrix4.lookAt(new Vector3(0, 0, -2), new Vector3(0), new Vector3(0, 1, 0)));

            Texture.bind(new Identifier("net/minecraftnt", "vroom"), 0);
            mesh.render();
        }

        window.dispose();
        Shader.REGISTRY.execute("dispose", Shader.class);
    }

    public static void main(String[] args){

        // Time to test our NBT parser

        try {
            NBTReader reader = new NBTReader(Resources.readStream("bigtest.nbt"));
            reader.parse();

            NBTNode root = reader.getRoot();

            System.out.println(root.toString());

            FileOutputStream testfile = new FileOutputStream("test.nbt");
            NBTWriter writer = new NBTWriter(testfile);
            writer.beginCompound("TestShit");
            writer.writeString("Name", "Amogus");
            writer.beginList("Gigachads", "string");
            writer.writeString("", "RedCube");
            writer.endList();
            writer.endCompound();

            writer.flush();

            NBTReader testReader = new NBTReader(new FileInputStream("test.nbt"));
            testReader.parse();

            root = testReader.getRoot();

            System.out.println(root.toString());



        } catch (IOException | UnexpectedNBTNodeException e) {
            throw new RuntimeException(e);
        }

        /*Client client = new Client();
        client.run();*/
    }
}
