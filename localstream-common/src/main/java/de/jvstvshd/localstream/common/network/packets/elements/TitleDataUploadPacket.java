package de.jvstvshd.localstream.common.network.packets.elements;

import de.jvstvshd.localstream.common.network.handling.PacketServerHandler;
import de.jvstvshd.localstream.common.network.packets.PacketBuffer;
import de.jvstvshd.localstream.common.network.packets.PacketPriority;

import java.util.UUID;

public class TitleDataUploadPacket extends ServerHandlingPacket {
    private UUID uuid;
    private byte[] transferData;
    private long number;

    public TitleDataUploadPacket(PacketPriority priority, byte[] transferData, UUID uuid, long number) {
        super(priority);
        this.transferData = transferData;
        this.uuid = uuid;
        this.number = number;
    }

    public TitleDataUploadPacket(PacketPriority priority) {
        super(priority);
    }

    @Override
    public void read(PacketBuffer buffer) {
        int length = buffer.readInt();
        this.transferData = new byte[length];
        buffer.readBytes(transferData);
        uuid = buffer.readUniqueId();
        number = buffer.readLong();
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeInt(transferData.length);
        buffer.writeBytes(transferData);
        buffer.writeUniqueId(uuid);
        buffer.writeLong(number);
    }

    public byte[] getTransferData() {
        return transferData;
    }

    public void setTransferData(byte[] transferData) {
        this.transferData = transferData;
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public void process(PacketServerHandler handler) {
        handler.handleUpload(this);
    }
}
