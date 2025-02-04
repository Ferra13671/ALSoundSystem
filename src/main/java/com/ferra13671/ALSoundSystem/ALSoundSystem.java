package com.ferra13671.ALSoundSystem;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename;
import static org.lwjgl.system.libc.LibCStdlib.free;

public class ALSoundSystem {

    private static long audioContext = Long.MIN_VALUE;
    private static long audioDevice = Long.MIN_VALUE;


    public static boolean reloadContext() {
        if (audioContext != Long.MIN_VALUE && audioDevice != Long.MIN_VALUE) destroy();

        //Initializing Audio Context
        String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
        audioDevice = alcOpenDevice(defaultDeviceName);

        int[] attributes = {0};
        audioContext = alcCreateContext(audioDevice, attributes);
        alcMakeContextCurrent(audioContext);

        ALCCapabilities alcCapabilities = ALC.createCapabilities(audioDevice);
        ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);

        if (!alCapabilities.OpenAL10) {
            System.err.println("[ALSoundSystem] Audio Library not supported!");
            return false;
        }
        return true;
        /////////////////
    }


    public static void destroy() {
        alcDestroyContext(audioContext);
        alcCloseDevice(audioDevice);
    }

    public static BufferInfo generateBuffer(String path) {
        IntBuffer channelsBuffer = MemoryUtil.memAllocInt(1);
        IntBuffer sampleRateBuffer = MemoryUtil.memAllocInt(1);

        ShortBuffer rawAudioBuffer = stb_vorbis_decode_filename(path, channelsBuffer, sampleRateBuffer);

        if (rawAudioBuffer == null) {
            System.err.println("[ALSoundSystem] Could not load music '" + path + "'");
            MemoryUtil.memFree(channelsBuffer);
            MemoryUtil.memFree(sampleRateBuffer);
            return null;
        }

        int channels = channelsBuffer.get();
        int sampleRate = sampleRateBuffer.get();

        MemoryUtil.memFree(channelsBuffer);
        MemoryUtil.memFree(sampleRateBuffer);

        int format = -1;
        if (channels == 1) {
            format = AL_FORMAT_MONO16;
        } else if (channels == 2) {
            format = AL_FORMAT_STEREO16;
        }

        int bufferId = alGenBuffers();
        alBufferData(bufferId, format, rawAudioBuffer, sampleRate);

        free(rawAudioBuffer);

        return new BufferInfo(bufferId, sampleRate, channels);
    }

    public static int generateSource(int bufferId, boolean loop) {
        int sourceId = alGenSources();

        alSourcei(sourceId, AL_BUFFER, bufferId);
        alSourcei(sourceId, AL_LOOPING, loop ? 1 : 0);
        alSourcei(sourceId, AL_POSITION, 0);
        alSourcef(sourceId, AL_GAIN, 1f); //volume

        return sourceId;
    }
}
