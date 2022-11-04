package net.minecraftnt.client.rendering;

import de.matthiasmann.twl.utils.PNGDecoder;
import net.minecraftnt.client.data.resources.Resources;
import net.minecraftnt.util.Identifier;
import net.minecraftnt.util.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.opengl.GL33C.*;

import java.io.InputStream;
import java.nio.ByteBuffer;

public class Texture {

    public static final Registry<Texture> REGISTRY = new Registry<>();
    public static final Logger LOGGER = LogManager.getLogger(Texture.class);

    private final int handle;

    public static TextureData readData(Identifier identifier){
        try {

            InputStream stream = Resources.readStream("assets/" + identifier.getNamespace() + "/textures/" + identifier.getName() + ".png");
            PNGDecoder decoder = new PNGDecoder(stream);
            ByteBuffer buffer = MemoryUtil.memAlloc(4 * decoder.getHeight() * decoder.getWidth());
            decoder.decodeFlipped(buffer, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
            buffer.flip();
            return new TextureData(buffer, decoder.getWidth(), decoder.getHeight());

        } catch (Throwable e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public static Texture load(Identifier identifier){

        Texture texture = new Texture(readData(identifier));

        REGISTRY.register(identifier, texture);

        return texture;

    }

    public static boolean bind(Identifier identifier, int id) {

        Texture texture = REGISTRY.get(identifier);

        if(texture == null) {
            LOGGER.warn("Fetching texture {} returned null!", identifier.toString());
            return false;
        }

        texture.bind(id);
        return true;
    }



    public Texture(TextureData data) {

        handle = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, handle);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        try {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, data.width, data.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data.dataBuffer);
        } catch(Exception e){
            e.printStackTrace();
        }

    }

    public void bind(int id){

        if(id > 32) {
            LOGGER.error("Cannot bind textures to id's higher than 32!");
        }

        glBindTexture(GL_TEXTURE0 + id, handle);

    }


    record TextureData(ByteBuffer dataBuffer, int width, int height) {
    }
}
