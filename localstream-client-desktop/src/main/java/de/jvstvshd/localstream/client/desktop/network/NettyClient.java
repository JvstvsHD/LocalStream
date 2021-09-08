package de.jvstvshd.localstream.client.desktop.network;

import de.jvstvshd.localstream.client.desktop.LocalStreamClient;
import de.jvstvshd.localstream.client.desktop.gui.ReconnectDialogController;
import de.jvstvshd.localstream.common.network.NetworkManager;
import de.jvstvshd.localstream.common.network.packets.Packet;
import de.jvstvshd.localstream.common.network.packets.PacketManager;
import de.jvstvshd.localstream.common.network.serializing.PacketDeserializer;
import de.jvstvshd.localstream.common.network.serializing.PacketPrepender;
import de.jvstvshd.localstream.common.network.serializing.PacketSerializer;
import de.jvstvshd.localstream.common.network.serializing.PacketSplitter;
import de.jvstvshd.localstream.common.network.util.NettyUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetSocketAddress;

public class NettyClient {

    private InetSocketAddress address;
    private Bootstrap bootstrap;
    private PacketManager packetManager;
    private NetworkManager manager;

    public NettyClient(InetSocketAddress address, LocalStreamClient client) {
        this.address = address;
        this.packetManager = new PacketManager();
        Packet.defaultHandler = new ClientPacketHandler(client.getMediaSystem());
    }

    public NettyClient(String host, int port, LocalStreamClient client) {
        this(new InetSocketAddress(host, port), client);
    }

    public void start(NetworkManager networkManager) throws Exception {
        this.bootstrap = new Bootstrap();
        try {
            networkManager.setPacketHandler(Packet.defaultHandler);
            bootstrap
                    .group(NettyUtils.getEventLoopGroup())
                    //.group(new NioEventLoopGroup())
                    .channel(NettyUtils.getChannel())
                    //.channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast("splitter", new PacketSplitter())
                                    .addLast("deserializer", new PacketDeserializer(packetManager))
                                    .addLast("prepender", new PacketPrepender())
                                    .addLast("serializer", new PacketSerializer(packetManager))
                                    .addLast("packet_handler", manager = networkManager);
                            manager.addInactiveHandler(t -> handleConnectionReset(t));
                        }
                    });
            ChannelFuture future = bootstrap.connect("127.0.0.1", 8080).sync();
            if (!future.isDone())
                return;
            if (future.isSuccess()) {
                return;
            }
            throw (Exception) future.cause();
        } catch (Exception e) {
            handleConnectionReset(e);
            throw new IOException("Could not start netty based server", e);
        }
    }

    public void shutdown() {
        this.bootstrap.config().group().shutdownGracefully();
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public void setAddress(InetSocketAddress address) {
        this.address = address;
    }

    public void setPacketManager(PacketManager packetManager) {
        this.packetManager = packetManager;
    }

    public PacketManager getPacketManager() {
        return packetManager;
    }

    public NetworkManager getManager() {
        return manager;
    }

    private void handleConnectionReset(Throwable throwable) {
        Platform.runLater(() -> {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(LocalStreamClient.class.getResource("reconnect-dialog.fxml"));
            Parent root;
            try {
                root = loader.load();
            } catch (IOException e) {
                throw new RuntimeException("Could not load reconnect-dialog.fxml via FXMLLoader", e);
            }
            Scene scene = new Scene(root);
            ReconnectDialogController controller = loader.getController();
            controller.setStage(stage);
            controller.initReconnectCooldown(throwable);

            stage.setScene(scene);
            stage.setTitle("Verbindung zum Server unterbrochen");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        });
    }
}
