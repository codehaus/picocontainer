/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                                        *
 *****************************************************************************/
package org.nanocontainer.pool2;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.ClassHierarchyIntrospector;

/**
 * <p>This Component Adapter maintains a pool of component instances that are 
 * automatically released from the garbage collector. Only components implementing 
 * an interface can be pooled with this adapter and, since the components are
 * proxied, only the methods of the implemented interfaces are accessible.</p>
 * <p>Each returned instance implements automatically {@link PooledInstance}, that
 * can be used to return an instance manually to the pool.</p> 
 * @author J&ouml;rg Schaible
 */
public class AutoReleasingPoolingComponentAdapter extends PoolingComponentAdapter {
    private static Method getInstanceToAutorelease;
    private static Method returnInstanceToPool;
    static {
        try {
            getInstanceToAutorelease = Autoreleasable.class.getMethod("getInstanceToAutorelease", null);
            returnInstanceToPool = PooledInstance.class.getMethod("returnInstanceToPool", null);
        } catch (NoSuchMethodException e) {
            throw new InternalError();
        }
    }
    
    private transient Class[] interfaces;

    /**
     * Construct an AutoReleasingPoolingComponentAdapter.
     * @param delegate The delegate adapter that provides the pooled instances.
     * @param maxPoolSize maximum pool size. A size of -1 results in an ever growing pool. 
     * Note that the garbage collector is called, if an instance is demanded and the pool is exhausted. 
     * @param waitMilliSeconds number of milliseconds to wait when {@link #getComponentInstance() }
     * is called. Three different values are possible here:
     * <ul>
     * <li>0 : Fail immediately when exhausted.</li>
     * <li>n : Wait for max n milliseconds until a component is available. This will fail on timeout.</li>
     * </ul>
     */
    public AutoReleasingPoolingComponentAdapter(ComponentAdapter delegate, int maxPoolSize, int waitMilliSeconds) {
        super(delegate, maxPoolSize, waitMilliSeconds);
        if (waitMilliSeconds < 0) {
            throw new IllegalArgumentException("Number of milliseconds cannot be negative");
        }
    }

    public AutoReleasingPoolingComponentAdapter(ComponentAdapter delegate, int maxPoolSize) {
        super(delegate, maxPoolSize);
    }

    public AutoReleasingPoolingComponentAdapter(ComponentAdapter delegate) {
        super(delegate);
    }
    
    /**
     * {@inheritDoc}
     * If all instances of the pool are marked as busy and the pool cannot grow further,
     * the method calls the garbage collector.
     * @see org.nanocontainer.pool2.PoolingComponentAdapter#getAvailable()
     */
    public int getAvailable() {
        if (super.getAvailable() == 0) {
            if (getBusy() == getMaxPoolSize()) {
                System.gc();
            }
            List freedInstances = new LinkedList();
            for(Iterator iter = busy.keySet().iterator(); iter.hasNext(); ) {
                Object key = iter.next();
                WeakReference ref = (WeakReference)busy.get(key);
                if (ref.get() == null) {
                    freedInstances.add(key);
                }
            }
            for (Iterator iter = freedInstances.iterator(); iter.hasNext();) {
                busy.remove(iter.next());
            }
            available.addAll(freedInstances);
        }
        return super.getAvailable();
    }
    
    protected Object markInstanceAsBusyAndReturnIt(Object componentInstance) {
        if (interfaces == null) {
            List interfaceSet = new LinkedList();
            interfaceSet.add(Autoreleasable.class);
            if(getDelegate().getComponentKey() instanceof Class && ((Class)getDelegate().getComponentKey()).isInterface()) {
                interfaceSet.add(getDelegate().getComponentKey());
            } else {
                interfaceSet.addAll(ClassHierarchyIntrospector.getAllInterfaces(getDelegate().getComponentImplementation()));
            }
            if (interfaceSet.size() == 0) {
                throw new PicoIntrospectionException("Can't realize auto releasing pool for " + getDelegate().getComponentImplementation().getName() + ". It doesn't implement any interfaces.");
            }
            interfaces = (Class[]) interfaceSet.toArray(new Class[interfaceSet.size()]);
        }
        final DelegatingInvocationHandler delegatingInvocationHandler = new DelegatingInvocationHandler(componentInstance);
        Object proxy = Proxy.newProxyInstance(getClass().getClassLoader(), interfaces, delegatingInvocationHandler);
        Object reference = new WeakReference(proxy);
        busy.put(componentInstance, reference);
        return proxy;
    }

    /**
     * {@inheritDoc}
     * @see org.nanocontainer.pool2.PoolingComponentAdapter#returnComponentInstance(java.lang.Object)
     */
    public synchronized void returnComponentInstance(Object componentInstance) {
        Object proxy = componentInstance; // Keep reference till end of function
        if (proxy instanceof Autoreleasable) {
            componentInstance = ((Autoreleasable)proxy).getInstanceToAutorelease();
        } else {
            throw new BadTypeException(Autoreleasable.class, proxy.getClass());
        }
        super.returnComponentInstance(componentInstance);
    }
    
    private static interface Autoreleasable extends PooledInstance {
        public Object getInstanceToAutorelease();
    }

    private class DelegatingInvocationHandler implements InvocationHandler {
        private final Object delegatedInstance;

        public DelegatingInvocationHandler(Object instance) {
            this.delegatedInstance = instance;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Class declaringClass = method.getDeclaringClass();
            if (declaringClass.equals(Object.class)) {
                if (method.equals(ClassHierarchyIntrospector.hashCode)) {
                    // Return the hashCode of ourself, as Proxy.newProxyInstance() may
                    // return cached proxies. We want a unique hashCode for each created proxy!
                    return new Integer(System.identityHashCode(this));
                }
                return method.invoke(delegatedInstance, args);
            } else if (getInstanceToAutorelease.equals(method)) {
                return delegatedInstance;
            } else if (returnInstanceToPool.equals(method)) {
                WeakReference reference = (WeakReference)busy.get(delegatedInstance);
                AutoReleasingPoolingComponentAdapter.this.returnComponentInstance(reference.get());
                return Void.TYPE;
            } else {
                return method.invoke(delegatedInstance, args);
            }
        }
    }
}
