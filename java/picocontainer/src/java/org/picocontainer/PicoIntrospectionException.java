package org.picocontainer;

/**
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.3 $
 */
public abstract class PicoIntrospectionException extends PicoInitializationException {
    ///CLOVER:OFF
    protected PicoIntrospectionException() {
    }

    protected PicoIntrospectionException(String message) {
        super(message);
    }

    protected PicoIntrospectionException(String message, Exception cause) {
        super(message, cause);
    }
    ///CLOVER:ON
}
