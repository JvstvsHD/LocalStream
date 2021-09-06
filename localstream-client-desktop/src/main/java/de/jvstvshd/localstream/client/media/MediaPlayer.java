package de.jvstvshd.localstream.client.media;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

public abstract class MediaPlayer {


    private final String title;
    private final long maxPackets;
    private final long durationTotal;
    protected long packets;

    public MediaPlayer(AudioFormat format, String title, long maxPackets, long durationTotal) throws LineUnavailableException {
        this.title = title;
        this.maxPackets = maxPackets;
        this.durationTotal = durationTotal;
    }

    public abstract void queue(byte[] data);

    public abstract void play() throws IOException;

    protected AudioFormat decodeFormat(AudioFormat format) {
        return new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,  // Encoding to use
                format.getSampleRate(),           // sample rate (same as base format)
                16,               // sample size in bits (thx to Javazoom)
                format.getChannels(),             // # of Channels
                format.getChannels() * 2,           // Frame Size
                format.getSampleRate(),           // Frame Rate
                false                 // Big Endian
        );
    }

    public String getTitle() {
        return title;
    }

    public long getMaxPackets() {
        return maxPackets;
    }

    public long getDurationTotal() {
        return durationTotal;
    }
}
