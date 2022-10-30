package net.minecraftnt.client.data.resources;

import net.minecraftnt.client.data.resources.ClassResources;
import net.minecraftnt.client.data.resources.FolderResources;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;

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


    /**
     * Loads a resource file as a byte buffer. Used in the sound system.
     * @param resource The resource to load
     * @return A byte buffer with data. Might be null.
     */
    public static ByteBuffer readByteBuffer(String resource) {
        ByteBuffer buffer;
        byte[] source = readBytes(resource);

        if(source == null)
            return null;

        try (InputStream stream = new ByteArrayInputStream(source); ReadableByteChannel rbc = Channels.newChannel(stream)) {
            buffer = BufferUtils.createByteBuffer(source.length);

            while (true) {
                int bytes = rbc.read(buffer);
                if (bytes == -1) {
                    break;
                }
                if (buffer.remaining() == 0) {
                    buffer = resizeBuffer(buffer, buffer.capacity() * 3 / 2); // 50%
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        buffer.flip();
        return MemoryUtil.memSlice(buffer);
    }

    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }
}
