package de.jvstvshd.localstream.client.util.requests;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class RequestSystem {

    private final Set<Request> requests = new HashSet<>();
    private boolean shutdown;

    public Throwable registerRequest(Request request) {
        if (shutdown)
            return new IllegalStateException("Cannot register requests after shutdown initiated.");
        Throwable validity = checkValidity(request);
        if (validity != null)
            return validity;
        boolean addPerformed = requests.add(request);
        if (!addPerformed)
            return new IllegalArgumentException("Request was already registered.");
        return null;
    }

    public Throwable checkValidity(Request request) {
        if (request.getRequestID() == null)
            return new NullPointerException("requestID cannot be null");
        AtomicBoolean returnValue = new AtomicBoolean(false);
        requests.stream().filter(r -> r.getRequestID().equals(request.getRequestID())).findAny().ifPresent(r -> returnValue.set(true));
        return returnValue.get() ? new IllegalArgumentException("A request with this requestID already was registered.") : null;
    }

    public void shutdown() {
        shutdown = true;
    }
}
