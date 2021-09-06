package de.jvstvshd.localstream.network.packets;

import de.jvstvshd.localstream.network.handling.PacketServerHandler;

public class SearchRequestPacket extends ServerHandlingPacket {

    private String request;

    public SearchRequestPacket(PacketPriority priority) {
        super(priority);
    }

    public SearchRequestPacket(PacketPriority priority, String request) {
        super(priority);
        this.request = request;
    }

    @Override
    public void read(PacketBuffer buffer) {
        request = buffer.readString();
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeString(request);
    }

    @Override
    public void process(PacketServerHandler handler) {
        handler.handleSearchRequest(this);
    }

    public String getRequest() {
        return request;
    }

}
