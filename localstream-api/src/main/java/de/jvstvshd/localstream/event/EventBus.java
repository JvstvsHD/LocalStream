package de.jvstvshd.localstream.event;

import java.util.Set;
import java.util.function.Consumer;

public interface EventBus {

    <T extends Event> EventSubscription<T> subscribe(Class<T> paramClass, Consumer<? super T> paramConsumer);

    <T extends Event> Set<EventSubscription<T>> getSubscriptions(Class<T> paramClass);
}
