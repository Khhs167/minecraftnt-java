package client.audio;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.*;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import util.resources.Resources;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.stb.STBVorbis.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memSlice;
import static org.lwjgl.system.libc.LibCStdlib.*;

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
        System.out.println(bufferData.length);
        System.out.println("PATH: " + Path.of("").toAbsolutePath() + "/test.ogg");
        ShortBuffer b = stb_vorbis_decode_filename(Path.of("").toAbsolutePath() + "/test.ogg", channels, sampleRate);
        if(b == null){
            System.err.println("Could not load sound!");
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
