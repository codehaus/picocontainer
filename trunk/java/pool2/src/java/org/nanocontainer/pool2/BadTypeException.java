package org.nanocontainer.pool2;

import org.picocontainer.PicoException;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class BadTypeException extends PicoException {
    private final Class expected;
    private final Class actual;

    public BadTypeException(Class expected, Class actual) {
        this.expected = expected;
        this.actual = actual;
    }

    public String getMessage() {
        return "Expected " + expected.getName() + ", but got " + actual.getName();
    }

    public Class getExpected() {
        return expected;
    }

    public Class getActual() {
        return actual;
    }
}
