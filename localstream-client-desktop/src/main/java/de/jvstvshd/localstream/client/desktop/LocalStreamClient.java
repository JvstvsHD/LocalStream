package de.jvstvshd.localstream.client.desktop;

import de.jvstvshd.localstream.client.desktop.gui.GuiController;
import de.jvstvshd.localstream.client.desktop.media.MediaSystem;
import de.jvstvshd.localstream.client.desktop.network.NettyClient;
import de.jvstvshd.localstream.client.desktop.util.Logging;
import de.jvstvshd.localstream.client.desktop.util.activity.NetworkActivities;
import de.jvstvshd.localstream.client.desktop.util.requests.RequestSystem;
import de.jvstvshd.localstream.common.event.EventBusImpl;
import de.jvstvshd.localstream.common.event.EventDispatcher;
import de.jvstvshd.localstream.common.network.NetworkManager;
import de.jvstvshd.localstream.common.network.NetworkManagerImpl;
import de.jvstvshd.localstream.common.scheduling.LsScheduler;
import de.jvstvshd.localstream.common.scheduling.Scheduler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class LocalStreamClient extends Application {

    private final static Logger logger = LogManager.getLogger(LocalStreamClient.class);

    private final NettyClient nettyClient;
    private MediaSystem mediaSystem;
    private final Scheduler scheduler;
    private Stage primaryStage;
    private final EventDispatcher eventDispatcher;
    private final NetworkManager networkManager;
    private final NetworkActivities activities;

    public LocalStreamClient() {
        this.scheduler = new LsScheduler();
        //standard connection
        this.eventDispatcher = new EventDispatcher(new EventBusImpl(), scheduler);

        networkManager = new NetworkManagerImpl();
        this.activities = new NetworkActivities();
        this.mediaSystem = new MediaSystem(networkManager, scheduler, new RequestSystem(), activities);
        this.nettyClient = new NettyClient("127.0.0.1", 8080, this);

    }

    @Override
    public void start(Stage stage) throws IOException {
        new Logging(logger).init();
        GuiController.setClient(this);
        this.nettyClient.setAddress(new InetSocketAddress(InetAddress.getLocalHost(), 8080));
        try {
            nettyClient.start(networkManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        primaryStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(LocalStreamClient.class.getResource("lsc-gui.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setOnCloseRequest(event -> {
            ChannelFuture closeFuture = nettyClient.getManager().close();
            if (closeFuture != null) {
                closeFuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            }
            mediaSystem.shutdown();
            System.exit(0);
        });
        stage.setTitle("LocalStreamClient");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        System.out.println("d");
        //--module-path C:\IntelliJ\Projects\LocalStream\localstream-client-desktop\libs\javafx-sdk-zip\javafx-sdk-17\lib --add-modules=javafx.controls,javafx.fxml
        /*JavaFXInstaller installer = new JavaFXInstaller();
        installer.installJavaFX();*/
        launch(args);
    }

    public MediaSystem getMediaSystem() {
        return mediaSystem;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    public NettyClient getNettyClient() {
        return nettyClient;
    }

    public NetworkActivities getActivities() {
        return activities;
    }
}