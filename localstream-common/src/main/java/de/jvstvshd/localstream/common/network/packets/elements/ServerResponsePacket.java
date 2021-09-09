package de.jvstvshd.localstream.common.network.packets.elements;


import de.jvstvshd.localstream.common.network.handling.PacketClientHandler;
import de.jvstvshd.localstream.common.network.packets.PacketBuffer;
import de.jvstvshd.localstream.common.network.packets.PacketPriority;

import java.util.Objects;
import java.util.UUID;

/**
 * Packet used for responding to the client's requests with a specific code which can have various meanings.
 */
public class ServerResponsePacket extends ClientHandlingPacket {

    private int responseCode;
    private UUID id;

    /**
     * Constructs a new ServerResponsePacket.
     * @param priority see {@link de.jvstvshd.localstream.common.network.packets.Packet#Packet(PacketPriority)}
     * @param responseCode the response code resulted from the corresponding action the server executed.
     * @param id ID of the request to identify the response.
     * @throws NullPointerException if the <code>id</code> is <code>null</code>
     */
    public ServerResponsePacket(PacketPriority priority, int responseCode, UUID id) {
        super(priority);
        this.responseCode = responseCode;
        Objects.requireNonNull(this.id = id);
    }

    /**
     * Creates a new ServerResponsePacket - should only be used by reflection.
     * @param priority see {@link de.jvstvshd.localstream.common.network.packets.Packet#Packet(PacketPriority)}
     */
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

    /**
     * @return the response code of this response.
     */
    public int getResponseCode() {
        return responseCode;
    }

    /**
     * @return the id of this response.
     */
    public UUID getId() {
        return id;
    }

    @Override
    public void process(PacketClientHandler handler) {
        handler.handleResponse(this);
    }
}
