package de.jvstvshd.localstream.client.util;

import java.io.Closeable;
import java.util.Queue;

public interface CloseableQueue<T> extends Queue<T>, Closeable {
}
