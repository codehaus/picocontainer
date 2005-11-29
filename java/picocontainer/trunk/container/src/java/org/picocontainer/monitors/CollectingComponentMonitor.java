package org.picocontainer.monitors;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.picocontainer.ComponentMonitor;

public class CollectingComponentMonitor implements ComponentMonitor {

    private final ComponentMonitor delegate;
    private final List lifecycleFailures = new ArrayList();

    public CollectingComponentMonitor(ComponentMonitor delegate) {
        this.delegate = delegate;
    }

    public CollectingComponentMonitor() {
        delegate = new NullComponentMonitor();
    }

    public void instantiating(Constructor constructor) {
        delegate.instantiating(constructor);
    }

    public void instantiated(Constructor constructor, long duration) {
        delegate.instantiated(constructor, duration);
    }

    public void instantiationFailed(Constructor constructor, Exception cause) {
        delegate.instantiationFailed(constructor, cause);
    }

    public void invoking(Method method, Object instance) {
        delegate.invoking(method, instance);
    }

    public void invoked(Method method, Object instance, long duration) {
        delegate.invoked(method, instance, duration);
    }

    public void invocationFailed(Method method, Object instance, Exception cause) {
        delegate.invocationFailed(method, instance, cause);
    }

    public void lifecycleFailure(Method method, Object instance, RuntimeException cause) {
        lifecycleFailures.add(cause);
        delegate.lifecycleFailure(method, instance, cause);
    }


    public void reThrowLifecycleExceptions() {
        if (lifecycleFailures.size() == 1) {
            throw (RuntimeException) lifecycleFailures.get(0);
        }
        if (lifecycleFailures.size() > 1) {
            throw new RuntimeException() {
                public String getMessage() {
                    StringBuffer message = new StringBuffer();
                    for (int i = 0; i < lifecycleFailures.size(); i++) {
                        RuntimeException runtimeException = (RuntimeException) lifecycleFailures.get(i);
                        message.append(runtimeException.getMessage()).append(";  ");
                    }
                    return message.toString();
                }
                public Collection getExceptionCollection() {
                    return lifecycleFailures;
                }
            };
        }
    }

}
