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
}
