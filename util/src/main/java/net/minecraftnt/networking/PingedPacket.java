package net.minecraftnt.networking;

import net.minecraftnt.util.Identifier;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class PingedPacket extends Packet {

    public static final Identifier IDENTIFIER = new Identifier("minecraftnt", "packet.ping");

    public PingedPacket() {
        super(IDENTIFIER);
    }

    @Override
    public void load(DataInputStream stream) {

    }

    @Override
    public void write(DataOutputStream stream) {

    }
}
