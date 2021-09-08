package de.jvstvshd.localstream.common.network.packets;


import de.jvstvshd.localstream.common.network.handling.PacketServerHandler;

public abstract class ServerHandlingPacket extends Packet<PacketServerHandler> {

    public ServerHandlingPacket(PacketPriority priority) {
        super(priority);
    }
}
