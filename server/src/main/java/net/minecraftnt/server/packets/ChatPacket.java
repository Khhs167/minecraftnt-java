package net.minecraftnt.server.packets;

import net.minecraftnt.networking.Packet;
import net.minecraftnt.util.GenericUtil;
import net.minecraftnt.util.Identifier;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ChatPacket extends Packet {
    public static final Identifier IDENTIFIER = new Identifier("minecrafnt", "packets.chat");
    public String command;

    public ChatPacket() {
        super(IDENTIFIER);
    }

    public ChatPacket(String command) {
        this();
        this.command = command;
    }

    @Override
    public void load(DataInputStream stream) throws IOException {
        command = GenericUtil.readString(stream);
    }

    @Override
    public void write(DataOutputStream stream) throws IOException {
        stream.writeBytes(command);
        stream.writeByte(0);
    }
}
