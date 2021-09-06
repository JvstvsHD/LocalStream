package de.jvstvshd.localstream.server;

import de.jvstvshd.localstream.event.EventBusImpl;
import de.jvstvshd.localstream.event.EventDispatcher;
import de.jvstvshd.localstream.network.packets.PacketManager;
import de.jvstvshd.localstream.scheduling.LsScheduler;
import de.jvstvshd.localstream.scheduling.Scheduler;
import de.jvstvshd.localstream.server.config.ConfigFile;
import de.jvstvshd.localstream.server.config.Configuration;
import de.jvstvshd.localstream.server.database.DataSourceManager;
import de.jvstvshd.localstream.server.network.NettyServer;
import de.jvstvshd.localstream.server.title.TitleManager;
import de.jvstvshd.localstream.server.util.ServerStartException;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import static org.apache.logging.log4j.LogManager.getLogger;

public class LocalStreamServer {

    private final static Logger logger = getLogger(LocalStreamServer.class);
    private final NettyServer nettyServer;
    private final DataSourceManager dataSourceManager;
    private final Configuration configuration;
    private final ConfigFile config;
    private TitleManager titleManager;
    private final Scheduler scheduler;
    private final EventDispatcher eventDispatcher;

    public LocalStreamServer() throws IOException {
        this.configuration = Configuration.create();
        this.config = configuration.getConfigFile();
        this.nettyServer = new NettyServer(new InetSocketAddress(InetAddress.getLocalHost(), 8080), this);
        this.dataSourceManager = new DataSourceManager();
        this.scheduler = new LsScheduler();


        this.eventDispatcher = new EventDispatcher(new EventBusImpl(), scheduler);
    }

    public void start() throws ServerStartException {
        try {
            dataSourceManager.init(config.getDatabaseCredentials());
            nettyServer.setPacketManager(new PacketManager());
            nettyServer.start();
            this.titleManager = new TitleManager(dataSourceManager, scheduler, nettyServer.getManager());
        } catch (IOException exception) {
            throw new ServerStartException();
        }
    }

    public DataSourceManager getDataSourceManager() {
        return dataSourceManager;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public ConfigFile getConfig() {
        return config;
    }

    public TitleManager getTitleManager() {
        return titleManager;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }
}
