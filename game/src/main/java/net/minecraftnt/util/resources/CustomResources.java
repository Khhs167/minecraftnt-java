package net.minecraftnt.util.resources;

import net.minecraftnt.util.GameInfo;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class CustomResources {
    private static String resourcePack = "dev";
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

    public static void setResourcePack(String resourcePack) {
        CustomResources.resourcePack = resourcePack;
    }

    public static String getResourcePack() {
        return resourcePack;
    }

    public static boolean fileExists(String name){
        return new File(GameInfo.getResourceLocationForPack(name, resourcePack)).exists();
    }

    public static String loadResourceAsString(String name){

        byte[] bytes = loadResourceAsBytes(name);

        if(bytes == null)
            return null;

        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static InputStream loadResourceAsStream(String fileName) {

        if(resourcePack == null)
            return null;

        if(!fileExists(fileName))
            return null;

        try {
            return new FileInputStream(GameInfo.getResourceLocationForPack(fileName, resourcePack));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
