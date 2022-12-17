package net.minecraftnt.server.data.resources;

import net.minecraftnt.server.data.GameData;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FolderResources implements ResourceProvider {
    public boolean fileExists(String name){
        return new File(GameData.getResourceLocation(name)).exists();
    }
    public InputStream loadResourceAsStream(String fileName) {
        if(!fileExists(fileName))
            return null;

        try {
            return new FileInputStream(GameData.getResourceLocation(fileName));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
