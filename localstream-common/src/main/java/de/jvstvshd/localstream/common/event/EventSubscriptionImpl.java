package de.jvstvshd.localstream.common.event;

import net.kyori.event.EventSubscriber;
import org.apache.logging.log4j.LogManager;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class EventSubscriptionImpl< T extends Event> implements EventSubscription<T>, EventSubscriber<T> {
    private final EventBusImpl eventBus;

    private final Class<T> eventClass;

    private final Consumer<? super T> consumer;

    private final AtomicBoolean active = new AtomicBoolean(true);

    public EventSubscriptionImpl(EventBusImpl eventBus, Class<T> eventClass, Consumer<? super T> consumer) {
        this.eventBus = eventBus;
        this.eventClass = eventClass;
        this.consumer = consumer;
    }

    @Override
    public boolean isActive() {
        return active.get();
    }

    @Override
    public void close() {
        if (!this.active.getAndSet(false))
            return;
        this.eventBus.unregisterHandler(this);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void invoke(T event) throws Throwable {
        try {
            this.consumer.accept(event);
        } catch (Throwable throwable) {
            LogManager.getLogger().warn("Unable to pass event " + event.getEventType().getSimpleName() +  " to handler " + this.consumer.getClass().getName(), throwable);
        }
    }

    @Override
    public Class<T> getEventClass() {
        return eventClass;
    }

    @Override
    public Consumer<? super T> getHandler() {
        return consumer;
    }
}
