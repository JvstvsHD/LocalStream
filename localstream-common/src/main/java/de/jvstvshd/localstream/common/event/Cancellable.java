package de.jvstvshd.localstream.common.event;

import java.util.concurrent.atomic.AtomicBoolean;

public interface Cancellable {

    @Param(-1)
    AtomicBoolean cancellationState();

    default boolean isCancelled() {
        return cancellationState().get();
    }

    default boolean setCancelled(boolean cancelled) {
        return cancellationState().getAndSet(cancelled);
    }
}
