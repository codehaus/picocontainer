package org.picocontainer.extras;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.InterfaceFinder;
import org.picocontainer.defaults.NotConcreteRegistrationException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.HashMap;

/**
 * This component adapter makes it possible to hide the implementation
 * of a real subject (behind a proxy). It is also possible to {@link #hotSwap(Object, Object)}
 * the subject at runtime.
 *
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @version $Revision$
 */
public class ImplementationHidingComponentAdapter extends DecoratingComponentAdapter {
    private final InterfaceFinder interfaceFinder = new InterfaceFinder();

    private MutablePicoContainer picoContainer;
    private Map handlers = new HashMap();

    public ImplementationHidingComponentAdapter(ComponentAdapter delegate) {
        super(delegate);
    }

    public Object getComponentInstance(MutablePicoContainer container)
            throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {

        this.picoContainer = container;
        Class[] interfaces = interfaceFinder.getInterfaces(getDelegate().getComponentImplementation());
        if (interfaces.length == 0) {
            throw new PicoIntrospectionException() {
                public String getMessage() {
                    return "Can't hide implementation for " + getDelegate().getComponentImplementation().getName() + ". It doesn't implement any interfaces.";
                }
            };
        }
        final DelegatingInvocationHandler delegatingInvocationHandler = new DelegatingInvocationHandler(this);
        Object proxyInstance = Proxy.newProxyInstance(getComponentImplementation().getClassLoader(),
                interfaces, delegatingInvocationHandler);
        handlers.put(proxyInstance, delegatingInvocationHandler);
        return proxyInstance;
    }

    private Object getDelegatedComponentInstance() {
        return super.getComponentInstance(picoContainer);
    }

    /**
     * Swaps the subject behind a proxy with a new instance.
     * @param proxy the proxy to swap for.
     * @param newSubject the new subject the proxy will delegate to.
     * @return the old suject
     */
    public Object hotSwap(Object proxy, Object newSubject) {
        DelegatingInvocationHandler handler = (DelegatingInvocationHandler) handlers.get(proxy);
        if(handler == null) {
            throw new IllegalArgumentException("Unknown proxy: " + proxy.toString());
        }
        return handler.hotSwap(newSubject);
    }

    // TODO: We need to handle methods from Object (equals, hashcode...).
    // See DefaultComponentMulticasterFactory
    private class DelegatingInvocationHandler implements InvocationHandler {
        private final ImplementationHidingComponentAdapter adapter;
        private Object delegatedInstance;

        public DelegatingInvocationHandler(ImplementationHidingComponentAdapter adapter) {
            this.adapter = adapter;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Class declaringClass = method.getDeclaringClass();
            if (declaringClass.equals(Object.class)) {
                if (method.equals(InterfaceFinder.hashCode)) {
                    // Return the hashCode of ourself, as Proxy.newProxyInstance() may
                    // return cached proxies. We want a unique hashCode for each created proxy!
                    return new Integer(System.identityHashCode(DelegatingInvocationHandler.this));
                }
                if (method.equals(InterfaceFinder.equals)) {
                    return new Boolean(proxy == args[0]);
                }
                // If it's any other method defined by Object, call on ourself.
                return method.invoke(DelegatingInvocationHandler.this, args);
            } else {
                if (delegatedInstance == null) {
                    delegatedInstance = adapter.getDelegatedComponentInstance();
                }
                return method.invoke(delegatedInstance, args);
            }
        }

        public Object hotSwap(Object newSubject) {
            Object result = delegatedInstance;
            delegatedInstance = newSubject;
            return result;
        }
    }
}
