package net.minecraftnt.rendering;

import net.minecraftnt.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.ByteBuffer;

public abstract class TextureProvider {

    private static final Logger LOGGER = LogManager.getLogger(TextureProvider.class);

    public abstract Texture load(Identifier identifier);
    public abstract Texture loadData(TextureData data, Identifier identifier);
    public abstract boolean bind(Identifier identifier, int id);

}
