package org.picoextras.integrationkit;

import org.picocontainer.PicoException;

public class PicoAssemblyException extends PicoException {

    protected Throwable cause;

    protected PicoAssemblyException() {
        super();
    }

    public PicoAssemblyException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    public PicoAssemblyException(Throwable cause) {
        this.cause = cause;
    }

    public PicoAssemblyException(String message) {
        super(message);
        cause = null;
    }

    public Throwable getCause() {
        return cause;
    }
}
