package org.picocontainer.extras;

import org.picocontainer.PicoIntrospectionException;

import java.lang.reflect.Method;

/**
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.2 $
 */
public class NoArgumentForSetterException extends PicoIntrospectionException {
    private Method setter;

    public NoArgumentForSetterException(Method setter) {
        this.setter = setter;
    }

    public String getMessage() {
        return "No appropriate argument for " + setter.toString();
    }
}
