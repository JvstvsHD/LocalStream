package de.jvstvshd.localstream.network.packets;

import de.jvstvshd.localstream.network.handling.PacketServerHandler;

import java.util.UUID;

public class TitleDataUploadPacket extends ServerHandlingPacket {
    private UUID uuid;
    private byte[] transferData;


    public TitleDataUploadPacket(PacketPriority priority, byte[] transferData, UUID uuid) {
        super(priority);
        this.transferData = transferData;
        this.uuid = uuid;
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

    }

    @Override
    public void write(PacketBuffer buffer) {

        buffer.writeInt(transferData.length);
        buffer.writeBytes(transferData);
        buffer.writeUniqueId(uuid);

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
