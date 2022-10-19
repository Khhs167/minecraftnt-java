package net.minecraftnt.client.sound;

import net.minecraftnt.util.Vector3;

import static org.lwjgl.openal.AL11.*;

public class SoundSource {

    private final int id;

    public SoundSource(boolean loop, boolean relative) {
        this.id = alGenSources();
        if (loop) {
            alSourcei(id, AL_LOOPING, AL_TRUE);
        }
        if (relative) {
            alSourcei(id, AL_SOURCE_RELATIVE, AL_TRUE);
        }
    }

    public void setBuffer(int bufferId) {
        stop();
        alSourcei(id, AL_BUFFER, bufferId);
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

    public void setLoop(boolean loop){
        alSourcei(id, AL_LOOPING, loop ? AL_TRUE : AL_FALSE);
    }

    public void setRelative(boolean relative){
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