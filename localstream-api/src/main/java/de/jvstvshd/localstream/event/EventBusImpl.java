package de.jvstvshd.localstream.event;


import net.kyori.event.EventSubscriber;
import net.kyori.event.SimpleEventBus;
import org.apache.logging.log4j.LogManager;

import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class EventBusImpl implements EventBus, AutoCloseable {

    private final Bus bus = new Bus();

    public EventBusImpl() {

    }

    public void post(Event event) {
        this.bus.post(event);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean shouldPost(Class<? extends Event> eventClass) {
        return this.bus.hasSubscribers(eventClass);
    }


    public void subscribe(EventListener listener) {
        listener.bind(this);
    }

    public <T extends Event> EventSubscription<T> subscribe(Class<T> eventClass, Consumer<? super T> handler) {
        Objects.requireNonNull(eventClass, "eventClass");
        Objects.requireNonNull(handler, "handler");
        try {
            return registerSubscription(eventClass, handler);
        } catch (EventException e) {
            LogManager.getLogger().warn("cannot subscribe to event " + eventClass, e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Event> Set<EventSubscription<T>> getSubscriptions(Class<T> paramClass) {
        return this.bus.getHandlers(paramClass);
    }

    private <T extends Event> EventSubscription<T> registerSubscription(Class<T> eventClass, Consumer<? super T> handler) throws EventException {
        if (!eventClass.isInterface())
            throw new EventException(new IllegalArgumentException("class " + eventClass + " is not an interface"));
        if (!Event.class.isAssignableFrom(eventClass))
            throw new EventException(new IllegalArgumentException("class " + eventClass + " does not implement HitMCEvent"));
        EventSubscriptionImpl<T> eventHandler = new EventSubscriptionImpl<>(this, eventClass, handler);
        this.bus.register(eventClass, eventHandler);
        return eventHandler;
    }

    public void unregisterHandler(EventSubscriptionImpl<?> handler) {
        this.bus.unregister(handler);
    }

    @Override
    public void close() {
        this.bus.unregisterAll();
    }

    private static final class Bus extends SimpleEventBus<Event> {

        public Bus() {
            super(Event.class);
        }

        @SuppressWarnings("NullableProblems")
        @Override
        protected boolean shouldPost(Event event, EventSubscriber<?> subscriber) {
            return true;
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        public <T extends Event> Set getHandlers(Class<T> eventClass) {
            return subscribers().values().stream()
                    .filter(s -> (s instanceof EventSubscription && ((EventSubscription)s).getEventClass().isAssignableFrom(eventClass)))
                    .map(s -> (EventSubscription)s)
                    .collect(Collectors.toSet());
        }
    }
}
