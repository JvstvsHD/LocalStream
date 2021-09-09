package de.jvstvshd.localstream.common.network.packets.elements;

import de.jvstvshd.localstream.common.network.handling.PacketClientHandler;
import de.jvstvshd.localstream.common.network.packets.PacketBuffer;
import de.jvstvshd.localstream.common.network.packets.PacketPriority;
import de.jvstvshd.localstream.common.title.TitleMetadata;

import javax.sound.sampled.AudioFormat;

/**
 * Sent for starting to play a new title to sending client.
 */
public class StartPlayPacket extends ClientHandlingPacket {

    private AudioFormat format;
    private long maxPacketsCount;
    private TitleMetadata metadata;

    /**
     * Constructs a new StartPlayPacket.
     * @param priority see {@link de.jvstvshd.localstream.common.network.packets.Packet#Packet(PacketPriority)}
     * @param format the (audio) format of the title.
     * @param maxPacketsCount max count of packets sent, <b>often bugged</b>
     * @param metadata the metadata of the title should play
     */
    public StartPlayPacket(PacketPriority priority, AudioFormat format, long maxPacketsCount, TitleMetadata metadata) {
        super(priority);
        this.format = format;
        this.maxPacketsCount = maxPacketsCount;
        this.metadata = metadata;
    }

    /**
     * Constructs a new StartPlayPacket - should only be used by reflection.
     * @param priority see {@link de.jvstvshd.localstream.common.network.packets.Packet#Packet(PacketPriority)}
     */
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

    /**
     * @return the audio format of the new title.
     */
    public AudioFormat getFormat() {
        return format;
    }

    /**
     * <b>This feature often returns incorrect data.</b>
     * @return the max. packets sent via the playing process of the title.
     */
    public long getMaxPacketsCount() {
        return maxPacketsCount;
    }

    /**
     * @return the metadata of the new title
     */
    public TitleMetadata getMetadata() {
        return metadata;
    }
}
