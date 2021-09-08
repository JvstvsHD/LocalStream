package de.jvstvshd.localstream.server;

import de.jvstvshd.localstream.server.utils.Logging;
import de.jvstvshd.localstream.server.utils.ServerStartException;
import org.apache.logging.log4j.Logger;

import java.text.DecimalFormat;

import static org.apache.logging.log4j.LogManager.getLogger;


public class LocalStreamServerLauncher {

    private static final Logger logger = getLogger(LocalStreamServer.class);

    public static void main(String[] args) throws Exception {
        new Logging(logger).init();
        LocalStreamServer server = new LocalStreamServer();
        System.out.println("Starting local stream server...");
        long start = System.nanoTime();
        try {
            server.start();
        } catch (ServerStartException e) {
            System.err.println("COULD NOT START SERVER!");
            throw e;
        }
        double diff = ((double) System.nanoTime() - (double) start) / 1_000_000.0;
        System.out.println("Done! Took " + new DecimalFormat("###.#####").format(diff) + "s");
    }
}
