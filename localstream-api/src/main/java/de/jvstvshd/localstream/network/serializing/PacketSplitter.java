package de.jvstvshd.localstream.network.serializing;

import de.jvstvshd.localstream.network.packets.PacketBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;

import java.util.List;

public class PacketSplitter extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        in.markReaderIndex();
        byte[] bytes = new byte[3];

        for (int i = 0; i < bytes.length; ++i) {
            if (!in.isReadable()) {
                in.resetReaderIndex();
                return;
            }
            bytes[i] = in.readByte();
            if (bytes[i] >= 0) {
                PacketBuffer packetBuffer = new PacketBuffer(Unpooled.wrappedBuffer(bytes));
                try {

                    int j = packetBuffer.readVarInt();
                    if (in.readableBytes() >= j) {
                        out.add(in.readBytes(j));
                        return;
                    }
                    in.resetReaderIndex();
                } finally {
                    packetBuffer.release();
                }
                return;
            }
        }
        throw new CorruptedFrameException("length wider than 21-bit");
    }
}
