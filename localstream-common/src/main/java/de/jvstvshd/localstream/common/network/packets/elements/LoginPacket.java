package de.jvstvshd.localstream.common.network.packets.elements;


import de.jvstvshd.localstream.common.network.handling.PacketServerHandler;
import de.jvstvshd.localstream.common.network.packets.PacketBuffer;
import de.jvstvshd.localstream.common.network.packets.PacketPriority;

/**
 * Packet sent directly after the connection established.
 */
public class LoginPacket extends ServerHandlingPacket {

    public LoginPacket(PacketPriority priority) {
        super(priority);
    }

    @Override
    public void read(PacketBuffer buffer) {

    }

    @Override
    public void write(PacketBuffer buffer) {

    }
    @Override
    public void process(PacketServerHandler handler) {
        handler.handleLogin(this);
    }
}
