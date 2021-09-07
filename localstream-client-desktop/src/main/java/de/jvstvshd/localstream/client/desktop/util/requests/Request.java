package de.jvstvshd.localstream.client.desktop.util.requests;

import java.util.UUID;

public interface Request {

    UUID getRequestID();

    interface Data { }
}
