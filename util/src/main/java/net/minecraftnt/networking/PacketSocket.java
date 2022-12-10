package net.minecraftnt.networking;

import net.minecraftnt.util.GenericUtil;
import net.minecraftnt.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A socket interface for packets, is in use for PacketClient and PacketConnection
 */
public class PacketSocket {
    private final static Logger LOGGER = LogManager.getLogger(PacketSocket.class);
    private final SocketChannel channel;
    private final ArrayList<Byte> currentReadBuffer = new ArrayList<>();
    private int currentReadLength = -1;
    private final Queue<Packet> packets = new LinkedList<>();
    private final Queue<Packet> writeQueue = new LinkedList<>();
    private final Lock writeLock = new ReentrantLock();
    public PacketSocket(SocketChannel channel) {
        this.channel = channel;
    }

    private void readPacket() throws IOException {
        byte[] data = new byte[currentReadBuffer.size()];
        for(int i = 0; i < data.length; i++) data[i] = currentReadBuffer.get(i);
        DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(data));

        String namespace = GenericUtil.readString(dataStream);
        String name = GenericUtil.readString(dataStream);

        Identifier identifier = new Identifier(namespace, name);
        PacketFactory<?> packetFactory = Packet.FACTORY_REGISTRY.get(identifier);

        if (packetFactory == null) {
            //LOGGER.warn("Could not read package from id '{}'", identifier);
            return;
        }

        try {
            Packet packet = packetFactory.instance();
            packet.load(dataStream);
            packets.add(packet);
        } catch (Exception e) {
            LOGGER.throwing(e);
        }

        currentReadLength = -1;
        currentReadBuffer.clear();


    }

    /**
     * Query for packets in socket, and write queued packets
     * @throws IOException Sometimes something goes wrong with reading
     */
    public void ping() throws IOException {
        if(!channel.isConnected())
            return;

        writeLock.lock();
        Packet p;
        while ((p = writeQueue.poll()) != null)
            write(p);
        writeLock.unlock();

        ByteBuffer readData = ByteBuffer.allocate(channel.socket().getInputStream().available());
        channel.read(readData);
        DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(readData.array()));

        while(true) {
            if(currentReadLength == -1) {
                if (dataInputStream.available() < Long.SIZE + Integer.SIZE) return;
                currentReadLength = dataInputStream.readInt();
            }
            while (currentReadLength > 0 && dataInputStream.available() > 0) {
                currentReadBuffer.add(dataInputStream.readByte());
                currentReadLength--;
            }

            if(currentReadLength == 0)
                readPacket();

            if(currentReadLength != -1)
                return;
        }
    }

    /**
     * Get the latest packet, or null if none are left
     * @return The latest packet
     */
    public Packet get() {
        return packets.poll();
    }

    /**
     * Enqueue a packet for sending
     * @param packet The packet to send
     */
    public void send(Packet packet) {
        writeLock.lock();
        writeQueue.add(packet);
        writeLock.unlock();
    }


    private void write(Packet packet) throws IOException {
        if(!channel.isConnected())
            return;

        Identifier identifier = packet.getTypeIdentifier();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(outputStream);
        out.writeBytes(identifier.getNamespace());
        out.write(0);
        out.writeBytes(identifier.getName());
        out.write(0);
        packet.write(out);

        ByteArrayOutputStream actualOutput = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(actualOutput);
        dataOutputStream.writeInt(outputStream.size());
        dataOutputStream.write(outputStream.toByteArray());

        channel.write(ByteBuffer.wrap(actualOutput.toByteArray()));
        out.flush();
    }

}
