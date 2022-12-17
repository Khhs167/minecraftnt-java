package net.minecraftnt.networking;

import net.minecraftnt.util.Identifier;
import net.minecraftnt.util.Registry;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * The base packet, for sending and receiving.
 */
public abstract class Packet {
    /**
     * This should be overridden with an empty constructor setting a constant identifier, or at least preferably
     * @param identifier The packet type identifier
     */
    public Packet(Identifier identifier) {
        this.typeIdentifier = identifier;
    }

    /**
     * A registry of all PackageFactories to be used, this will be queried using the identifier specified to a packet
     */
    public static final Registry<PacketFactory<?>> FACTORY_REGISTRY = new Registry<>();
    private final Identifier typeIdentifier;
    private int sender;

    /**
     * Get the packet type identifier
     * @return The identifier
     */
    public Identifier getTypeIdentifier() {
        return typeIdentifier;
    }

    /**
     * Get an ID of the sender of a packet, for responding
     * @return The ID
     */
    public int getSender() {
        return sender;
    }

    /**
     * Function used internally to update the sender of a packet
     * @param sender The sender ID that this packet was sent by
     */
    public void setSender(int sender) {
        this.sender = sender;
    }

    /**
     * Load in packet data
     * @param stream A stream to read data from
     * @throws IOException In case something fails with the stream
     */
    public abstract void load(DataInputStream stream) throws IOException;

    /**
     * Serialize packet data
     * @param stream The stream to serialize to
     * @throws IOException In case something fails with the stream
     */
    public abstract void write(DataOutputStream stream) throws IOException;

    /**
     * Get the smallest size the packet may have
     * @return The size to preallocate
     */
    public int minimalSize() {
        return 0;
    }
}
