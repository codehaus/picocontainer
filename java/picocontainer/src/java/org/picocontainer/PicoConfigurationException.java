package org.picocontainer;

public abstract class PicoConfigurationException extends Exception {

    protected final Throwable cause;

    protected PicoConfigurationException() {
        cause = null;
    }

    protected PicoConfigurationException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    protected PicoConfigurationException(Throwable cause) {
        this.cause = cause;
    }

    protected PicoConfigurationException(String message) {
        super(message);
        cause = null;
    }

    public Throwable getCause() {
        return cause;
    }
}
