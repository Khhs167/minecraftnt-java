package net.minecraftnt.client.rendering.gl;

import net.minecraftnt.Registries;
import net.minecraftnt.client.rendering.TextureProvider;
import net.minecraftnt.rendering.Texture;
import net.minecraftnt.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.lwjgl.opengl.GL33C.*;

public final class GLTextureProvider extends TextureProvider {


    public static final Logger LOGGER = LogManager.getLogger(GLTextureProvider.class);

    public Texture load(Identifier identifier){

        LOGGER.info("Creating texture {}", identifier);

        TextureData data = readData(identifier);

        int handle = glGenTextures();
        assert data != null;
        LOGGER.debug("Building texture {}, ({}x{})", handle, data.width(), data.height());

        glBindTexture(GL_TEXTURE_2D, handle);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        try {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, data.width(), data.height(), 0, GL_RGBA, GL_UNSIGNED_BYTE, data.dataBuffer());
        } catch(Exception e){
            e.printStackTrace();
        }

        Texture texture = new GLTexture(handle);

        Registries.TEXTURE.register(identifier, texture);

        return texture;

    }

    public boolean bind(Identifier identifier, int id) {

        GLTexture texture = (GLTexture)Registries.TEXTURE.get(identifier);

        if(texture == null) {
            LOGGER.warn("Fetching texture {} returned null!", identifier.toString());
            return false;
        }

        bind(texture.getId(), id);
        return true;
    }

    private static void bind(int handle, int id){

        if(id > 32) {
            LOGGER.error("Cannot bind textures to id's higher than 32!");
        }

        glBindTexture(GL_TEXTURE_2D, handle);

    }




}
