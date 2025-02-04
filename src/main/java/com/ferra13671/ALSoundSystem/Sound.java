package com.ferra13671.ALSoundSystem;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.lwjgl.openal.AL10.*;


public class Sound {
    private int channels;
    private int sampleRate;
    private int bufferId;
    private int sourceId;

    protected Sound() {}

    protected void _fromFile(File file) {
        BufferInfo bufferInfo = ALSoundSystem.generateBuffer(file.getAbsolutePath());
        if (bufferInfo == null) return;
        channels = bufferInfo.channels;
        sampleRate = bufferInfo.sampleRate;
        bufferId = bufferInfo.bufferId;
        sourceId = ALSoundSystem.generateSource(bufferId, false);
    }

    public void play() {
        this.play(1f);
    }

    public void play(float volume) {
        int state = alGetSourcei(sourceId, AL_SOURCE_STATE);
        if (state == AL_PLAYING) {
            alSourceStop(sourceId);
            alSourcei(sourceId, AL_POSITION, 0);
        }

        alSourcef(sourceId, AL_GAIN, volume);
        alSourcePlay(sourceId);
    }

    public Sound delete() {
        alDeleteSources(sourceId);
        alDeleteBuffers(bufferId);

        return null;
    }

    public int getBufferId() {
        return this.bufferId;
    }

    public int getSourceId() {
        return this.sourceId;
    }

    public int getChannels() {
        return channels;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public static Sound fromFile(File file) {
        Sound sound = new Sound();
        sound._fromFile(file);
        return sound;
    }

    public static Sound fromInputStream(InputStream stream) {
        Sound sound = new Sound();

        Path path;
        try {
            path = Files.createFile(Paths.get("temp" + (int) (System.currentTimeMillis() / 10000)));
            BufferedInputStream inputStream = new BufferedInputStream(stream);
            FileOutputStream outputStream = new FileOutputStream(path.toFile());

            byte[] db = new byte[1024];
            int b;
            while ((b = inputStream.read(db, 0, 1024)) != -1)
                outputStream.write(db, 0, b);

            inputStream.close();
            outputStream.close();

            sound._fromFile(path.toFile());
            Files.delete(path);
        } finally {
            return sound;
        }
    }
}
