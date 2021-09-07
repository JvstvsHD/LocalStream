package de.jvstvshd.localstream.common.network.util;


import de.jvstvshd.localstream.common.network.packets.PacketBuffer;
import io.netty.buffer.ByteBuf;


/**
 * @deprecated This class is deprecated since {@link PacketBuffer} exists which is not only a replacement for
 * {@link BufferUtil} but also can be modified for reading more complex data types.<br>
 * This class will be removed in a functional beta/alpha release.
 */
@Deprecated(forRemoval = true)
public class BufferUtil {

    /**
     * Writes a compressed int to the buffer. The smallest number of bytes to fit the passed int will be written. Of
     * each such byte only 7 bits will be used to describe the actual value since its most significant bit dictates
     * whether the next byte is part of that same int. Micro-optimization for int values that are expected to have
     * values below 128.<br>
     * <i>Proudly stolen (incl. description) from the Minecraft PacketBuffer.</i>
     * @param buf {@link ByteBuf} in which the var int should be written.
     * @param input Var int.
     * @return The {@link ByteBuf} from <code>buf</code> with the written var int <code>input</code>.
     * @deprecated Use {@link PacketBuffer#writeVarInt(int)} instead.
     * For further information, see {@link BufferUtil}.
     */
    @Deprecated(forRemoval = true)
    public static ByteBuf writeVarInt(ByteBuf buf, int input) {
        while ((input & -128) != 0) {
            buf.writeByte(input & 127 | 128);
            input >>>= 7;
        }

        return buf.writeByte(input);
    }

    /**
     * Reads a compressed int from the buffer. To do so it maximally reads 5 byte-sized chunks whose most significant
     * bit dictates whether another byte should be read.<br>
     * <i>Proudly stolen (incl. description) from the Minecraft PacketBuffer.</i>
     * @param buf {@link ByteBuf} from which the var int should be read.
     * @return the var int read from the {@link ByteBuf} <code>buf</code>.
     * @deprecated Use {@link PacketBuffer#readVarInt()} instead.
     * For further information, see {@link BufferUtil}.
     */
    @Deprecated(forRemoval = true)
    public static int readVarInt(ByteBuf buf) {
        int i = 0;
        int j = 0;

        while (true) {
            byte b0 = buf.readByte();
            i |= (b0 & 127) << j++ * 7;

            if (j > 5) {
                throw new RuntimeException("VarInt too big");
            }

            if ((b0 & 128) != 128) {
                break;
            }
        }

        return i;
    }

    /**
     * Calculates the number of bytes required to fit the supplied int (0-5) if it were to be read/written using
     * readVarIntFromBuffer or writeVarIntToBuffer.<br>
     * <i>Proudly stolen (incl. description) from the Minecraft PacketBuffer.</i>
     * @param input int from which the size should be computed
     * @return the size of the int <code>input</code>.
     * @deprecated Use {@link PacketBuffer#getVarIntSize(int)} instead.
     * For further information, see {@link BufferUtil}.
     */
    @Deprecated(forRemoval = true)
    public static int getVarIntSize(int input) {
        for (int i = 1; i < 5; ++i) {
            if ((input & -1 << i * 7) == 0) {
                return i;
            }
        }

        return 5;
    }
}
