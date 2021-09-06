package de.jvstvshd.localstream.event;

public interface Event {

    Class<? extends Event> getEventType();
}
