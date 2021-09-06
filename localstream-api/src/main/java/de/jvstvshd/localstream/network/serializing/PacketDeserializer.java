package de.jvstvshd.localstream.network.serializing;

import de.jvstvshd.localstream.network.packets.Packet;
import de.jvstvshd.localstream.network.packets.PacketBuffer;
import de.jvstvshd.localstream.network.packets.PacketManager;
import de.jvstvshd.localstream.network.packets.PacketPriority;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;

import java.io.IOException;
import java.util.List;

public class PacketDeserializer extends ByteToMessageDecoder {

    private final PacketManager packetManager;

    public PacketDeserializer(PacketManager packetManager) {
        this.packetManager = packetManager;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() == 0)
            return;
        PacketBuffer buffer = new PacketBuffer(in);
        int id = buffer.readInt();
        int prioInt = buffer.readInt();
        PacketPriority priority = PacketPriority.get(prioInt).orElse(PacketPriority.NORMAL);

        Packet<?> packet = null;
        try {
            packet = packetManager.getPacketById(id, priority);
        } catch (Exception e) {
            throw new IOException("Error occurred whilst decoding.", e);
        }
        packet.read(buffer);
        if (buffer.readableBytes() > 0)
            throw new DecoderException("There are some extra bytes (" + buffer.readableBytes() + " additional bytes). " +
                    "They should NOT be there since a later decoded packet may have a weird packet id because these extra bytes are decoded also.\nPacket class: " + packet.getClass());
        out.add(packet);
    }
}
