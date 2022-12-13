package net.minecraftnt.server.packets;

import net.minecraftnt.InputCommand;
import net.minecraftnt.networking.Packet;
import net.minecraftnt.util.GenericUtil;
import net.minecraftnt.util.Identifier;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class InputPacket extends Packet {
    public static final Identifier IDENTIFIER = new Identifier("minecrafnt", "packets.input");
    public InputCommand command;

    public InputPacket() {
        super(IDENTIFIER);
    }

    public InputPacket(InputCommand command) {
        this();
        this.command = command;
    }

    @Override
    public void load(DataInputStream stream) throws IOException {
        command = new InputCommand();
        command.command = GenericUtil.readString(stream);
        command.value = stream.readFloat();
    }

    @Override
    public void write(DataOutputStream stream) throws IOException {
        stream.writeBytes(command.command);
        stream.writeByte(0);
        stream.writeFloat(command.value);
    }
}
