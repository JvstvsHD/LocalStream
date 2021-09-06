package de.jvstvshd.localstream.server.util;

import java.io.IOException;

public class ServerStartException extends IOException {

    public ServerStartException() {
    }

    public ServerStartException(String message) {
        super(message);
    }

    public ServerStartException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerStartException(Throwable cause) {
        super(cause);
    }
}
