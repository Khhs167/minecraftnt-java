package net.minecraftnt.client.audio;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.*;
import net.minecraftnt.util.resources.Resources;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.file.Path;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.stb.STBVorbis.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memSlice;

public class AudioManager {
    private static AudioManager instance;


    public static AudioManager getInstance(){
        if(instance == null){
            instance = new AudioManager();
        }
        return instance;
    }

    public static void unloadInstance(){
        instance.unload();
        instance = null;

    }

    private long device;
    private long context;


    private static final Logger LOGGER = LogManager.getLogger(AudioManager.class);

    private AudioManager()  {
        String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
        this.device = alcOpenDevice(defaultDeviceName);
        if (device == NULL) {
            throw new IllegalStateException("Failed to open the default OpenAL device.");
        }
        int[] attributes = {0};
        this.context = alcCreateContext(device, attributes);
        if (context == NULL) {
            throw new IllegalStateException("Failed to create OpenAL context.");
        }
        alcMakeContextCurrent(context);

        ALCCapabilities alcCapabilities = ALC.createCapabilities(device);
        ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);

        int buffers, source;
        buffers = alGenBuffers();
        source = alGenSources();

        IntBuffer channels = IntBuffer.allocate(1);
        IntBuffer sampleRate = IntBuffer.allocate(1);

        byte[] bufferData = Resources.loadResourceAsBytes("assets/sound/music/minigames/tumble/DoubleTime.ogg");
        ByteBuffer data = ByteBuffer.allocateDirect(bufferData.length);
        data.clear();
        data.put(bufferData);
        data.flip();
        ShortBuffer b = stb_vorbis_decode_filename(Path.of("").toAbsolutePath() + "/test.ogg", channels, sampleRate);
        if(b == null){
            LOGGER.error("Could not load sound!");
            //throw new IllegalStateException("Could not load sound!");
        }

        int sampleFreq = 44100;
        double dt = 2 * Math.PI / sampleFreq;
        double amp = 0.5;

        int freq = 440;
        int dataCount = sampleFreq / freq;

        var sinData = new short[dataCount];
        for (int i = 0; i < sinData.length; i++){
            sinData[i] = (short)(amp * Short.MAX_VALUE * Math.sin(i * dt * freq));
        }

        alBufferData(buffers, AL_FORMAT_MONO16, sinData, sampleFreq);

        alSourcei(source, AL_BUFFER, buffers);
        alSourcei(source, AL_LOOPING, AL_TRUE);
        checkALError();

        alSourcePlay(source);
        checkALError();


    }

    static void checkALError() {
        int err = alGetError();
        if (err != AL_NO_ERROR) {
            throw new RuntimeException(alGetString(err));
        }
    }

    private void unload(){
        alcDestroyContext(context);
        alcCloseDevice(device);
    }
}
