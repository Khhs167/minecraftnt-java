package net.minecraftnt.client.sound;

import fr.delthas.javamp3.Sound;
import net.minecraftnt.util.maths.Vector3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.*;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;

import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class MCSound {
    private static final Logger LOGGER = LogManager.getLogger(MCSound.class);
    private static MCSound instance;

    public static MCSound getInstance() {
        if (instance == null)
            throw new IllegalStateException("No MCSound instance created!");
        return instance;
    }

    public static MCSound create() {
        return create(true);
    }

    public static MCSound create(boolean global) {
        if (global && instance != null)
            throw new IllegalStateException("Global MCSound instance already created!");

        MCSound mcSound = new MCSound();
        instance = mcSound;
        return mcSound;
    }

    private static short currentIdentifier = 1;

    private final long deviceHandle;
    private final long alcContext;
    private final ALCCapabilities alcCapabilities;
    private final ALCapabilities alCapabilities;
    private final HashMap<Short, Source> sources = new HashMap<>();
    private final HashMap<Short, Clip> clips = new HashMap<>();
    private final HashMap<Short, StreamedClip> streams = new HashMap<>();
    private final HashMap<Short, SoundData> loadedSoundData = new HashMap<>();


    private MCSound() {
        LOGGER.info("Creating MCSound context");
        LOGGER.info("Opening OpenAL device");
        String device_specifier = alcGetString(NULL, ALC_DEVICE_SPECIFIER);
        LOGGER.info("ALC Device: " + device_specifier);

        deviceHandle = alcOpenDevice(device_specifier);
        if (deviceHandle == NULL) {
            throw new IllegalStateException("Could not open device!");
        }

        LOGGER.info("Opening ALC context");
        alcContext = alcCreateContext(deviceHandle, (IntBuffer) null);
        if (alcContext == NULL) {
            throw new IllegalStateException("Could not create AL context!");
        }
        alcMakeContextCurrent(alcContext);
        alcCapabilities = ALC.createCapabilities(deviceHandle);
        ALC.setCapabilities(alcCapabilities);

        LOGGER.info("Opening OpenAL context");
        alCapabilities = AL.createCapabilities(alcCapabilities);
        AL.setCurrentThread(alCapabilities);
        AL.setCurrentProcess(alCapabilities);
    }

    public void close() {
        LOGGER.info("Cleaning up MCSound");
        for (Source source : sources.values()) {
            source.cleanup();
        }

        for (Clip clip : clips.values()) {
            clip.cleanup();
        }

        loadedSoundData.clear();

        alcCloseDevice(deviceHandle);
    }

    public short createSource(boolean loop, boolean relative) {
        Source source = new Source(loop, relative);
        short id = currentIdentifier++;
        sources.put(id, source);
        return id;
    }

    public void freeSource(short id) {
        Source source = sources.get(id);
        if (source != null) {
            source.cleanup();
            sources.remove(id);
        }
    }

    public short readAudio(InputStream stream) {
        try (Sound sound = new Sound(stream)) {

            SoundData soundData = new SoundData();
            soundData.sampleRate = sound.getSamplingFrequency();
            soundData.stereo = sound.isStereo();

            byte[] pcm = sound.readAllBytes();
            soundData.pcm = BufferUtils.createByteBuffer(pcm.length);
            soundData.pcm.put(pcm).flip();

            short id = currentIdentifier++;
            loadedSoundData.put(id, soundData);
            return id;

        } catch (Exception e) {
            LOGGER.throwing(e);
        }
        return 0;
    }

    public short readStreamingClip(InputStream stream) {
        Sound sound = null;
        try {
            sound = new Sound(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        StreamedClip clip = new StreamedClip(sound);

        short id = currentIdentifier++;
        streams.put(id, clip);
        return id;
    }

    public void stream(short source, short streamingClip) {
        StreamedClip clip = streams.get(streamingClip);
        Source source1 = sources.get(source);

        try {
            clip.play(source1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void startStream(short source, short streamingClip) {
        StreamedClip clip = streams.get(streamingClip);
        Source source1 = sources.get(source);

        clip.start(source1);
    }

    public short createClip() {
        Clip clip = new Clip();
        short id = currentIdentifier++;
        clips.put(id, clip);
        return id;
    }

    public short readClip(InputStream stream) {
        short data = readAudio(stream);
        short clip = createClip();
        setClipData(clip, data);
        freeData(data);
        return clip;
    }

    public void freeData(short data) {
        loadedSoundData.remove(data);
    }

    public void setClipData(short clip, short data) {
        Clip clip1 = clips.get(clip);
        SoundData soundData = loadedSoundData.get(data);

        if (clip1 == null || soundData == null)
            return;

        clip1.setData(soundData);
    }

    public void play(short source, short clip) {
        Clip clip1 = clips.get(clip);
        Source source1 = sources.get(source);

        if (clip1 == null || source1 == null)
            return;

        source1.setClip(clip1);
        source1.play();
    }

    public void setVolume(short source, float volume) {
        Source source1 = sources.get(source);
        source1.setGain(volume);
    }

    public boolean playing(short source) {
        Source source1 = sources.get(source);
        return source1.isPlaying();
    }

    public boolean done(short streamedClip) {
        StreamedClip clip = streams.get(streamedClip);
        return clip.done();
    }

    private class Source {
        public final int id;

        public Source(boolean loop, boolean relative) {
            this.id = alGenSources();
            alSourcei(id, AL_LOOPING, loop ? AL_TRUE : AL_FALSE);
            alSourcei(id, AL_SOURCE_RELATIVE, relative ? AL_TRUE : AL_FALSE);
        }


        public void setClip(Clip clip) {
            stop();
            alSourcei(id, AL_BUFFER, clip.id);
        }

        public void setPosition(Vector3 position) {
            alSource3f(id, AL_POSITION, position.getX(), position.getY(), position.getZ());
        }

        public void setSpeed(Vector3 speed) {
            alSource3f(id, AL_VELOCITY, speed.getX(), speed.getY(), speed.getZ());
        }


        public void setGain(float gain) {
            alSourcef(id, AL_GAIN, gain);
        }

        public void setProperty(int param, float value) {
            alSourcef(id, param, value);
        }

        public void play() {
            alSourcePlay(id);
        }

        public boolean isPlaying() {
            return alGetSourcei(id, AL_SOURCE_STATE) == AL_PLAYING;
        }

        public void pause() {
            alSourcePause(id);
        }

        public void setLoop(boolean loop) {
            alSourcei(id, AL_LOOPING, loop ? AL_TRUE : AL_FALSE);
        }

        public void setRelative(boolean relative) {
            alSourcei(id, AL_SOURCE_RELATIVE, relative ? AL_TRUE : AL_FALSE);
        }

        public void stop() {
            alSourceStop(id);
        }

        public void cleanup() {
            stop();
            alDeleteSources(id);
        }
    }

    private class Clip {
        public final int id;

        public Clip() {
            id = alGenBuffers();
        }

        public void cleanup() {
            alDeleteBuffers(id);
        }

        public void setData(SoundData data) {
            alBufferData(id, data.stereo ? AL_FORMAT_STEREO16 : AL_FORMAT_MONO16, data.pcm, data.sampleRate);
        }
    }

    private class StreamedClip {

        public static final int BUFFERS = 2;
        public static final float READ_SECONDS = 1f;
        public final int[] buffers;
        public final Sound sound;
        private final ByteBuffer readBuffer;
        private boolean done;
        private boolean canSample;

        public StreamedClip(Sound sound) {
            buffers = new int[BUFFERS];
            for (int i = 0; i < buffers.length; i++) {
                buffers[i] = alGenBuffers();
            }
            this.sound = sound;
            readBuffer = MemoryUtil.memAlloc((int) (sound.getSamplingFrequency() * READ_SECONDS) * 2);
            readBuffer.order(ByteOrder.nativeOrder());
            done = false;
            canSample = true;
        }

        public boolean done() {
            return done;
        }

        public void streamBuffer(ByteBuffer buffer) {
            try {
                int size = buffer.capacity();
                byte[] data = new byte[size];
                int readAmt = sound.read(data);

                if (readAmt <= 0)
                    canSample = false;

                if (readAmt < 0) {
                    return;
                }

                buffer.rewind();
                buffer.put(data, 0, readAmt);
                buffer.rewind();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public boolean streamBuffer(int buffer, Source source) {
            streamBuffer(readBuffer);
            if (!canSample)
                return false;
            if (done)
                return false;
            alBufferData(buffer, sound.isStereo() ? AL_FORMAT_STEREO16 : AL_FORMAT_MONO16, readBuffer.slice(), sound.getSamplingFrequency());
            alSourceQueueBuffers(source.id, buffer);
            return true;
        }

        public void start(Source source) {
            for (int buffer : buffers) {
                if (!streamBuffer(buffer, source))
                    break;
            }

            source.play();
        }

        public void stop(Source source) {
            int queued = alGetSourcei(source.id, AL_BUFFERS_QUEUED);
            for (int i = 0; i < queued; i++)
                alSourceUnqueueBuffers(source.id);
        }

        public void play(Source source) throws IOException {

            int p = alGetSourcei(source.id, AL_BUFFERS_PROCESSED);

            while (p > 0) {
                int buffer = alSourceUnqueueBuffers(source.id);
                streamBuffer(buffer, source);
                p = alGetSourcei(source.id, AL_BUFFERS_PROCESSED);
            }

            if (alGetSourcei(source.id, AL_BUFFERS_QUEUED) == 0 && !canSample) {
                done = true;
                stop(source);
            }
        }
    }

    private class SoundData {
        public boolean stereo;
        public ByteBuffer pcm;
        public int sampleRate;
    }

}