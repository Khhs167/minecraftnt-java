package net.minecraftnt.networking;

import net.minecraftnt.util.Registry;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ProtocolFamily;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

/**
 * A packet server for listening and communicating with multiple clients
 */
public class PacketListener {

    private final ServerSocketChannel serverSocket;
    private final Selector selector;
    private final ArrayList<PacketConnection> connections = new ArrayList<>();
    private final Queue<Packet> packets = new LinkedList<>();
    private boolean isAlive = true;

    /**
     * Create a listener to a port
     * @param port The port to listen at
     */
    public PacketListener(int port) {
        try {
            serverSocket = ServerSocketChannel.open();
            serverSocket.configureBlocking(false);
            serverSocket.bind(new InetSocketAddress("0.0.0.0", port));

            selector = Selector.open();

            serverSocket.register(selector, SelectionKey.OP_ACCEPT);



        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Enable listening
     */
    public void open() {
        isAlive = true;
    }

    /**
     * Send a packet to an individual connection
     * @param connection The connection ID to send to. To get the ID of a packets' sender, do Packet.getSender()
     * @param packet The packet to send
     */
    public void send(int connection, Packet packet) {
        try {
            connections.get(connection).send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Broadcast a packet to all connected clients
     * @param packet The packet to broadcast
     */
    public void broadcast(Packet packet) {
        for(PacketConnection connection : connections) {
            try {
                connection.send(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Get the latest packet, or null if none is available
     * @return The latest packet
     */
    public Packet get() {
        return packets.poll();
    }

    /**
     * Close the connection
     */
    public void close() {
        isAlive = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Accept incoming connections and read available packets
     */
    public void ping() {
        try {
            if (isAlive) {
                SocketChannel acceptedChannel;
                while ((acceptedChannel  = serverSocket.accept()) != null)
                    connections.add(new PacketConnection(acceptedChannel));

                for (int i = 0; i < connections.size(); i++) {
                    PacketConnection connection = connections.get(i);
                    connection.read();
                    Packet p;
                    while((p = connection.get()) != null) {
                        p.setSender(i);
                        packets.add(p);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
