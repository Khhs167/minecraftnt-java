package net.minecraftnt.server.packets;

import net.minecraftnt.networking.Packet;
import net.minecraftnt.util.Identifier;
import net.minecraftnt.util.maths.Transformation;
import net.minecraftnt.util.maths.Vector3;

import java.io.*;

public class CameraTransformPacket extends Packet {
    public static final Identifier IDENTIFIER = new Identifier("minecraftnt", "packets.camera");
    public Transformation transformation;
    public CameraTransformPacket() {
        super(IDENTIFIER);
        transformation = new Transformation();
    }

    public CameraTransformPacket(Transformation transformation) {
        this();
        this.transformation = transformation;
    }

    @Override
    public void load(DataInputStream stream) throws IOException {
        transformation = new Transformation();
        transformation.setPosition(readV3(stream));
        transformation.setRotation(readV3(stream));
    }

    @Override
    public void write(DataOutputStream stream) throws IOException {
        writeV3(transformation.getPosition(), stream);
        writeV3(transformation.getRotation(), stream);
    }

    private void writeV3(Vector3 vector3, DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeFloat(vector3.getX());
        dataOutputStream.writeFloat(vector3.getY());
        dataOutputStream.writeFloat(vector3.getZ());
    }

    private Vector3 readV3(DataInputStream dataInputStream) throws IOException {
        float x = dataInputStream.readFloat();
        float y = dataInputStream.readFloat();
        float z = dataInputStream.readFloat();
        return new Vector3(x, y, z);
    }
}
