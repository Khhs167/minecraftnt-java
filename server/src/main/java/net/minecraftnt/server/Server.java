package net.minecraftnt.server;

import net.minecraftnt.MinecraftntData;
import net.minecraftnt.ModLoader;
import net.minecraftnt.networking.Packet;
import net.minecraftnt.networking.PacketListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Server implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger(Server.class);
    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }

    private volatile boolean alive = false;

    @Override
    public void run() {
        try {
            runServer();
            LOGGER.info("Server closed gracefully");
        } catch (Exception e) {
            LOGGER.error("Server closed with error: ", e);
        }
    }

    private void runServer() {
        LOGGER.info("Starting server");
        PacketListener listener = new PacketListener(25569);

        LOGGER.info("Loading mods");
        ModLoader modLoader = ModLoader.getLatest();
        if(modLoader == null)
            modLoader = new ModLoader();

        modLoader.loadMods(false);

        LOGGER.info("Creating Minecraftnt instance");
        Minecraftnt minecraftnt = new Minecraftnt(listener);
        minecraftnt.load();

        listener.open();
        LOGGER.info("Server listening on *:25569");

        alive = true;
        LOGGER.info("Entering main loop");

        while (alive) {
            listener.ping();

            Packet c;
            while((c = listener.get()) != null)
                minecraftnt.handlePacket(c);

            minecraftnt.update();
            Thread.onSpinWait();
        }

        LOGGER.info("Closing server");
        listener.close();
    }

    public void kill() {
        alive = false;
    }
}
