package net.minecraftnt.networking;

import java.lang.reflect.InvocationTargetException;

/**
 * A basic class to instantiate packets
 * @param <T> The packet type
 */
public class PacketFactory<T extends Packet> {
    private final Class<? extends Packet> clazz;

    public PacketFactory(T packet) {
        clazz = packet.getClass();
    }

    public Packet instance() {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
