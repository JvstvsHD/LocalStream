package de.jvstvshd.localstream.common.network;


import de.jvstvshd.localstream.common.network.handling.PacketHandler;
import de.jvstvshd.localstream.common.network.packets.Packet;
import de.jvstvshd.localstream.common.network.util.NetworkTask;
import io.netty.channel.*;

import java.util.function.Consumer;

/**
 * Network manager for all network operations.<br>
 * As this is a implementation of {@link SimpleChannelInboundHandler}, incoming packets are read in the read method.
 * and then processed useing {@link Packet#process(PacketHandler)}.<br>
 * An associated {@link PacketHandler} can be set through {@link #setPacketHandler(PacketHandler)}. If no handler was set, {@link Packet#defaultHandler} is used.
 *
 */
public interface NetworkManager extends ChannelInboundHandler {


    /**
     * Sets the packet handler should be used for packets in {@link Packet#process(PacketHandler)}.
     * @param packetHandler a new {@link PacketHandler}, which is not allowed to be null.
     */
    void setPacketHandler(PacketHandler packetHandler);

    /**
     * Checks if the current channel is not null and if it is active
     * @return true if the channel is not null and active ({@link Channel#isActive()}.
     */
    boolean channelOpen();

    /**
     * Sends a packet to the other connection participant. If the current thread is not in {@link EventLoop#inEventLoop()}, the packet is sent
     * through {@link EventLoop#execute(Runnable)}, it is sent directly if {@link #channelOpen()} returns true.
     * @param packet The packet that should be sent to the other connection participant.
     */
    void sendPacket(Packet<?> packet);

    /**
     * Closes the channel.
     * @return the {@link ChannelFuture} returned by the close request.
     * @see Channel#close()
     */
    ChannelFuture close();

    /**
     * Adds a new listener that gets accepted when {@link #channelInactive(ChannelHandlerContext)} is called.
     * @param consumer A consumer for handling the channel inactivity
     */
    void addInactiveHandler(Consumer<Throwable> consumer);

    /**
     *
     * @return the {@link PacketHandler} which was set through {@link #setPacketHandler(PacketHandler)} or if this method was
     * never called {@link Packet#defaultHandler}.
     */
    PacketHandler getPacketHandler();

    void registerNetworkTask(NetworkTask task);

    void callShutdown();
}
