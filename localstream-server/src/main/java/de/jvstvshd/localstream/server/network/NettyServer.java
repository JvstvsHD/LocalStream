package de.jvstvshd.localstream.server.network;

import com.google.common.collect.Lists;
import de.jvstvshd.localstream.event.EventBusImpl;
import de.jvstvshd.localstream.event.EventDispatcher;
import de.jvstvshd.localstream.network.NetworkManager;
import de.jvstvshd.localstream.network.NetworkManagerImpl;
import de.jvstvshd.localstream.network.packets.PacketManager;
import de.jvstvshd.localstream.network.serializing.PacketDeserializer;
import de.jvstvshd.localstream.network.serializing.PacketPrepender;
import de.jvstvshd.localstream.network.serializing.PacketSerializer;
import de.jvstvshd.localstream.network.serializing.PacketSplitter;
import de.jvstvshd.localstream.network.util.NettyUtils;
import de.jvstvshd.localstream.server.LocalStreamServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

import static org.apache.logging.log4j.LogManager.getLogger;

public class NettyServer {

    private final static Logger logger = getLogger(NettyServer.class);

    private InetSocketAddress address;
    private final ServerBootstrap bootstrap;
    private final List<EventLoopGroup> eventLoopGroups = Lists.newArrayList();
    private PacketManager packetManager;
    private final LocalStreamServer lss;

    private NetworkManager manager;

    public NettyServer(InetSocketAddress address, LocalStreamServer lss) {
        this.address = address;
        this.lss = lss;
        this.bootstrap = new ServerBootstrap();
    }

    public NettyServer(int port, String host, LocalStreamServer lss) {
        this(new InetSocketAddress(host, port), lss);
    }

    public NettyServer(int port, LocalStreamServer lss) {
        this(new InetSocketAddress(port), lss);
    }

    public boolean start() throws IOException {
        Validate.notNull(address, "Cannot connect to no address");
        Validate.notNull(packetManager, "need a valid PacketManager for (de-)serializing packets.");
        EventLoopGroup bossGroup = NettyUtils.getEventLoopGroup();
        EventLoopGroup workerGroup = NettyUtils.getEventLoopGroup();
        eventLoopGroups.addAll(Arrays.asList(bossGroup, workerGroup));
        ChannelFuture future;
        Throwable failCause;
        try {
            future = bootstrap
                    .group(bossGroup, workerGroup)
                    .channel(NettyUtils.getServerChannel())
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast("splitter", new PacketSplitter())
                                    .addLast("deserializer", new PacketDeserializer(packetManager))
                                    .addLast("prepender", new PacketPrepender())
                                    .addLast("serializer", new PacketSerializer(packetManager))
                                    .addLast("packet_handler", manager = new NetworkManagerImpl());
                            manager.setPacketHandler(new ServerPacketHandler(manager, lss.getTitleManager(), lss));
                        }
                    }).bind(8080).sync();
            if (future.isSuccess())
                return true;

            failCause = future.cause();
        } catch (Exception e) {
            failCause = e;
        }
        throw new IOException("Could not start netty based server", failCause);
    }

    public NettyServer setAddress(InetSocketAddress address) {
        Validate.notNull(address);
        this.address = address;
        return this;
    }

    public NettyServer setPacketManager(PacketManager newManager) {
        Validate.notNull(newManager);
        this.packetManager = newManager;
        return this;
    }

    public void shutdown() {
        for (EventLoopGroup eventLoopGroup : eventLoopGroups) {
            eventLoopGroup.shutdownGracefully();
        }
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public boolean restart() throws IOException {
        shutdown();
        return start();
    }

    public boolean restart(InetSocketAddress address) throws IOException {
        setAddress(address);
        return restart();
    }

    public NetworkManager getManager() {
        return manager;
    }
}
