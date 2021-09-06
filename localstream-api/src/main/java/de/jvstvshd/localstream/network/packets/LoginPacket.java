package de.jvstvshd.localstream.network.packets;


import de.jvstvshd.localstream.network.handling.PacketServerHandler;

/**
 * Packet sent directly after the connection established.
 */
public class LoginPacket extends ServerHandlingPacket {

    /**
     * See {@link ServerHandlingPacket#ServerHandlingPacket(PacketPriority)}
     */
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
