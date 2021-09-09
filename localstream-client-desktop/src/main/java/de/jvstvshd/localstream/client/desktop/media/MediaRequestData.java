package de.jvstvshd.localstream.client.desktop.media;

import de.jvstvshd.localstream.client.desktop.util.requests.Request;
import de.jvstvshd.localstream.common.network.packets.elements.TitlePacket;
import de.jvstvshd.localstream.common.title.TitleAction;
import de.jvstvshd.localstream.common.title.TitleMetadata;

import java.util.UUID;
import java.util.function.Consumer;

public class MediaRequestData implements Request.Data {

    private final UUID requestId;
    private final Consumer<MediaSystem.SimpleResponse> consumer;
    private final TitleMetadata metadata;
    private final TitleAction action;

    public MediaRequestData(UUID requestId,
                            Consumer<MediaSystem.SimpleResponse> consumer,
                            TitleMetadata metadata, TitleAction action) {
        this.requestId = requestId;
        this.consumer = consumer;
        this.metadata = metadata;
        this.action = action;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public Consumer<MediaSystem.SimpleResponse> getConsumer() {
        return consumer;
    }

    public TitleMetadata getMetadata() {
        return metadata;
    }

    public TitleAction getAction() {
        return action;
    }
}
