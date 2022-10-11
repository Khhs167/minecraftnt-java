package net.minecraftnt.client.rendering;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import de.matthiasmann.twl.utils.PNGDecoder;
import net.minecraftnt.util.Identifier;
import net.minecraftnt.util.registries.Registry;
import org.lwjgl.system.MemoryUtil;
import net.minecraftnt.util.resources.Resources;

import static org.lwjgl.opengl.GL11.*;

public class Texture implements AutoCloseable {

    public static final Identifier TEXTURE_NULL = new Identifier("minecraft", "null");

    private int id;

    public int getId() {
        return id;
    }

    public static Texture loadTexture(String path){

        if(!Resources.fileExists(path))
            return null;

        return new Texture(path);
    }

    private Texture(String path)  {

        id = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, id);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        InputStream stream = Resources.loadResourceAsStream(path);

        try {
            PNGDecoder decoder = new PNGDecoder(stream);
            ByteBuffer buffer = MemoryUtil.memAlloc(4 * decoder.getHeight() * decoder.getWidth());
            decoder.decode(buffer, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
            buffer.flip();
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        } catch(Exception e){
            e.printStackTrace();
        }


    }

    public static RawTexture loadData(String resource){
        try {
            InputStream stream = Resources.loadResourceAsStream(resource);
            PNGDecoder decoder = new PNGDecoder(stream);
            ByteBuffer buffer = MemoryUtil.memAlloc(4 * decoder.getHeight() * decoder.getWidth());
            decoder.decode(buffer, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
            buffer.flip();
            return new RawTexture(buffer, decoder.getWidth(), decoder.getHeight());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void use(Identifier identifier) {

        Texture texture = Registry.TEXTURES.get(identifier);

        if(texture == null) {
            texture = Registry.TEXTURES.get(TEXTURE_NULL);
        }

        texture.use();


    }

    public void use(){
        glBindTexture(GL_TEXTURE_2D, id);
    }

    @Override
    public void close()  {
        glDeleteTextures(id);
    }

    public static class RawTexture {
        public final ByteBuffer data;
        public final int width;
        public final int height;

        public RawTexture(ByteBuffer data, int width, int height) {
            this.data = data;
            this.width = width;
            this.height = height;
        }
    }
}
