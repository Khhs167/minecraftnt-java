package util.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Resources {

    public static InputStream loadResourceAsStream(String fileName) {

        // The class loader that loaded the class
        ClassLoader classLoader = Resources.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);

        // the stream holding the file content
        if (inputStream == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return inputStream;
        }

    }

    public static byte[] loadResourceAsBytes(String path){
        InputStream stream = loadResourceAsStream(path);
        byte[] bytes = new byte[0];

        try{
            bytes = stream.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes;
    }

    public static String loadResourceAsString(String path){

        return new String(loadResourceAsBytes(path), StandardCharsets.UTF_8);
    }
}
