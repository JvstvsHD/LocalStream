package de.jvstvshd.localstream.common.network.packets.elements;


import de.jvstvshd.localstream.common.network.handling.PacketClientHandler;
import de.jvstvshd.localstream.common.network.packets.Packet;
import de.jvstvshd.localstream.common.network.packets.PacketPriority;

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
