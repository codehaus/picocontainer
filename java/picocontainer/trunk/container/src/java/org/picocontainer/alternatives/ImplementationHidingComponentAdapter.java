/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.alternatives;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.DecoratingComponentAdapter;
import org.picocontainer.defaults.DelegatingComponentMonitor;
import org.picocontainer.defaults.NotConcreteRegistrationException;

/**
 * This component adapter makes it possible to hide the implementation
 * of a real subject (behind a proxy) provided the key is an interface.
 * <p/>
 * This class exists here, because a) it has no deps on external jars, b) dynamic proxy is quite easy.
 * The user is prompted to look at nanocontainer-proxytoys for alternate and bigger implementations.
 *
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @version $Revision$
 * @see org.nanocontainer.proxytoys.HotSwappingComponentAdapter for a more feature-rich version of this class.
 * @see org.nanocontainer.proxytoys.HotSwappingComponentAdapterFactory
 * @since 1.1
 */
public class ImplementationHidingComponentAdapter extends DecoratingComponentAdapter {
    private final boolean strict;
    private ComponentMonitor componentMonitor;

    public ImplementationHidingComponentAdapter(ComponentAdapter delegate, boolean strict, 
                                                ComponentMonitor componentMonitor) {
        super(delegate);
        this.strict = strict;
        this.componentMonitor = componentMonitor;
    }
    
    public ImplementationHidingComponentAdapter(ComponentAdapter delegate, boolean strict) {
        this(delegate, strict, new DelegatingComponentMonitor());
    }

    public Object getComponentInstance(final PicoContainer container)
            throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {

        Object componentKey = getDelegate().getComponentKey();
        Class[] classes = null;
        if (componentKey instanceof Class && ((Class) getDelegate().getComponentKey()).isInterface()) {
            classes = new Class[]{(Class) getDelegate().getComponentKey()};
        } else if (componentKey instanceof Class[]) {
            classes = (Class[]) componentKey;
        } else {
            if(strict) {
                throw new PicoIntrospectionException("In strict mode, " + getClass().getName() + " only allows components registered with interface keys (java.lang.Class or java.lang.Class[])");
            }
            return getDelegate().getComponentInstance(container);
        }

        Class[] interfaces = verifyInterfacesOnly(classes);
        return createProxy(interfaces, container, getDelegate().getComponentImplementation().getClassLoader());
    }

    private Object createProxy(Class[] interfaces, final PicoContainer container, final ClassLoader classLoader) {
        return Proxy.newProxyInstance(classLoader,
                interfaces, new InvocationHandler() {
                    public Object invoke(final Object proxy, final Method method,
                                         final Object[] args)
                            throws Throwable {
                        Object componentInstance = getDelegate().getComponentInstance(container);
                        try {
                            componentMonitor.invoking(method, componentInstance);
                            long startTime = System.currentTimeMillis();
                            Object object = method.invoke(componentInstance, args);
                            componentMonitor.invoked(method, componentInstance, System.currentTimeMillis() - startTime);
                            return object;
                        } catch (final InvocationTargetException ite) {
                            componentMonitor.invocationFailed(method, componentInstance, ite);
                            throw ite.getTargetException();
                        }
                    }
                });
    }

    private Class[] verifyInterfacesOnly(Class[] classes) {
        for (int i = 0; i < classes.length; i++) {
            if(!classes[i].isInterface()) {
                throw new PicoIntrospectionException("Class keys must be interfaces. " + classes[i] + " is not an interface.");
            }
        }
        return classes;
    }

    // These two methods are copied from ProxyToys' ClassHierarchyIntrospector
    // TODO: Why? These two are currently not called in the complete Pico/Nano/Micro codebase ...
    // they just decrease coverage significantly ...
    /* *
     * Get all interfaces of the given type.
     * If the type is a class, the returned list contains any interface, that is
     * implemented by the class. If the type is an interface, the all
     * superinterfaces and the interface itself are included.
     *
     * @param clazz type to explore.
     * @return an array with all interfaces. The array may be empty.
     */
    /*
    public static Class[] getAllInterfaces(Class clazz) {
        Set interfaces = new HashSet();
        getInterfaces(clazz, interfaces);
        return (Class[]) interfaces.toArray(new Class[interfaces.size()]);
    }

    private static void getInterfaces(Class clazz, Set interfaces) {
        if (clazz.isInterface()) {
            interfaces.add(clazz);
        }
        // Class.getInterfaces will return only the interfaces that are
        // implemented by the current class. Therefore we must loop up
        // the hierarchy for the superclasses and the superinterfaces.
        while (clazz != null) {
            Class[] implemented = clazz.getInterfaces();
            for (int i = 0; i < implemented.length; i++) {
                if (!interfaces.contains(implemented[i])) {
                    getInterfaces(implemented[i], interfaces);
                }
            }
            clazz = clazz.getSuperclass();
        }
    }
    */
}
