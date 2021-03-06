package de.jvstvshd.localstream.common.network.packets;

import com.google.common.base.Charsets;
import de.jvstvshd.localstream.common.title.TitleMetadata;
import de.jvstvshd.localstream.common.utils.AudioUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.codec.DecoderException;
import io.netty.util.ByteProcessor;
import io.netty.util.internal.UnstableApi;
import org.apache.commons.lang3.Validate;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

public class PacketBuffer extends ByteBuf {

    private final ByteBuf buf;

    public PacketBuffer(ByteBuf buf) {
        this.buf = buf;
    }

    @Override
    public int capacity() {
        return buf.capacity();
    }

    @Override
    public ByteBuf capacity(int newCapacity) {
        return buf.capacity(newCapacity);
    }

    @Override
    public int maxCapacity() {
        return buf.maxCapacity();
    }

    @Override
    public ByteBufAllocator alloc() {
        return buf.alloc();
    }

    @Deprecated
    @Override
    public ByteOrder order() {
        return buf.order();
    }

    @Deprecated
    @Override
    public ByteBuf order(ByteOrder endianness) {
        return buf.order(endianness);
    }

    @Override
    public ByteBuf unwrap() {
        return buf.unwrap();
    }

    @Override
    public boolean isDirect() {
        return buf.isDirect();
    }

    @Override
    public boolean isReadOnly() {
        return buf.isReadOnly();
    }

    @Override
    public ByteBuf asReadOnly() {
        return buf.asReadOnly();
    }

    @Override
    public int readerIndex() {
        return buf.readerIndex();
    }

    @Override
    public ByteBuf readerIndex(int readerIndex) {
        return buf.readerIndex(readerIndex);
    }

    @Override
    public int writerIndex() {
        return buf.writerIndex();
    }

    @Override
    public ByteBuf writerIndex(int writerIndex) {
        return buf.writerIndex(writerIndex);
    }

    @Override
    public ByteBuf setIndex(int readerIndex, int writerIndex) {
        return buf.setIndex(readerIndex, writerIndex);
    }

    @Override
    public int readableBytes() {
        return buf.readableBytes();
    }

    @Override
    public int writableBytes() {
        return buf.writableBytes();
    }

    @Override
    public int maxWritableBytes() {
        return buf.maxWritableBytes();
    }

    @Override
    public boolean isReadable() {
        return buf.isReadable();
    }

    @Override
    public boolean isReadable(int size) {
        return buf.isReadable(size);
    }

    @Override
    public boolean isWritable() {
        return buf.isWritable();
    }

    @Override
    public boolean isWritable(int size) {
        return buf.isWritable(size);
    }

    @Override
    public ByteBuf clear() {
        return buf.clear();
    }

    @Override
    public ByteBuf markReaderIndex() {
        return buf.markReaderIndex();
    }

    @Override
    public ByteBuf resetReaderIndex() {
        return buf.resetReaderIndex();
    }

    @Override
    public ByteBuf markWriterIndex() {
        return buf.markWriterIndex();
    }

    @Override
    public ByteBuf resetWriterIndex() {
        return buf.resetWriterIndex();
    }

    @Override
    public ByteBuf discardReadBytes() {
        return buf.discardReadBytes();
    }

    @Override
    public ByteBuf discardSomeReadBytes() {
        return buf.discardSomeReadBytes();
    }

    @Override
    public ByteBuf ensureWritable(int minWritableBytes) {
        return buf.ensureWritable(minWritableBytes);
    }

    @Override
    public int ensureWritable(int minWritableBytes, boolean force) {
        return buf.ensureWritable(minWritableBytes, force);
    }

    @Override
    public boolean getBoolean(int index) {
        return buf.getBoolean(index);
    }

    @Override
    public byte getByte(int index) {
        return buf.getByte(index);
    }

    @Override
    public short getUnsignedByte(int index) {
        return buf.getUnsignedByte(index);
    }

    @Override
    public short getShort(int index) {
        return buf.getShort(index);
    }

    @Override
    public short getShortLE(int index) {
        return buf.getShortLE(index);
    }

    @Override
    public int getUnsignedShort(int index) {
        return buf.getUnsignedShort(index);
    }

    @Override
    public int getUnsignedShortLE(int index) {
        return buf.getUnsignedShortLE(index);
    }

    @Override
    public int getMedium(int index) {
        return buf.getMedium(index);
    }

    @Override
    public int getMediumLE(int index) {
        return buf.getMediumLE(index);
    }

    @Override
    public int getUnsignedMedium(int index) {
        return buf.getUnsignedMedium(index);
    }

    @Override
    public int getUnsignedMediumLE(int index) {
        return buf.getUnsignedMediumLE(index);
    }

    @Override
    public int getInt(int index) {
        return buf.getInt(index);
    }

    @Override
    public int getIntLE(int index) {
        return buf.getIntLE(index);
    }

    @Override
    public long getUnsignedInt(int index) {
        return buf.getUnsignedInt(index);
    }

    @Override
    public long getUnsignedIntLE(int index) {
        return buf.getUnsignedIntLE(index);
    }

    @Override
    public long getLong(int index) {
        return buf.getLong(index);
    }

    @Override
    public long getLongLE(int index) {
        return buf.getLongLE(index);
    }

    @Override
    public char getChar(int index) {
        return buf.getChar(index);
    }

    @Override
    public float getFloat(int index) {
        return buf.getFloat(index);
    }

    @Override
    public double getDouble(int index) {
        return buf.getDouble(index);
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst) {
        return buf.getBytes(index, dst);
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int length) {
        return buf.getBytes(index, dst, length);
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
        return buf.getBytes(index, dst, dstIndex, length);
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst) {
        return buf.getBytes(index, dst);
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
        return buf.getBytes(index, dst, dstIndex, length);
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuffer dst) {
        return buf.getBytes(index, dst);
    }

    @Override
    public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
        return buf.getBytes(index, out, length);
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        return buf.getBytes(index, out, length);
    }

    @Override
    public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
        return buf.getBytes(index, out, position, length);
    }

    @Override
    public CharSequence getCharSequence(int index, int length, Charset charset) {
        return buf.getCharSequence(index, length, charset);
    }

    @Override
    public ByteBuf setBoolean(int index, boolean value) {
        return buf.setBoolean(index, value);
    }

    @Override
    public ByteBuf setByte(int index, int value) {
        return buf.setByte(index, value);
    }

    @Override
    public ByteBuf setShort(int index, int value) {
        return buf.setShort(index, value);
    }

    @Override
    public ByteBuf setShortLE(int index, int value) {
        return buf.setShortLE(index, value);
    }

    @Override
    public ByteBuf setMedium(int index, int value) {
        return buf.setMedium(index, value);
    }

    @Override
    public ByteBuf setMediumLE(int index, int value) {
        return buf.setMediumLE(index, value);
    }

    @Override
    public ByteBuf setInt(int index, int value) {
        return buf.setInt(index, value);
    }

    @Override
    public ByteBuf setIntLE(int index, int value) {
        return buf.setIntLE(index, value);
    }

    @Override
    public ByteBuf setLong(int index, long value) {
        return buf.setLong(index, value);
    }

    @Override
    public ByteBuf setLongLE(int index, long value) {
        return buf.setLongLE(index, value);
    }

    @Override
    public ByteBuf setChar(int index, int value) {
        return buf.setChar(index, value);
    }

    @Override
    public ByteBuf setFloat(int index, float value) {
        return buf.setFloat(index, value);
    }

    @Override
    public ByteBuf setDouble(int index, double value) {
        return buf.setDouble(index, value);
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src) {
        return buf.setBytes(index, src);
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int length) {
        return buf.setBytes(index, src, length);
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
        return buf.setBytes(index, src, srcIndex, length);
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src) {
        return buf.setBytes(index, src);
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
        return buf.setBytes(index, src, srcIndex, length);
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuffer src) {
        return buf.setBytes(index, src);
    }

    @Override
    public int setBytes(int index, InputStream in, int length) throws IOException {
        return buf.setBytes(index, in, length);
    }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
        return buf.setBytes(index, in, length);
    }

    @Override
    public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
        return buf.setBytes(index, in, position, length);
    }

    @Override
    public ByteBuf setZero(int index, int length) {
        return buf.setZero(index, length);
    }

    @Override
    public int setCharSequence(int index, CharSequence sequence, Charset charset) {
        return buf.setCharSequence(index, sequence, charset);
    }

    @Override
    public boolean readBoolean() {
        return buf.readBoolean();
    }

    @Override
    public byte readByte() {
        return buf.readByte();
    }

    @Override
    public short readUnsignedByte() {
        return buf.readUnsignedByte();
    }

    @Override
    public short readShort() {
        return buf.readShort();
    }

    @Override
    public short readShortLE() {
        return buf.readShortLE();
    }

    @Override
    public int readUnsignedShort() {
        return buf.readUnsignedShort();
    }

    @Override
    public int readUnsignedShortLE() {
        return buf.readUnsignedShortLE();
    }

    @Override
    public int readMedium() {
        return buf.readMedium();
    }

    @Override
    public int readMediumLE() {
        return buf.readMediumLE();
    }

    @Override
    public int readUnsignedMedium() {
        return buf.readUnsignedMedium();
    }

    @Override
    public int readUnsignedMediumLE() {
        return buf.readUnsignedMediumLE();
    }

    @Override
    public int readInt() {
        return buf.readInt();
    }

    @Override
    public int readIntLE() {
        return buf.readIntLE();
    }

    @Override
    public long readUnsignedInt() {
        return buf.readUnsignedInt();
    }

    @Override
    public long readUnsignedIntLE() {
        return buf.readUnsignedIntLE();
    }

    @Override
    public long readLong() {
        return buf.readLong();
    }

    @Override
    public long readLongLE() {
        return buf.readLongLE();
    }

    @Override
    public char readChar() {
        return buf.readChar();
    }

    @Override
    public float readFloat() {
        return buf.readFloat();
    }

    @Override
    public double readDouble() {
        return buf.readDouble();
    }

    @Override
    public ByteBuf readBytes(int length) {
        return buf.readBytes(length);
    }

    @Override
    public ByteBuf readSlice(int length) {
        return buf.readSlice(length);
    }

    @Override
    public ByteBuf readRetainedSlice(int length) {
        return buf.readRetainedSlice(length);
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst) {
        return buf.readBytes(dst);
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst, int length) {
        return buf.readBytes(dst, length);
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst, int dstIndex, int length) {
        return buf.readBytes(dst, dstIndex, length);
    }

    @Override
    public ByteBuf readBytes(byte[] dst) {
        return buf.readBytes(dst);
    }

    @Override
    public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
        return buf.readBytes(dst, dstIndex, length);
    }

    @Override
    public ByteBuf readBytes(ByteBuffer dst) {
        return buf.readBytes(dst);
    }

    @Override
    public ByteBuf readBytes(OutputStream out, int length) throws IOException {
        return buf.readBytes(out, length);
    }

    @Override
    public int readBytes(GatheringByteChannel out, int length) throws IOException {
        return buf.readBytes(out, length);
    }

    /**
     * No more deprecated.
     */
    @Override
    public CharSequence readCharSequence(int length, Charset charset) {
        return buf.readCharSequence(length, charset);
    }

    @Override
    public int readBytes(FileChannel out, long position, int length) throws IOException {
        return buf.readBytes(out, position, length);
    }

    @Override
    public ByteBuf skipBytes(int length) {
        return buf.skipBytes(length);
    }

    @Override
    public ByteBuf writeBoolean(boolean value) {
        return buf.writeBoolean(value);
    }

    @Override
    public ByteBuf writeByte(int value) {
        return buf.writeByte(value);
    }

    @Override
    public ByteBuf writeShort(int value) {
        return buf.writeShort(value);
    }

    @Override
    public ByteBuf writeShortLE(int value) {
        return buf.writeShortLE(value);
    }

    @Override
    public ByteBuf writeMedium(int value) {
        return buf.writeMedium(value);
    }

    @Override
    public ByteBuf writeMediumLE(int value) {
        return buf.writeMediumLE(value);
    }

    @Override
    public ByteBuf writeInt(int value) {
        return buf.writeInt(value);
    }

    @Override
    public ByteBuf writeIntLE(int value) {
        return buf.writeIntLE(value);
    }

    @Override
    public ByteBuf writeLong(long value) {
        return buf.writeLong(value);
    }

    @Override
    public ByteBuf writeLongLE(long value) {
        return buf.writeLongLE(value);
    }

    @Override
    public ByteBuf writeChar(int value) {
        return buf.writeChar(value);
    }

    @Override
    public ByteBuf writeFloat(float value) {
        return buf.writeFloat(value);
    }

    @Override
    public ByteBuf writeDouble(double value) {
        return buf.writeDouble(value);
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src) {
        return buf.writeBytes(src);
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src, int length) {
        return buf.writeBytes(src, length);
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src, int srcIndex, int length) {
        return buf.writeBytes(src, srcIndex, length);
    }

    @Override
    public ByteBuf writeBytes(byte[] src) {
        return buf.writeBytes(src);
    }

    @Override
    public ByteBuf writeBytes(byte[] src, int srcIndex, int length) {
        return buf.writeBytes(src, srcIndex, length);
    }

    @Override
    public ByteBuf writeBytes(ByteBuffer src) {
        return buf.writeBytes(src);
    }

    @Override
    public int writeBytes(InputStream in, int length) throws IOException {
        return buf.writeBytes(in, length);
    }

    @Override
    public int writeBytes(ScatteringByteChannel in, int length) throws IOException {
        return buf.writeBytes(in, length);
    }

    @Override
    public int writeBytes(FileChannel in, long position, int length) throws IOException {
        return buf.writeBytes(in, position, length);
    }

    @Override
    public ByteBuf writeZero(int length) {
        return buf.writeZero(length);
    }

    /**
     * @deprecated use {@link #writeCharSequence(CharSequence)} instead as you don't have to know the exact length there.
     */
    @Deprecated()
    @Override
    public int writeCharSequence(CharSequence sequence, Charset charset) {
        return buf.writeCharSequence(sequence, charset);
    }

    @Override
    public int indexOf(int fromIndex, int toIndex, byte value) {
        return buf.indexOf(fromIndex, toIndex, value);
    }

    @Override
    public int bytesBefore(byte value) {
        return buf.bytesBefore(value);
    }

    @Override
    public int bytesBefore(int length, byte value) {
        return buf.bytesBefore(length, value);
    }

    @Override
    public int bytesBefore(int index, int length, byte value) {
        return buf.bytesBefore(index, length, value);
    }

    @Override
    public int forEachByte(ByteProcessor processor) {
        return buf.forEachByte(processor);
    }

    @Override
    public int forEachByte(int index, int length, ByteProcessor processor) {
        return buf.forEachByte(index, length, processor);
    }

    @Override
    public int forEachByteDesc(ByteProcessor processor) {
        return buf.forEachByteDesc(processor);
    }

    @Override
    public int forEachByteDesc(int index, int length, ByteProcessor processor) {
        return buf.forEachByteDesc(index, length, processor);
    }

    @Override
    public ByteBuf copy() {
        return buf.copy();
    }

    @Override
    public ByteBuf copy(int index, int length) {
        return buf.copy(index, length);
    }

    @Override
    public ByteBuf slice() {
        return buf.slice();
    }

    @Override
    public ByteBuf retainedSlice() {
        return buf.retainedSlice();
    }

    @Override
    public ByteBuf slice(int index, int length) {
        return buf.slice(index, length);
    }

    @Override
    public ByteBuf retainedSlice(int index, int length) {
        return buf.retainedSlice(index, length);
    }

    @Override
    public ByteBuf duplicate() {
        return buf.duplicate();
    }

    @Override
    public ByteBuf retainedDuplicate() {
        return buf.retainedDuplicate();
    }

    @Override
    public int nioBufferCount() {
        return buf.nioBufferCount();
    }

    @Override
    public ByteBuffer nioBuffer() {
        return buf.nioBuffer();
    }

    @Override
    public ByteBuffer nioBuffer(int index, int length) {
        return buf.nioBuffer(index, length);
    }

    @Override
    public ByteBuffer internalNioBuffer(int index, int length) {
        return buf.internalNioBuffer(index, length);
    }

    @Override
    public ByteBuffer[] nioBuffers() {
        return buf.nioBuffers();
    }

    @Override
    public ByteBuffer[] nioBuffers(int index, int length) {
        return buf.nioBuffers(index, length);
    }

    @Override
    public boolean hasArray() {
        return buf.hasArray();
    }

    @Override
    public byte[] array() {
        return buf.array();
    }

    @Override
    public int arrayOffset() {
        return buf.arrayOffset();
    }

    @Override
    public boolean hasMemoryAddress() {
        return buf.hasMemoryAddress();
    }

    @Override
    public long memoryAddress() {
        return buf.memoryAddress();
    }

    @Override
    public String toString(Charset charset) {
        return buf.toString(charset);
    }

    @Override
    public String toString(int index, int length, Charset charset) {
        return buf.toString(index, length, charset);
    }

    @Override
    public int hashCode() {
        return buf.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return buf.equals(obj);
    }

    @Override
    public int compareTo(ByteBuf buffer) {
        return buf.compareTo(buffer);
    }

    @Override
    public String toString() {
        return buf.toString();
    }

    @Override
    public ByteBuf retain(int increment) {
        return buf.retain(increment);
    }

    @Override
    public ByteBuf retain() {
        return buf.retain();
    }

    @Override
    public ByteBuf touch() {
        return buf.touch();
    }

    @Override
    public ByteBuf touch(Object hint) {
        return buf.touch(hint);
    }

    @Override
    public int refCnt() {
        return buf.refCnt();
    }

    @Override
    public boolean release() {
        return buf.release();
    }

    @Override
    public boolean release(int decrement) {
        return buf.release(decrement);
    }

    /**
     * Writes a compressed int to the buffer. The smallest number of bytes to fit the passed int will be written. Of
     * each such byte only 7 bits will be used to describe the actual value since its most significant bit dictates
     * whether the next byte is part of that same int. Micro-optimization for int values that are expected to have
     * values below 128.<br>
     * <i>Proudly stolen (incl. description) from the Minecraft PacketBuffer.</i>
     *
     * @param value {@link Integer} that should be written compressed.
     * @return The underlying {@link ByteBuf} with the written, compressed int, specified in <code>value</code>.
     */
    public ByteBuf writeVarInt(int value) {
        while ((value & -128) != 0) {
            buf.writeByte(value & 127 | 128);
            value >>>= 7;
        }

        return buf.writeByte(value);
    }

    /**
     * Reads a compressed int from the buffer. To do so it maximally reads 5 byte-sized chunks whose most significant
     * bit dictates whether another byte should be read.<br>
     * <i>Proudly stolen (incl. description) from the Minecraft PacketBuffer.</i>
     *
     * @return the compressed int read from the underlying {@link ByteBuf}.
     * @throws RuntimeException if the var int is too big.
     */
    public int readVarInt() {
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
     * {@link #readVarInt()} or {@link #writeVarInt(int)}.<br>
     * <i>Proudly stolen (incl. description) from the Minecraft PacketBuffer.</i>
     *
     * @param varInt int from which the size should be computed
     * @return the number of bytes required to fit in <code>varInt</code>.
     * @see #readVarInt()
     * @see #writeVarInt(int)
     */
    public static int getVarIntSize(int varInt) {
        for (int i = 1; i < 5; ++i) {
            if ((varInt & -1 << i * 7) == 0) {
                return i;
            }
        }
        return 5;
    }

    /**
     * Writes a string. This string could be read with {@link #writeCharSequence(CharSequence, Charset)} <i>(unconfirmed)</i> or {@link #readString(int)}.
     * Also, the second variant does not require an exact length of the string but a max length which is pretty easier for the transfer of {@link String}s.
     *
     * @param value The {@link String} should be written.
     * @return this for builder.
     * @see #writeCharSequence(CharSequence, Charset)
     * @see #readString(int)
     */
    public PacketBuffer writeString(String value) {
        writeVarInt(value.getBytes(StandardCharsets.UTF_8).length);
        writeCharSequence(value, StandardCharsets.UTF_8);
        return this;
        /*byte[] bytes = value.getBytes(Charsets.UTF_8);

        if (bytes.length > 32767) {
            throw new EncoderException("String too big (was " + value.length() + " bytes encoded, max " + 32767 + ")");
        } else {
            int length = bytes.length;
            System.out.println("length = " + length);
            this.writeVarInt(length);
            this.writeBytes(bytes);
            return this;
        }*/
    }

    /**
     * Reads a {@link String} that is maximum <code>maxLength</code> characters long.
     *
     * @param maxLength Maximum length as {@link Integer} of the string to read.
     * @return The string that was read.
     * @throws DecoderException if
     *                          <ul>
     *                              <li>the received encoded string is longer than maximum allowed.</li>
     *                              <li>The received encoded string length is negative. This would be very weird.</li>
     *                              <li>The received string length is longer than specified in <code>maxLength</code></li>
     *                          </ul>
     * @see #readString()
     * @see #readCharSequence(int, Charset)
     * @see #readCharSequence()
     * @deprecated Use {@link #readString()} instead, as the <code>maxLength</code> is almost useless and was only used in Minecraft for such things as a scoreboard.
     */
    @UnstableApi
    @Deprecated(forRemoval = false)
    public String readString(int maxLength) {
        int varInt = this.readVarInt();
        return String.valueOf(readCharSequence(varInt, StandardCharsets.UTF_8));
    }

    public String readString() {
        int varInt = this.readVarInt();
        return String.valueOf(readCharSequence(varInt, StandardCharsets.UTF_8));
    }

    @Deprecated()
    public String readString_old() {
        int varInt = this.readVarInt();
        if (varInt < 0) {
            throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
        } else {
            byte[] bytes = new byte[varInt];
            getBytes(readerIndex(), bytes, 0, varInt);


            return new String(bytes, Charsets.UTF_8);
        }
    }

    public PacketBuffer writeUniqueId(UUID uuid) {
        writeLong(uuid.getMostSignificantBits());
        writeLong(uuid.getLeastSignificantBits());
        return this;
    }

    public UUID readUniqueId() {
        return new UUID(readLong(), readLong());
    }

    public PacketBuffer writeTitleMetadata(TitleMetadata metadata) {
        writeString(metadata.getName());
        writeLong(metadata.getLength());
        writeLong(metadata.getSize());
        writeUniqueId(metadata.getUuid());
        writeString(metadata.getInterpret());
        writeString(metadata.getTitleName());
        return this;
    }

    public TitleMetadata readTitleMetadata() {
        return new TitleMetadata(readString(), readLong(), readLong(), readUniqueId(), readString(), readString());
    }

    /**
     * @param sequence the char sequence should be written.
     * @return this for builders
     * @deprecated Use {@link #writeCharSequence(CharSequence, Charset)} instead
     */
    @Deprecated(forRemoval = true)
    public PacketBuffer writeCharSequence(CharSequence sequence) {
        return writeString(String.valueOf(sequence));
    }

    /**
     * @return the read {@link CharSequence} (from {@link #readString()})
     * @see #readCharSequence(int, Charset)
     * @see #readString()
     * @see #readString(int)
     * @deprecated This is only a replacement for {@link #readString()}, so use this or {@link #readCharSequence(int, Charset)}.
     */
    @Deprecated(forRemoval = true)
    public CharSequence readCharSequence() {
        return readString();
    }

    public void deadRead(byte[] dead) {
        readBytes(dead);
    }

    public void deadRead() {
        deadRead(new byte[readableBytes()]);
    }

    /**
     * Writes a audioformat through the underlying {@link ByteBuf}.
     *
     * @param value a non-null {@link AudioFormat}
     * @return this for builders
     */
    public PacketBuffer writeAudioFormat(AudioFormat value) {
        System.out.println(value.getEncoding().toString());
        Validate.notNull(value);
        writeEncoding(value.getEncoding())
                .writeFloat(value.getSampleRate())
                .writeInt(value.getSampleSizeInBits())
                .writeInt(value.getChannels())
                .writeInt(value.getFrameSize())
                .writeFloat(value.getFrameRate())
                .writeBoolean(value.isBigEndian());
        return this;
    }

    /**
     * Reads a audioformat from the underlying {@link ByteBuf}.
     *
     * @return the read byte buf.
     */
    public AudioFormat readAudioFormat() {
        return new AudioFormat(readEncoding(),
                readFloat(),
                readInt(),
                readInt(),
                readInt(),
                readFloat(),
                readBoolean());
    }

    /**
     * Writes an {@link AudioFormat.Encoding} to the underlying {@link ByteBuf} via the name it contains.
     *
     * @param encoding value
     * @return this
     */
    public PacketBuffer writeEncoding(AudioFormat.Encoding encoding) {
        return writeString(encoding.toString());
    }

    /**
     * Reads an {@link AudioFormat.Encoding} from the underlying {@link ByteBuf}.
     *
     * @return the read ByteBuf
     * @throws IllegalStateException if the read string matches no encoding.
     */
    public AudioFormat.Encoding readEncoding() {
        String s;
        Optional<AudioFormat.Encoding> encoding = AudioUtils.getEncoding(s = readString());
        if (encoding.isEmpty())
            throw new IllegalStateException("Unexpected value: " + s);
        return encoding.get();
    }
}
