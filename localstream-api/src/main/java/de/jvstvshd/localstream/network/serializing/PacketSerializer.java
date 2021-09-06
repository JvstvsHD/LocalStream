package de.jvstvshd.localstream.network.serializing;

import de.jvstvshd.localstream.network.packets.Packet;
import de.jvstvshd.localstream.network.packets.PacketBuffer;
import de.jvstvshd.localstream.network.packets.PacketManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketSerializer extends MessageToByteEncoder<Packet> {

    private final PacketManager packetManager;

    public PacketSerializer(PacketManager packetManager) {
        this.packetManager = packetManager;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet msg, ByteBuf out) throws Exception {
        int id = packetManager.getIdForPacket(msg.getClass());
        if (id == -1)
            throw new EncoderException("Cannot serialize unregistered packet.");
        PacketBuffer buffer = new PacketBuffer(out);
        int prio = msg.getPriority().getPriority();
        buffer.writeInt(id);
        buffer.writeInt(prio);
        msg.write(buffer);
    }
}
