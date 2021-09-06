package de.jvstvshd.localstream.client.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.io.PrintStream;

/**
 * Logging system util class.
 */
public record Logging(Logger logger) {

    /**
     * Initialises the {@link Logging} system and sets the output streams.
     */
    public void init() {

        System.setOut(new PrintStream(new CustomOutputStream(logger, Level.INFO)));
        System.setErr(new PrintStream(new CustomOutputStream(logger, Level.FATAL)));
    }
}
