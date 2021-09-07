package de.jvstvshd.localstream.client.desktop.util;

public interface Manager {

    void shutdown();

    void init() throws Exception;

    void start();
}
