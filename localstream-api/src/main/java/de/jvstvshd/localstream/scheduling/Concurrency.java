package de.jvstvshd.localstream.scheduling;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

public final class Concurrency {

    private static final Logger logger = LogManager.getLogger();
    public static final ThreadFactory FACTORY = new LsThreadFactory();
    public static final ScheduledExecutorService GLOBAL_SERVICE = Executors.newScheduledThreadPool(10, FACTORY);
    public static final Thread.UncaughtExceptionHandler EXCEPTION_HANDLER = (thread, throwable) -> {
        logger.log(Level.FATAL, "An uncaught exception occurred in" + thread.getName() + " (" + thread.getId() + ")", throwable);
    };
    static {
        Thread.setDefaultUncaughtExceptionHandler(EXCEPTION_HANDLER);
    }

    private static final class LsThreadFactory implements ThreadFactory {

        private final String prefix;
        private static int tasks = 1;

        public LsThreadFactory() {
            this.prefix = "LocalStream Task #";
        }

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, prefix + ++tasks);
        }
    }
}
