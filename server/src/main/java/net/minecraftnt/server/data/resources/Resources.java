package net.minecraftnt.server.data.resources;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class Resources {
    public static byte[] readBytes(String name){

        byte[] folderBytes = FolderResources.loadResourceAsBytes(name);
        if(folderBytes != null)
            return folderBytes;

        byte[] classBytes = ClassResources.loadResourceAsBytes(name);
        if(classBytes != null)
            return classBytes;

        return null;
    }

    public static boolean exists(String name){

        if(FolderResources.fileExists(name))
            return true;

        if(ClassResources.fileExists(name))
            return true;

        return false;
    }

    public static String readString(String name){
        String folderString = FolderResources.loadResourceAsString(name);
        if(folderString != null)
            return folderString;

        String classString = ClassResources.loadResourceAsString(name);
        if(classString != null)
            return classString;

        return null;
    }

    public static InputStream readStream(String fileName) {
        InputStream folderStream = FolderResources.loadResourceAsStream(fileName);
        if(folderStream != null)
            return folderStream;

        InputStream classStream = ClassResources.loadResourceAsStream(fileName);
        if(classStream != null)
            return classStream;

        return null;
    }
}
