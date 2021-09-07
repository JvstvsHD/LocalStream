package de.jvstvshd.localstream.common.network;

import de.jvstvshd.localstream.common.network.handling.PacketHandler;
import de.jvstvshd.localstream.common.network.packets.Packet;
import de.jvstvshd.localstream.common.network.util.NetworkTask;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.lang3.Validate;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class NetworkManagerImpl extends SimpleChannelInboundHandler<Packet<?>> implements NetworkManager {

    private PacketHandler packetHandler = Packet.defaultHandler;
    private Channel channel;
    private final Set<Consumer<Throwable>> consumers = new HashSet<>();
    private final Set<NetworkTask> tasks = new HashSet<>();

    @Override
    public void setPacketHandler(PacketHandler packetHandler) {
        Validate.notNull(packetHandler, "new PacketHandler cannot be null");
        this.packetHandler = packetHandler;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) {
        packet.process(packetHandler);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.channel = ctx.channel();
        System.out.println(ctx.channel().remoteAddress() + " has connected.");
    }

    @Override
    public boolean channelOpen() {
        return channel != null && channel.isActive();
    }

    @Override
    public void sendPacket(Packet<?> packet) {
        if (!channel.eventLoop().inEventLoop()) {
            channel.eventLoop().execute(() -> {
                if (!channelOpen())
                    return;
                channel.writeAndFlush(packet).addListener(future -> {
                    if (future.cause() != null)
                        future.cause().printStackTrace();
                });
            });
            return;
        }
        if (channelOpen()) {
            channel.writeAndFlush(packet).addListener(future -> {
                if (future.cause() != null)
                    future.cause().printStackTrace();
            });
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        System.out.println(ctx.channel().remoteAddress() + " has disconnected.");
        callShutdown();
        //dispatcher.dispatchConnectionClose(this);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
        consumers.forEach(consumer -> consumer.accept(cause));
        close();
    }

    @Override
    public ChannelFuture close() {
        if (channel == null)
            return null;
        return channel.close();
    }

    @Override
    public void addInactiveHandler(Consumer<Throwable> consumer) {
        consumers.add(consumer);
    }

    @Override
    public PacketHandler getPacketHandler() {
        return packetHandler;
    }

    @Override
    public void registerNetworkTask(NetworkTask task) {
        tasks.add(task);
    }

    @Override
    public void callShutdown() {
        for (NetworkTask task : tasks) {
            task.shutdown();
        }
    }
}
