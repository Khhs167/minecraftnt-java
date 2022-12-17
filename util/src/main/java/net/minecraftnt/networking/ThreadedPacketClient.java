package net.minecraftnt.networking;

import java.io.IOException;

public class ThreadedPacketClient extends Thread {
    private static final PacketClient client = new PacketClient();
    private boolean running = false;
    @Deprecated(forRemoval = false)
    @Override
    public synchronized void start() {
    }

    public synchronized void start(String host, int port) {
        try {
            client.connect(host, port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        super.start();
    }

    @Override
    public void run() {
        running = true;
        while (client.connected() && running) {
            try {
                client.ping();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean connected() {
        return client.connected();
    }

    public void close() {
        client.close();
        running = false;
    }

    public Packet get() {
        return client.get();
    }

    public void send(Packet packet) {
        client.send(packet);
    }
}
