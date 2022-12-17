package net.minecraftnt.server.data.resources;

import java.io.InputStream;

public interface ResourceProvider {
    boolean fileExists(String name);
    InputStream loadResourceAsStream(String fileName);
}
