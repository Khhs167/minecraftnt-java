package net.minecraftnt.client.sound;

import net.minecraftnt.util.Identifier;
import net.minecraftnt.util.registries.Registry;
import net.minecraftnt.util.resources.Resources;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL11.*;
import static org.lwjgl.stb.STBVorbis.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_close;
import static org.lwjgl.system.MemoryUtil.NULL;

public class SoundClip {

    private final int id;

    /**
     * Load a new sound clip
     * @param identifier The identifier for the clip
     * @param file The file to load
     */
    public static void loadSoundClip(Identifier identifier, String file){
        new SoundClip(identifier, file);
    }

    private SoundClip(Identifier identifier, String file){
        id = alGenBuffers();

        SoundManager.getInstance().checkALErrors();

        try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
            ShortBuffer pcm = readVorbis(file, info);

            //copy to buffer
            alBufferData(id, info.channels() == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16, pcm, info.sample_rate());
            SoundManager.getInstance().checkALErrors();
        }

        Registry.SOUNDS.add(identifier, this);

    }

    public int getId() {
        return id;
    }

    public void cleanup() {
        alDeleteBuffers(id);
        SoundManager.getInstance().checkALErrors();
    }

    private ShortBuffer readVorbis(String resource, STBVorbisInfo info) {
        ByteBuffer vorbis;
        try {
            vorbis = Resources.loadResourceAsByteBuffer(resource);
        } catch (Exception  e) {
            throw new RuntimeException(e);
        }

        IntBuffer error   = BufferUtils.createIntBuffer(1);
        long      decoder = stb_vorbis_open_memory(vorbis, error, null);
        if (decoder == NULL) {
            throw new RuntimeException("Failed to open Ogg Vorbis file. Error: " + error.get(0));
        }

        STBVorbis.stb_vorbis_get_info(decoder, info);

        int channels = info.channels();

        ShortBuffer pcm = BufferUtils.createShortBuffer(stb_vorbis_stream_length_in_samples(decoder) * channels);

        stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm);
        stb_vorbis_close(decoder);

        return pcm;
    }
}
