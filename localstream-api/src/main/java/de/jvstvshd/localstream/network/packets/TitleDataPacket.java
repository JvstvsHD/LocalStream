package de.jvstvshd.localstream.network.packets;

import de.jvstvshd.localstream.network.handling.PacketClientHandler;

public class TitleDataPacket extends ClientHandlingPacket {

    private byte[] bytes;
    private long number;

    public TitleDataPacket(PacketPriority priority, byte[] bytes, long number) {
        super(priority);
        this.bytes = bytes;
        this.number = number;
    }

    public TitleDataPacket(PacketPriority priority) {
        super(priority);
    }

    @Override
    public void read(PacketBuffer buffer) {
        number = buffer.readLong();
        this.bytes = new byte[buffer.readableBytes()];
        buffer.readBytes(bytes);
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeLong(number);
        buffer.writeBytes(bytes);
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public long getNumber() {
        return number;
    }

    @Override
    public void process(PacketClientHandler handler) {
        handler.handleTitleData(this);
    }
}
