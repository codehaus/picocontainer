package org.picocontainer.defaults;

import org.picocontainer.PicoInitializationException;

import java.lang.reflect.Constructor;

// Placeholder till the right version gets booked in.

public class CyclicDependencyException extends PicoInitializationException {
    private final Constructor constructor;

    public CyclicDependencyException(Constructor constructor) {
        this.constructor = constructor;
    }

    public Constructor getConstructor() {
        return constructor;
    }

    public String getMessage() {
        return "Cyclic dependency: " + constructor.getName() + "(" + getCtorParams(constructor) + ")";
    }

    private String getCtorParams(Constructor constructor) {
        String retval = "";
        Class[] parameterTypes = constructor.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            retval = retval + parameterTypes[i].getName();
            if (i+1 < parameterTypes.length) {
                retval += ",";
            }
        }
        return retval;
    }
}
