package net.minecraftnt.server.data.resources;

import net.minecraftnt.server.data.GameData;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FolderResources {
    public static byte[] loadResourceAsBytes(String name){
        try {
            InputStream fileStream = loadResourceAsStream(name);

            if(fileStream == null)
                return null;

            return fileStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean fileExists(String name){
        return new File(GameData.getResourceLocation(name)).exists();
    }

    public static String loadResourceAsString(String name){

        byte[] bytes = loadResourceAsBytes(name);

        if(bytes == null)
            return null;

        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static InputStream loadResourceAsStream(String fileName) {
        if(!fileExists(fileName))
            return null;

        try {
            return new FileInputStream(GameData.getResourceLocation(fileName));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
