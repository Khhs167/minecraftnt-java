package net.minecraftnt.client.sound;

import net.minecraftnt.server.Minecraft;
import net.minecraftnt.util.Identifier;
import net.minecraftnt.util.Vector3;
import net.minecraftnt.util.registries.Registry;
import net.minecraftnt.util.resources.Resources;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.*;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;

import javax.sound.sampled.AudioSystem;
import java.io.IOException;
import java.nio.*;
import java.util.Random;

import static org.lwjgl.openal.ALC11.*;
import static org.lwjgl.openal.AL11.*;
import static org.lwjgl.openal.EXTThreadLocalContext.alcSetThreadContext;
import static org.lwjgl.openal.SOFTDirectChannels.AL_DIRECT_CHANNELS_SOFT;
import static org.lwjgl.stb.STBVorbis.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class SoundManager {
    private static final Logger LOGGER = LogManager.getLogger(SoundManager.class);

    private static SoundManager instance;

    public static SoundManager getInstance() {
        return instance;
    }



    public static void initialize() {
        instance = new SoundManager();
    }

    private final long device;
    private final long context;
    private final SoundSource[] sources;

    private SoundManager() {
        LOGGER.info("Creating sound context");

        device = alcOpenDevice((ByteBuffer) null);
        if(device == NULL){
            LOGGER.fatal("Could not open OpenAL device");
        }

        context = alcCreateContext(device, (IntBuffer) null);
        if(context == NULL){
            LOGGER.fatal("Could not create OpenAL context");
        }

        LOGGER.info("Setting sound context");
        alcSetThreadContext(context);
        ALCCapabilities deviceCaps = ALC.createCapabilities(device);

        AL.createCapabilities(deviceCaps);

        LOGGER.info("Creating sound source buffer");
        final int sourceCount = 128; // 128 sounds is a LOT

        sources = new SoundSource[sourceCount];

        for (int i = 0; i < sourceCount; i++){
            sources[i] = new SoundSource(false, false);
        }

        checkALErrors();

        alDistanceModel(AL_LINEAR_DISTANCE_CLAMPED);

    }

    public SoundSource getFreeSource(){

        for(SoundSource source : sources){
            if(!source.isPlaying())
                return source;
        }

        int sourceToTake = Minecraft.RANDOM.nextInt(0, sources.length);

        sources[sourceToTake].stop();
        return sources[sourceToTake];

    }

    /**
     * Play a sound once, globally
     * @param identifier The sound to play
     * @return The sound source. NOTE: This might be reused as soon as the sound stops playing
     */
    public SoundSource play(Identifier identifier) {
        return play(identifier, false, false, Vector3.zero(), Vector3.zero(), 1, 1);
    }

    /**
     * Play a sound, globally
     * @param identifier The sound to play
     * @param loop Should the sound loop?
     * @return The sound source. NOTE: This might be reused as soon as the sound stops playing
     */
    public SoundSource play(Identifier identifier, boolean loop) {
        return play(identifier, loop, false, Vector3.zero(), Vector3.zero(), 1, 1);
    }

    /**
     * Play a sound once, at a position
     * @param identifier The sound to play
     * @param position Where is the sound located(relative to the listener)
     * @return The sound source. NOTE: This might be reused as soon as the sound stops playing
     */
    public SoundSource play(Identifier identifier, Vector3 position) {
        return play(identifier, false, true, position, Vector3.zero(), 1, 1);
    }

    /**
     * Play a sound, at a position
     * @param identifier The sound to play
     * @param loop Should it loop?
     * @param position Where is the sound located(relative to the listener)
     * @return The sound source. NOTE: This might be reused as soon as the sound stops playing
     */
    public SoundSource play(Identifier identifier, boolean loop, Vector3 position) {
        return play(identifier, loop, true, position, Vector3.zero(), 1, 1);
    }

    /**
     * Play a sound once in world
     * @param identifier The sound to play
     * @param position Where is the sound located (relative to the listener)
     * @param velocity What velocity does the source have
     * @return The sound source. NOTE: This might be reused as soon as the sound stops playing
     */
    public SoundSource play(Identifier identifier, Vector3 position, Vector3 velocity) {
        return play(identifier, false, true, position, velocity, 1, 1);
    }

    /**
     * Play a sound in world.
     * @param identifier The sound to play
     * @param loop Should it loop?
     * @param position Where is the sound located (relative to the listener)
     * @param velocity What velocity does the source have
     * @return The sound source. NOTE: This might be reused as soon as the sound stops playing
     */
    public SoundSource play(Identifier identifier, boolean loop, Vector3 position, Vector3 velocity) {
        return play(identifier, loop, true, position, velocity, 1, 1);
    }

    /**
     * Play a sound in world with full control at start.
     * @param identifier The sound to play
     * @param loop Should it loop?
     * @param position Where is the sound located (relative to the listener)
     * @param velocity What velocity does the source have
     * @param gain The gain of the sound
     * @param pitch The pitch of the sound
     * @return The sound source. NOTE: This might be reused as soon as the sound stops playing
     */
    public SoundSource play(Identifier identifier, boolean loop, boolean relativistic, Vector3 position, Vector3 velocity, float gain, float pitch){

        SoundClip clip = Registry.SOUNDS.get(identifier);

        if(clip == null)
            return null;

        var source = getFreeSource();
        source.setBuffer(clip.getId());
        source.setLoop(loop);
        source.setGain(gain);
        source.setProperty(AL_PITCH, pitch);
        source.setRelative(relativistic);
        source.setPosition(position);
        source.setSpeed(velocity);

        source.play();


        checkALErrors();

        return source;
    }

    public void setListenerPosition(Vector3 position){
        alListener3f(AL_POSITION, position.getX(), position.getY(), position.getZ());
    }

    public void setListenerVelocity(Vector3 velocity){
        alListener3f(AL_VELOCITY, velocity.getX(), velocity.getY(), velocity.getZ());
    }

    public void setListenerOrientation(Vector3 forward, Vector3 up){
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(6 * 4);
        byteBuffer.order( ByteOrder.nativeOrder() );
        FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();

        floatBuffer.put(0, forward.getX());
        floatBuffer.put(1, forward.getY());
        floatBuffer.put(2, forward.getZ());

        floatBuffer.put(3, up.getX());
        floatBuffer.put(4, up.getY());
        floatBuffer.put(5, up.getZ());

        alListenerfv(AL_ORIENTATION, floatBuffer);
    }

    public void close(){
        for (SoundSource source : sources) {
            source.stop();
            source.cleanup();
        }

        for(SoundClip clip : Registry.SOUNDS.getValues()){
            clip.cleanup();
        }

        alcSetThreadContext(NULL);
        alcDestroyContext(context);
        alcCloseDevice(device);

        checkALErrors();
    }

    public void checkALErrors() {
        int error;
        if ((error = alGetError()) != AL_NO_ERROR){
            LOGGER.error("AL error: {}/{}", error, getALErrorString(error));
        }

        if ((error = alcGetError(device)) != AL_NO_ERROR){
            LOGGER.error("ALC error: {}", error);
        }
    }

    private String getALErrorString(int err)
    {
        switch (err)
        {
            case AL11.AL_NO_ERROR:
                return "AL_NO_ERROR";
            case AL11.AL_INVALID_NAME:
                return "AL_INVALID_NAME";
            case AL11.AL_INVALID_ENUM:
                return "AL_INVALID_ENUM";
            case AL11.AL_INVALID_VALUE:
                return "AL_INVALID_VALUE";
            case AL11.AL_INVALID_OPERATION:
                return "AL_INVALID_OPERATION";
            case AL11.AL_OUT_OF_MEMORY:
                return "AL_OUT_OF_MEMORY";
            default:
                return "No such error code";
        }
    }

}
