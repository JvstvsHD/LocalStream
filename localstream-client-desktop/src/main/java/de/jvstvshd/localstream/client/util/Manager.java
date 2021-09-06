package de.jvstvshd.localstream.client.util;

public interface Manager {

    void shutdown();

    void init() throws Exception;

    void start();
}
