package de.jvstvshd.localstream.event;

import de.jvstvshd.localstream.network.NetworkManager;

public interface ConnectionCloseEvent extends Event {

    @Param(0)
    NetworkManager getNetworkManager();
}
