package net.minecraftnt.networking;

import java.io.*;
import java.nio.channels.SocketChannel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class PacketConnection {
    public static final Logger LOGGER = LogManager.getLogger(PacketConnection.class);
    private final SocketChannel socket;
    private final PacketSocket packetSocket;
    public PacketConnection(SocketChannel accept) {
        this.socket = accept;
        try {
            this.socket.configureBlocking(false);
            this.socket.finishConnect();
            this.packetSocket = new PacketSocket(accept);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void send(Packet packet) throws IOException {
        packetSocket.send(packet);
    }

    public Packet get() {
        return packetSocket.get();
    }

    public void ping() throws IOException {
        packetSocket.ping();
    }

    public void disconnect() throws IOException {
        packetSocket.socket().close();
    }
}
