package net.minecraftnt.server.data.resources;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Collections;

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

    @Override
    public String[] getDirectoryContents(String path) {
        /*
        ClassLoader classLoader = ClassResources.class.getClassLoader();
        URL directoryURL = classLoader.getResource(path);
        assert directoryURL != null;
        String jarPath = directoryURL.getPath();
        File[] files = new File(jarPath).listFiles();

        ArrayList<String> content = new ArrayList<>();

        assert files != null;
        for(File file : files) {
            if(file.isFile() && !file.getName().startsWith(".")) {
                content.add(file.getName());
            }
        }

        return content.toArray(new String[0]);
        */
        return new String[0];
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
