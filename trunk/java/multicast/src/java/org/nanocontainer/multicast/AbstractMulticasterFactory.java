package org.nanocontainer.multicast;

import org.picocontainer.defaults.InterfaceFinder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public abstract class AbstractMulticasterFactory implements MulticasterFactory, Serializable {
    protected final InterfaceFinder interfaceFinder = new InterfaceFinder();

    public Object createComponentMulticaster(ClassLoader classLoader,
                                             Class type, List objectsToAggregateCallFor,
                                             boolean callInReverseOrder,
                                             InvocationInterceptor invocationInterceptor,
                                             Invoker invoker) {
        List copy = new ArrayList(objectsToAggregateCallFor);

        if (!callInReverseOrder) {
            // reverse the list
            Collections.reverse(copy);
        }
        Object[] targets = copy.toArray();

        return createProxy(classLoader, type, objectsToAggregateCallFor, targets, invocationInterceptor, invoker);
    }

    protected abstract Object createProxy(ClassLoader classLoader, Class type, List objectsToAggregateCallFor, Object[] targets, InvocationInterceptor invocationInterceptor, Invoker invoker);
}