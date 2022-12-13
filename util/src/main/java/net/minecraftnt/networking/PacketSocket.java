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
    private final Lock readLock = new ReentrantLock();
    private final long PACKET_HEADER = 0xDEADBEEFL;
    public PacketSocket(SocketChannel channel) {
        this.channel = channel;
    }

    private void readPacket() throws IOException {
        if(currentReadBuffer.isEmpty()) return;

        byte[] data = new byte[currentReadBuffer.size()];
        for(int i = 0; i < data.length; i++) data[i] = currentReadBuffer.get(i);
        DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(data));

        String namespace = GenericUtil.readString(dataStream);
        String name = GenericUtil.readString(dataStream);

        Identifier identifier = new Identifier(namespace, name);

        PacketFactory<?> packetFactory = Packet.FACTORY_REGISTRY.get(identifier);

        if (packetFactory == null) {
            LOGGER.warn("Could not read package from id '{}'", identifier);
            currentReadLength = -1;
            currentReadBuffer.clear();
            return;
        }

        try {
            Packet packet = packetFactory.instance();
            packet.load(dataStream);
            readLock.lock();
            packets.add(packet);
            readLock.unlock();
        } catch (IOException e) {
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
        if(!channel.isConnected() || !channel.isOpen())
            return;

        writeLock.lock();
        while (!writeQueue.isEmpty())
            write(writeQueue.poll());
        writeLock.unlock();

        ByteBuffer readData = ByteBuffer.allocate(channel.socket().getInputStream().available());
        channel.read(readData);
        DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(readData.array()));

        while(true) {
            if(currentReadLength == -1) {
                if (dataInputStream.available() < Long.BYTES) return;
                while(dataInputStream.available() >= Long.BYTES) {
                    if(dataInputStream.readLong() == PACKET_HEADER) {
                        currentReadLength = -2;
                        break;
                    }
                    currentReadLength = -1;
                    Thread.onSpinWait();
                }
            }

            if(currentReadLength == -2) {
                if(dataInputStream.available() >= Integer.BYTES)
                    currentReadLength = dataInputStream.readInt();
            }

            while (currentReadLength > 0 && dataInputStream.available() > 0) {
                currentReadBuffer.add(dataInputStream.readByte());
                currentReadLength--;
            }

            if(currentReadLength == 0) {
                try {
                    readPacket();
                } catch(Exception e) {
                    LOGGER.throwing(e);
                }
            }

            if(currentReadLength != -1)
                return;
        }
    }

    /**
     * Get the latest packet, or null if none are left
     * @return The latest packet
     */
    public Packet get() {
        readLock.lock();
        Packet packet = null;
        if(!packets.isEmpty())
            packet = packets.poll();
        readLock.unlock();
        return packet;
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
        if(!channel.isConnected() || !channel.isOpen())
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
        dataOutputStream.writeLong(PACKET_HEADER);
        dataOutputStream.writeInt(outputStream.size());
        dataOutputStream.write(outputStream.toByteArray());

        channel.write(ByteBuffer.wrap(actualOutput.toByteArray()));
        out.flush();
    }

    public SocketChannel socket() {
        return channel;
    }

}
