package org.nanocontainer.integrationkit;

import org.picocontainer.PicoException;

public class PicoCompositionException extends PicoException {

    protected Throwable cause;

    protected PicoCompositionException() {
        super();
    }

    public PicoCompositionException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    public PicoCompositionException(Throwable cause) {
        this.cause = cause;
    }

    public PicoCompositionException(String message) {
        super(message);
        cause = null;
    }

    public Throwable getCause() {
        return cause;
    }
}
