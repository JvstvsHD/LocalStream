package de.jvstvshd.localstream.common.event;

import com.google.common.collect.ImmutableList;
import de.jvstvshd.localstream.common.network.NetworkManager;
import de.jvstvshd.localstream.common.scheduling.Scheduler;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class EventDispatcher {

    private final EventBusImpl eventBus;
    private final Scheduler scheduler;

    public EventDispatcher(EventBusImpl eventBus, Scheduler scheduler) {
        this.eventBus = eventBus;
        this.scheduler = scheduler;
    }

    public EventBusImpl getEventBus() {
        return eventBus;
    }

    private <T extends Event> void postAsync(Class<T> eventClass, Object... params) {
        if (Cancellable.class.isAssignableFrom(eventClass))
            throw new RuntimeException(new EventException("Cancellable event cannot be posted async (" + eventClass + ")"));
        //TODO: Result-Eventif (Res)

        if (!this.eventBus.shouldPost(eventClass))
            return;
        scheduler.runAsync(() -> {
            T event = generate(eventClass, params);
            this.eventBus.post(event);
        });
    }

    private <T extends Event> void postSync(Class<T> eventClass, Object... params) {
        if (!this.eventBus.shouldPost(eventClass))
            return;
        T event = generate(eventClass, params);
        this.eventBus.post(event);
    }

    private <T extends Event & Cancellable> boolean postCancellable(Class<T> eventClass, Object... params) {
        boolean initialState = (Boolean) params[0];
        if (!this.eventBus.shouldPost(eventClass))
            return initialState;
        AtomicBoolean cancel = new AtomicBoolean(initialState);
        params[0] = cancel;
        postSync(eventClass, params);
        return cancel.get();
    }

    @SuppressWarnings("unchecked")
    private <T extends Event> T generate(Class<T> eventClass, Object... params) {
        try {
            return (T) GeneratedEventClass.generate(eventClass).newInstance(params);
        } catch (Throwable throwable) {
            throw new RuntimeException(new EventException("Exception occurred whilst generating event instance", throwable));
        }
    }

    public static List<Class< ? extends Event>> getKnownEventTypes() {
        return ImmutableList.of(ConnectionCloseEvent.class);
    }

    public void dispatchConnectionClose(NetworkManager manager) {
        postAsync(ConnectionCloseEvent.class, manager);
    }
}
