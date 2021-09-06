package de.jvstvshd.localstream.network.packets;

import de.jvstvshd.localstream.network.handling.PacketServerHandler;

public abstract class ServerHandlingPacket extends Packet<PacketServerHandler> {

    public ServerHandlingPacket(PacketPriority priority) {
        super(priority);
    }
}
