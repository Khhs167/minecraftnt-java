package net.minecraftnt.server.data.resources;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;

public class Resources {
    private static final ResourceProvider[] resourceProviders = {
            new FolderResources(),
            new ClassResources()
    };
    public static byte[] readBytes(String name){
        try(InputStream fileStream = readStream(name)) {
            if(fileStream == null)
                return null;
            return fileStream.readAllBytes();
        } catch (IOException e) {
            return null;
        }
    }

    public static String[] getDirectoryContents(String directory) {
        ArrayList<String> content = new ArrayList<>();

        for(ResourceProvider provider : resourceProviders) {
            String[] providerDirectoryContents = provider.getDirectoryContents(directory);

            for(String c : providerDirectoryContents) {
                if(!content.contains(c))
                    content.add(c);
            }
        }

        return content.toArray(new String[0]);
    }

    public static boolean exists(String name){

        for (ResourceProvider provider : resourceProviders) {
            if(provider.fileExists(name))
                return true;
        }

        return false;
    }

    public static String readString(String name){
        byte[] data = readBytes(name);
        if(data != null)
            return new String(data);
        return null;
    }

    public static InputStream readStream(String name) {
        for (ResourceProvider provider : resourceProviders) {
            InputStream data = provider.loadResourceAsStream(name);
            if(data != null)
                return data;
        }

        return null;
    }
}
