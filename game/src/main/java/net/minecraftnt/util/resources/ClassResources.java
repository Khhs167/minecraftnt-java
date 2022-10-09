package net.minecraftnt.util.resources;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ClassResources {

    public static InputStream loadResourceAsStream(String fileName) {

        // The class loader that loaded the class
        ClassLoader classLoader = ClassResources.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);

        // the stream holding the file content
        if (inputStream == null) {
            return null;
        } else {
            return inputStream;
        }

    }

    public static boolean fileExists(String fileName) {

        // The class loader that loaded the class
        ClassLoader classLoader = ClassResources.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);

        // the stream holding the file content
        if (inputStream == null)
            return false;

        return true;

    }

    public static byte[] loadResourceAsBytes(String path){
        InputStream stream = loadResourceAsStream(path);

        if(stream == null)
            return null;

        byte[] bytes = new byte[0];

        try{
            bytes = stream.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes;
    }

    public static String loadResourceAsString(String path){

        byte[] bytes = loadResourceAsBytes(path);

        if(bytes == null)
            return null;

        return new String(bytes, StandardCharsets.UTF_8);
    }
}
