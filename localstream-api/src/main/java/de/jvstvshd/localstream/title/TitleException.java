package de.jvstvshd.localstream.title;

public class TitleException extends RuntimeException {

    public TitleException() {
    }

    public TitleException(String message) {
        super(message);
    }

    public TitleException(String message, Throwable cause) {
        super(message, cause);
    }

    public TitleException(Throwable cause) {
        super(cause);
    }
}
