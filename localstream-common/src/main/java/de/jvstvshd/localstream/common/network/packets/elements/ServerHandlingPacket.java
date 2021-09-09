package de.jvstvshd.localstream.common.network.packets.elements;


import de.jvstvshd.localstream.common.network.handling.PacketServerHandler;
import de.jvstvshd.localstream.common.network.packets.Packet;
import de.jvstvshd.localstream.common.network.packets.PacketPriority;

/**
 * Superclass for all <a href=Packet>Packets</a> which should be handled by the server's {@link de.jvstvshd.localstream.common.network.handling.PacketHandler}.
 * It was created to simplify packet creation since the class parameter for the packet class must not be specified.
 */
public abstract class ServerHandlingPacket extends Packet<PacketServerHandler> {

    /**
     * Creates a new ServerHandlingPacket.
     * @param priority see see {@link de.jvstvshd.localstream.common.network.packets.Packet#Packet(PacketPriority)}
     */
    public ServerHandlingPacket(PacketPriority priority) {
        super(priority);
    }
}
