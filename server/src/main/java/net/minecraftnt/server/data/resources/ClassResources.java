package net.minecraftnt.server.data.resources;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ClassResources implements ResourceProvider {
    private static final Logger LOGGER = LogManager.getLogger(ClassResources.class);
    public InputStream loadResourceAsStream(String fileName) {

        // The class loader that loaded the class
        ClassLoader classLoader = ClassResources.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);

        // the stream holding the file content
        if (inputStream == null) {
            LOGGER.error("Attempting to read invalid class resource {}! This means that something is horribly wrong!", fileName);
            return null;
        } else {
            return inputStream;
        }

    }
    public boolean fileExists(String fileName) {

        // The class loader that loaded the class
        ClassLoader classLoader = ClassResources.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);

        // the stream holding the file content
        if (inputStream == null)
            return false;

        return true;
    }
}
