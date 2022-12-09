package net.minecraftnt.server.packets;

import net.minecraftnt.networking.Packet;
import net.minecraftnt.util.Identifier;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ConnectPacket extends Packet {
    public static final Identifier IDENTIFIER = new Identifier("minecraftnt", "packet.connect");
    public ConnectPacket() {
        super(IDENTIFIER);
    }

    @Override
    public void load(DataInputStream stream) throws IOException {

    }

    @Override
    public void write(DataOutputStream stream) throws IOException {

    }
}
