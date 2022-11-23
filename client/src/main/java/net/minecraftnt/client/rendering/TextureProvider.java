package net.minecraftnt.client.rendering;

import de.matthiasmann.twl.utils.PNGDecoder;
import net.minecraftnt.client.rendering.gl.GLTextureProvider;
import net.minecraftnt.rendering.Texture;
import net.minecraftnt.server.data.resources.Resources;
import net.minecraftnt.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.MemoryUtil;

import java.io.InputStream;
import java.nio.ByteBuffer;

public abstract class TextureProvider {

    private static final Logger LOGGER = LogManager.getLogger(GLTextureProvider.class);

    public abstract Texture load(Identifier identifier);
    public abstract boolean bind(Identifier identifier, int id);
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

    protected record TextureData(ByteBuffer dataBuffer, int width, int height) {
    }
}
