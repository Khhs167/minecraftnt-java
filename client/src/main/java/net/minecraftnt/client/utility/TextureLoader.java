package net.minecraftnt.client.utility;

import de.matthiasmann.twl.utils.PNGDecoder;
import net.minecraftnt.MinecraftntData;
import net.minecraftnt.Registries;
import net.minecraftnt.client.rendering.gl.GLTextureProvider;
import net.minecraftnt.rendering.Renderer;
import net.minecraftnt.rendering.TextureAtlasLocation;
import net.minecraftnt.rendering.TextureData;
import net.minecraftnt.server.data.resources.Resources;
import net.minecraftnt.util.Identifier;
import net.minecraftnt.util.maths.Vector2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.MemoryUtil;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class TextureLoader {
    public static final Logger LOGGER = LogManager.getLogger(GLTextureProvider.class);
    private static final boolean SAVE_ATLASES = true;

    public static TextureData readData(Identifier identifier) {
        return readData("assets/" + identifier.getNamespace() + "/textures/" + identifier.getName() + ".png");
    }

    public static TextureData readData(String file) {
        try {

            InputStream stream = Resources.readStream(file);
            PNGDecoder decoder = new PNGDecoder(stream);
            ByteBuffer buffer = MemoryUtil.memAlloc(4 * decoder.getHeight() * decoder.getWidth());

            decoder.decode(buffer, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);

            return new TextureData(buffer, decoder.getWidth(), decoder.getHeight());

        } catch (Throwable e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public static TextureData packAtlas(Identifier... identifiers) {
        TextureData[] data = new TextureData[identifiers.length];
        for (int i = 0; i < data.length; i++) {
            data[i] = readData(identifiers[i]);
        }

        return packAtlas(identifiers, data);
    }

    private static String getNameWithoutExtension(String file) {
        int dotIndex = file.lastIndexOf('.');
        return (dotIndex == -1) ? file : file.substring(0, dotIndex);
    }

    public static void loadAtlas(Identifier directory) {
        String[] files = Resources.getDirectoryContents("assets/" + directory.getNamespace() + "/textures/" + directory.getName());
        Identifier[] identifiers = new Identifier[files.length];
        for(int i = 0; i < files.length; i++) {
            identifiers[i] = new Identifier(directory.getNamespace(), directory.getName() + "/" + getNameWithoutExtension(files[i]));
        }

        loadPack(directory, identifiers);
    }

    public static void loadPack(Identifier texture, Identifier... identifiers) {
        TextureData data = packAtlas(identifiers);
        Renderer.textureProviderC().loadData(data, texture);
    }

    public static void writeFile(TextureData textureData, String path) {
        LOGGER.info("Saving texture");
        StringBuilder builder = new StringBuilder();
        builder.append("P3 # PPM format - autogen\n");
        builder.append(textureData.width()).append(" ").append(textureData.height()).append(" # Resolution\n");
        builder.append("255 # 255 bits colour depth\n# Pixel data start\n");

        ByteBuffer data = textureData.dataBuffer();
        for (int y = 0; y < textureData.height(); y++) {
            for (int x = 0; x < textureData.width(); x++) {

                int i = (y * textureData.width() + x) * 4;

                for (int j = 0; j < 3; j++) {
                    int val = (int) data.get(i + j) & 0xff;

                    String writeVal = "" + val;
                    builder.append(writeVal);
                    builder.append(" ".repeat(Math.max(0, 3 - writeVal.length() + 1)));
                }
            }
            builder.append("\n");
        }

        LOGGER.info("Writing data");
        try(FileOutputStream outputStream = new FileOutputStream(path)) {
            outputStream.write(builder.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static TextureData verticalFlip(TextureData textureData) {
        ByteBuffer newBuffer = MemoryUtil.memAlloc(textureData.width() * textureData.height() * 4);

        for (int x = 0; x < textureData.width(); x++) {
            for (int y = 0; y < textureData.height(); y++) {
                int sourceY = y;
                int sourceIndex = (sourceY * textureData.width() + x) * 4;

                int pixel = y * textureData.width() + x;
                int index = pixel * 4;
                for(int i = 0; i < 4; i++) {
                    newBuffer.put(index + i, textureData.dataBuffer().get(sourceIndex + i));
                }
            }
        }

        return new TextureData(newBuffer, textureData.width(), textureData.height());
    }


    public static TextureData packAtlas(Identifier[] identifiers, TextureData[] textures) {
        int width = 0;
        int height = 0;

        for(TextureData data : textures) {
            width += data.width();
            if(data.height() > height)
                height = data.height();
        }

        LOGGER.info("Packing texture atlas at {}x{}, {} textures", width, height, identifiers.length);

        int currentX = 0;
        ByteBuffer textureBuffer = MemoryUtil.memAlloc(width * height * 4);

        for(int i = 0; i < textures.length; i++) {

            TextureData data = textures[i];

            ByteBuffer textureData = data.dataBuffer();
            for(int x = 0; x < data.width(); x++) {
                for(int y = 0; y < data.height(); y++) {
                    int xx = currentX + x;

                    int globalPixel = width * y + xx;
                    int localPixel = data.width() * y + x;

                    int globalPixelTransformed = globalPixel * 4;
                    int localPixelTransformed = localPixel * 4;
                    for(int p = 0; p < 4; p++) {
                        textureBuffer.put(globalPixelTransformed + p, textureData.get(localPixelTransformed + p));
                    }


                }
            }

            Vector2 topLeft = new Vector2(currentX / (float)width, 1 - data.height() / (float)height);
            Vector2 topRight = new Vector2(topLeft.getX() + data.width() / (float)width, 1);

            Registries.TEXTURE_ATLAS_LOC.register(identifiers[i], new TextureAtlasLocation(topLeft, topRight));

            currentX += data.width();
        }

        TextureData out = new TextureData(textureBuffer, width, height);

        if(SAVE_ATLASES)
            writeFile(out, "pack-" + MinecraftntData.RANDOM.nextInt(0, Integer.MAX_VALUE) + ".ppm");

        return out;
    }
}
