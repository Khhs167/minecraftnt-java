package net.minecraftnt.util.resources;

import net.minecraftnt.util.GameInfo;

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
        return new File(GameInfo.getResourceLocation(name)).exists();
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
            return new FileInputStream(GameInfo.getResourceLocation(fileName));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
