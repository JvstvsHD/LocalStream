package de.jvstvshd.localstream.client.desktop.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.io.OutputStream;

/**
 * A custom output stream for logging with the logging with {@link Logger}
 */
public class CustomOutputStream extends OutputStream {
    Logger logger;
    Level level;
    StringBuilder stringBuilder;

    public CustomOutputStream(Logger logger, Level level) {
        this.logger = logger;
        this.level = level;
        stringBuilder = new StringBuilder();
    }

    @Override
    public final void write(int i) {
        char c = (char) i;
        if (c == '\r' || c == '\n') {
            if (stringBuilder.length() > 0) {
                logger.log(level, stringBuilder.toString());
                stringBuilder = new StringBuilder();
            }
        } else
            stringBuilder.append(c);
    }
}