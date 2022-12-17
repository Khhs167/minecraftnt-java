package net.minecraftnt.networking;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * A client for connecting to a PacketListener
 */
public class PacketClient {
    private SocketChannel channel;
    private PacketSocket packetSocket;
    /**
     * Create a client
     */
    public PacketClient() {
    }

    /**
     * Connect the client to a server
     * @param host The host to connect to
     * @param port The port to connect to
     * @throws IOException Sometimes connecting fails
     */
    public void connect(String host, int port) throws IOException {

        Selector selector = Selector.open();
        channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        channel.connect(new InetSocketAddress(host, port));
        while(!channel.finishConnect()) Thread.onSpinWait();
        packetSocket = new PacketSocket(channel);
    }

    /**
     * Send a packet to the connected-to server
     * @param packet The packet to send
     * @throws IOException Sometimes connecting errors happen
     */
    public void send(Packet packet) {
        packetSocket.send(packet);
    }

    /**
     * Close the connection to a server
     */
    public void close() {
        try {
            channel.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the latest packet, or null if none are left
     * @return The latest packet
     */
    public Packet get() {
        return packetSocket.get();
    }


    /**
     * Query for packets in connections and write packets
     * @throws IOException Sometimes something goes wrong with reading
     */
    public void ping() throws IOException {
        packetSocket.ping();
    }

    public boolean connected() {
        return packetSocket.connected();
    }
}
