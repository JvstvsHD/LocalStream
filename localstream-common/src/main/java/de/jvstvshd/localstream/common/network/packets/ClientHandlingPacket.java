package de.jvstvshd.localstream.common.network.packets;


import de.jvstvshd.localstream.common.network.handling.PacketClientHandler;

/**
 * Packets that are handled in the client with {@link PacketClientHandler}'s implementations.
 */
public abstract class ClientHandlingPacket extends Packet<PacketClientHandler> {

    /**
     * Constructor from {@link Packet#Packet(PacketPriority)}.
     * @param priority Priority of the packet.
     */
    public ClientHandlingPacket(PacketPriority priority) {
        super(priority);
    }
}