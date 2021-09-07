package de.jvstvshd.localstream.common.event;

import java.util.function.Consumer;

public interface EventSubscription <T extends Event> extends AutoCloseable {

    Class<T> getEventClass();

    boolean isActive();

    void close();

    Consumer<? super T> getHandler();
}
