package de.jvstvshd.localstream.network.serializing;

import de.jvstvshd.localstream.network.packets.PacketBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketPrepender extends MessageToByteEncoder<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        int i = msg.readableBytes();
        int j = PacketBuffer.getVarIntSize(i);
        if (j > 3)
            throw new IllegalArgumentException("unable to fit " + i + " into 3");
        PacketBuffer buffer = new PacketBuffer(out);
        buffer.ensureWritable(j + i);
        buffer.writeVarInt(i);
        buffer.writeBytes(msg, msg.readerIndex(), i);
    }
}
