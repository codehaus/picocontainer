package org.picocontainer;

/**
 * Superclass for all Exceptions in PicoContainer for lazy people
 * who want to catch only one Exception type.
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public abstract class PicoException extends RuntimeException {
    private Throwable cause;

    protected PicoException() {
    }

    protected PicoException(String message) {
        super(message);
    }

    protected PicoException(Throwable cause) {
        this.cause = cause;
    }

    protected PicoException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    public Throwable getCause() {
        return cause;
    }
}
