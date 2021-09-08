package de.jvstvshd.localstream.common.network.packets;

import de.jvstvshd.localstream.common.network.handling.PacketHandler;
import de.jvstvshd.localstream.common.network.handling.PacketServerHandler;

import java.lang.reflect.Field;
import java.util.Optional;

/**
 * The super class of all packets.<br>
 * Packets are used to write data simply through {@link io.netty.buffer.ByteBuf}s/{@link PacketBuffer}s.
 * @param <Handler> The handler the packet should be processed with.
 */
public abstract class Packet<Handler extends PacketHandler> implements PacketComparable<Packet<?>> {

    /**
     * The default packet handler for handling packets. May set to the same as
     */
    public static PacketHandler defaultHandler;

    private final PacketPriority priority;
    /**
     * Will soon be removed.
     * @deprecated No use.
     */
    @Deprecated(forRemoval = true)
    public PacketHandler packetHandler;

    /**
     * Creates a new packet with the given <code>priority</code>.
     * @param priority The priority of the packet.
     * @see PacketPriority
     */
    public Packet(PacketPriority priority) {
        this.priority = priority;
    }

    public PacketPriority getPriority() {
        return priority;
    }

    @Override
    public final Optional<Packet<?>> compare(Packet<?> comparator) {
        int packetPriority = comparator.getPriority().compareTo(getPriority());
        if (packetPriority == 0)
            return Optional.empty();
        if (packetPriority > 0)
            return Optional.of(comparator);
        return Optional.of(this);
    }

    /**
     * Reads data that is essential for the packet into it's fields using the {@link PacketBuffer} <code>buffer</code>.
     * @param buffer Packet buffer, in the most cases it comes from the decoding methods.
     */
    public abstract void read(PacketBuffer buffer);

    /**
     * Writes essential packet data via the <code>buffer</code> to the other connection participant.
     * @param buffer {@link PacketBuffer} in which the data should be written, comes mostly from encoding methods.
     */
    public abstract void write(PacketBuffer buffer);

    /**
     * Processes the packet with the <code>handler</code>.
     * @param handler Handler - often a implementation of {@link de.jvstvshd.localstream.common.network.handling.PacketClientHandler} or {@link PacketServerHandler}.
     * @see PacketHandler
     * @see de.jvstvshd.localstream.common.network.handling.PacketClientHandler
     * @see PacketServerHandler
     */
    public abstract void process(Handler handler);

    /**
     * <b><u>DEPRECATED - DESCRIPTION MISSING</u></b>
     * @param handler sets the public static final {@link PacketHandler} <code>defaultHandler</code> to the <code>handler</code> via reflection.
     * @return the set handler or the <code>defaultHandler</code> if any errors occurred.
     */
    @Deprecated
    public static PacketHandler setDefaultHandler(PacketHandler handler) {
        try {
            Field field = Packet.class.getDeclaredField("defaultHandler");
            boolean fieldAccessibility = field.isAccessible();
            field.setAccessible(true);
            field.set(Packet.class, handler);
            field.setAccessible(fieldAccessibility);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return defaultHandler;
        }
        return handler;
    }

    /**
     * Sets the <code>packetHandler</code> and the <code>defaultHandler></code> to the <code>newPacketHandler</code>
     * @param newPacketHandler the new packet handler which the packetHandler and defaultHandler should be.
     * @deprecated Access the <code>packetHandler</code> direct.
     */
    @Deprecated(forRemoval = true)
    public void setPacketHandler(PacketHandler newPacketHandler) {
        this.packetHandler = newPacketHandler;
        PacketHandler newDefaultPacketHandler = setDefaultHandler(newPacketHandler);
        if (newDefaultPacketHandler != newPacketHandler)
            throw new RuntimeException("The defaultHandler could not be changed. The cause is readable a bit over this exception.");
    }
}
