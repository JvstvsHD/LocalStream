package de.jvstvshd.localstream.common.event;


import java.lang.invoke.MethodHandles;

public class AbstractEvent implements Event {

    protected AbstractEvent() {}

    @Override
    public Class<? extends Event> getEventType() {
        return null;
    }

    public MethodHandles.Lookup mhl() {
        throw new UnsupportedOperationException();
    }
}
