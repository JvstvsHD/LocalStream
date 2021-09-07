package de.jvstvshd.localstream.common.network.packets;


import de.jvstvshd.localstream.common.network.handling.PacketClientHandler;

import java.util.UUID;

public class ServerResponsePacket extends ClientHandlingPacket {

    private int responseCode;
    private UUID id;

    public ServerResponsePacket(PacketPriority priority, int responseCode, UUID id) {
        super(priority);
        this.responseCode = responseCode;
        this.id = id;
    }

    public ServerResponsePacket(PacketPriority priority) {
        super(priority);
    }

    @Override
    public void read(PacketBuffer buffer) {
        this.id = buffer.readUniqueId();
        this.responseCode = buffer.readInt();
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeUniqueId(id)
                .writeInt(responseCode);
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public void process(PacketClientHandler handler) {
        handler.handleResponse(this);
    }
}
