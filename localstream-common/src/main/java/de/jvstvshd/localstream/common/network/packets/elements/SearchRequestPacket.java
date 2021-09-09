package de.jvstvshd.localstream.common.network.packets.elements;

import de.jvstvshd.localstream.common.network.handling.PacketServerHandler;
import de.jvstvshd.localstream.common.network.packets.PacketBuffer;
import de.jvstvshd.localstream.common.network.packets.PacketPriority;

public class SearchRequestPacket extends ServerHandlingPacket {

    private String request;

    /**
     * Constructs a new SearchRequestPacket - should not be used out of reflection.
     * @param priority see {@link de.jvstvshd.localstream.common.network.packets.Packet#Packet(PacketPriority)}
     */
    public SearchRequestPacket(PacketPriority priority) {
        super(priority);
    }

    /**
     * Constructs a new SearchRequestPacket.
     * @param priority see {@link de.jvstvshd.localstream.common.network.packets.Packet#Packet(PacketPriority)}
     * @param request keyword containing the client's search
     */
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

    /**
     * @return the keyword the user wanted to see suggestions for
     */
    public String getRequest() {
        return request;
    }

}
