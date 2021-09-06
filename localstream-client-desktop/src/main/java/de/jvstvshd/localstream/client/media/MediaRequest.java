package de.jvstvshd.localstream.client.media;

import de.jvstvshd.localstream.client.util.requests.Request;
import de.jvstvshd.localstream.client.util.requests.RequestException;
import de.jvstvshd.localstream.client.util.requests.RequestSystem;
import de.jvstvshd.localstream.network.NetworkManager;
import de.jvstvshd.localstream.network.packets.PacketPriority;
import de.jvstvshd.localstream.network.packets.TitlePacket;

import java.util.UUID;

public class MediaRequest implements Request {

    private final RequestSystem requestSystem;
    private final NetworkManager manager;
    private final MediaRequestData requestData;

    public MediaRequest(MediaRequestData requestData, RequestSystem requestSystem, NetworkManager manager) {
        this.requestSystem = requestSystem;
        this.manager = manager;
        this.requestData = requestData;
    }

    public synchronized void request() {
        Throwable cause;
        if ((cause = requestSystem.registerRequest(this)) != null) {
            throw new RequestException("Could not perform request. ", cause);
        }
        manager.sendPacket(new TitlePacket(PacketPriority.HIGH, getRequestData().getAction(), getRequestData().getMetadata(), getRequestData().getRequestId()));

    }

    public MediaRequestData getRequestData() {
        return requestData;
    }

    @Override
    public UUID getRequestID() {
        return getRequestData().getRequestId();
    }
}
