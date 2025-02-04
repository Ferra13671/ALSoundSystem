package com.ferra13671.ALSoundSystem;

public class BufferInfo {
    public final int bufferId;
    public final int sampleRate;
    public final int channels;

    public BufferInfo(int bufferId, int sampleRate, int channels) {
        this.bufferId = bufferId;
        this.sampleRate = sampleRate;
        this.channels = channels;
    }
}
