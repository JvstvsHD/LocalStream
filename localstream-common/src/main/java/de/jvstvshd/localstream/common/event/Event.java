package de.jvstvshd.localstream.common.event;

public interface Event {

    Class<? extends Event> getEventType();
}
