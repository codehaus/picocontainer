package org.picoextras.script;

import org.picocontainer.PicoException;

/**
 * @deprecated Rename to PicoAssemblyException and throw from
 * {@link org.picoextras.integrationkit.ContainerAssembler}.
 */
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
