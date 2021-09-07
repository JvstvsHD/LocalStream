package de.jvstvshd.localstream.common.network.packets;

import de.jvstvshd.localstream.common.network.handling.PacketClientHandler;
import de.jvstvshd.localstream.common.title.TitleMetadata;

import javax.sound.sampled.AudioFormat;

public class StartPlayPacket extends ClientHandlingPacket {

    private AudioFormat format;
    private long maxPacketsCount;
    private TitleMetadata metadata;

    public StartPlayPacket(PacketPriority priority, AudioFormat format, long maxPacketsCount, TitleMetadata metadata) {
        super(priority);
        this.format = format;
        this.maxPacketsCount = maxPacketsCount;
        this.metadata = metadata;
    }

    public StartPlayPacket(PacketPriority priority) {
        super(priority);
    }

    @Override
    public void read(PacketBuffer buffer) {
        this.format = buffer.readAudioFormat();
        maxPacketsCount = buffer.readLong();
        metadata = buffer.readTitleMetadata();
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeAudioFormat(format);
        buffer.writeLong(maxPacketsCount);
        buffer.writeTitleMetadata(metadata);
    }

    @Override
    public void process(PacketClientHandler handler) {
        handler.handleStartPlay(this);
    }

    public AudioFormat getFormat() {
        return format;
    }

    public long getMaxPacketsCount() {
        return maxPacketsCount;
    }

    public TitleMetadata getMetadata() {
        return metadata;
    }
}
