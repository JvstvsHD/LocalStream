package de.jvstvshd.localstream.event;

import java.util.function.Consumer;

public interface EventSubscription <T extends Event> extends AutoCloseable {

    Class<T> getEventClass();

    boolean isActive();

    void close();

    Consumer<? super T> getHandler();
}
