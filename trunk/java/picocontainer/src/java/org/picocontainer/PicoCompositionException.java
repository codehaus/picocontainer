package org.picocontainer;

public abstract class PicoCompositionException extends Exception {

    protected final Throwable cause;

    protected PicoCompositionException() {
        cause = null;
    }

    protected PicoCompositionException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    protected PicoCompositionException(Throwable cause) {
        this.cause = cause;
    }

    protected PicoCompositionException(String message) {
        super(message);
        cause = null;
    }

    public Throwable getCause() {
        return cause;
    }
}
