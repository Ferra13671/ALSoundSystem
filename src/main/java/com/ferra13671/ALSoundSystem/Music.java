package com.ferra13671.ALSoundSystem;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.lwjgl.openal.AL10.*;


public class Music {
    private int channels;
    private int sampleRate;
    private int bufferId;
    private int sourceId;

    private MusicState state = MusicState.STOPPED;

    protected Music() {}

    protected void _fromFile(File file, boolean loop) {
        BufferInfo bufferInfo = ALSoundSystem.generateBuffer(file.getAbsolutePath());
        if (bufferInfo == null) return;
        channels = bufferInfo.channels;
        sampleRate = bufferInfo.sampleRate;
        bufferId = bufferInfo.bufferId;
        sourceId = ALSoundSystem.generateSource(bufferId, loop);
    }

    public void play() {
        int state = alGetSourcei(sourceId, AL_SOURCE_STATE);
        if (state == AL_STOPPED) {
            this.state = MusicState.STOPPED;
            alSourcei(sourceId, AL_POSITION, 0);
        }
        if (state == AL_PAUSED) {
            this.state = MusicState.PAUSED;
        }

        if (this.state == MusicState.STOPPED || this.state == MusicState.PAUSED) {
            alSourcePlay(sourceId);
            this.state = MusicState.PLAYING;
        }
    }

    public void stop() {
        if (this.state == MusicState.PLAYING) {
            alSourceStop(sourceId);
            this.state = MusicState.STOPPED;
        }
    }

    public void pause() {
        if (this.state == MusicState.PLAYING) {
            alSourcePause(sourceId);
            this.state = MusicState.PAUSED;
        }
    }

    public void setVolume(float volume) {
        alSourcef(sourceId, AL_GAIN, volume);
    }

    public Music delete() {
        alDeleteSources(sourceId);
        alDeleteBuffers(bufferId);

        return null;
    }

    public boolean isPlaying() {
        int state = alGetSourcei(sourceId, AL_SOURCE_STATE);

        if (state == AL_STOPPED) {
            this.state = MusicState.STOPPED;
        } else if (state == AL_PAUSED) {
            this.state = MusicState.PAUSED;
        }
        return this.state == MusicState.PLAYING;
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

    public static Music fromFile(File file, boolean loop) {
        Music music = new Music();
        music._fromFile(file, loop);
        return music;
    }

    public static Music fromInputStream(InputStream stream, boolean loop) {
        Music music = new Music();

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

            music._fromFile(path.toFile(), loop);
            Files.delete(path);
        } finally {
            return music;
        }
    }
}