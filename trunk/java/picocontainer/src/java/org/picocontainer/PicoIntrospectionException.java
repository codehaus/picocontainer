package org.picocontainer;

/**
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.3 $
 */
public class PicoIntrospectionException extends PicoRegistrationException {
    public PicoIntrospectionException() {
    }

    protected PicoIntrospectionException(String message) {
        super(message);
    }

    public PicoIntrospectionException(Throwable cause) {
        super(cause);
    }

    public PicoIntrospectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
