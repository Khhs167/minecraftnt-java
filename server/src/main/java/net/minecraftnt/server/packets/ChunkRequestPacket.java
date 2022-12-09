package net.minecraftnt.server.packets;

import net.minecraftnt.networking.Packet;
import net.minecraftnt.util.Identifier;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ChunkRequestPacket extends Packet {
    public static final Identifier IDENTIFIER = new Identifier("minecraftnt", "packet.chunk_request");
    public int x;
    public int z;
    public ChunkRequestPacket() {
        super(IDENTIFIER);
    }

    public ChunkRequestPacket(int x, int z) {
        super(IDENTIFIER);
        this.x = x;
        this.z = z;
    }

    @Override
    public void load(DataInputStream stream) throws IOException {
        x = stream.readInt();
        z = stream.readInt();
    }

    @Override
    public void write(DataOutputStream stream) throws IOException {
        stream.writeInt(x);
        stream.writeInt(z);
    }
}
