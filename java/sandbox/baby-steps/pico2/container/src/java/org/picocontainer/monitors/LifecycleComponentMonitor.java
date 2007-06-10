package org.picocontainer.monitors;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.PicoException;
import org.picocontainer.PicoLifecycleException;
import org.picocontainer.ComponentAdapter;

/**
 * A {@link ComponentMonitor} which collects lifecycle failures
 * and rethrows them on demand after the failures.
 * 
 * @author Paul Hammant
 * @author Mauro Talevi
 */
public final class LifecycleComponentMonitor implements ComponentMonitor {

    private final ComponentMonitor delegate;
    private final List lifecycleFailures = new ArrayList();

    public LifecycleComponentMonitor(ComponentMonitor delegate) {
        this.delegate = delegate;
    }

    public LifecycleComponentMonitor() {
        delegate = new NullComponentMonitor();
    }

    public Constructor instantiating(ComponentAdapter componentAdapter,
                                     Constructor constructor
    ) {
        return delegate.instantiating(componentAdapter, constructor);
    }

    public void instantiated(ComponentAdapter componentAdapter,
                             Constructor constructor,
                             Object instantiated,
                             Object[] parameters,
                             long duration) {
        delegate.instantiated(componentAdapter, constructor, instantiated, parameters, duration);
    }

    public void instantiationFailed(ComponentAdapter componentAdapter, Constructor constructor, Exception cause) {
        delegate.instantiationFailed(componentAdapter, constructor, cause);
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

    public void lifecycleInvocationFailed(Method method, Object instance, RuntimeException cause) {
        lifecycleFailures.add(cause);
        try {
            delegate.lifecycleInvocationFailed(method, instance, cause);
        } catch (PicoLifecycleException e) {
            // do nothing, exception already logged for later rethrow.
        }
    }

    public void noComponent(Object componentKey) {
        delegate.noComponent(componentKey);
    }


    public void rethrowLifecycleFailuresException() {
        throw new LifecycleFailuresException(lifecycleFailures);
    }

    /**
     * Subclass of {@link PicoException} that is thrown when the collected
     * lifecycle failures need to be be collectively rethrown.
     * 
     * @author Paul Hammant
     * @author Mauro Talevi
     */
    public final class LifecycleFailuresException extends PicoException {

        private final List lifecycleFailures;

        public LifecycleFailuresException(List lifecycleFailures) {
            this.lifecycleFailures = lifecycleFailures;
        }

        public String getMessage() {
            StringBuffer message = new StringBuffer();
            for (Object lifecycleFailure : lifecycleFailures) {
                Exception failure = (Exception)lifecycleFailure;
                message.append(failure.getMessage()).append(";  ");
            }
            return message.toString();
        }

        public Collection getFailures() {
            return lifecycleFailures;
        }
    }
}
