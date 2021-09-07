package de.jvstvshd.localstream.client.desktop.util;

import java.io.IOException;
import java.util.LinkedList;

public class CloseableLinkedList<T> extends LinkedList<T> implements CloseableQueue<T> {

    private boolean closed;

    @Override
    public boolean add(T t) {
        if (closed)
            throw new IllegalStateException("Cannot add elements after closing.");
        return super.add(t);
    }

    @Override
    public void close() throws IOException {
        closed = true;
    }
}
