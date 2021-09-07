package de.jvstvshd.localstream.common.event;

import de.jvstvshd.localstream.common.network.NetworkManager;

public interface ConnectionCloseEvent extends Event {

    @Param(0)
    NetworkManager getNetworkManager();
}
