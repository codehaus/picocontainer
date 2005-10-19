/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.gems.lifecycle;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.defaults.AbstractMonitoringLifecylceStrategy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Reflection lifecycle strategy. Starts, stops, disposes of component if appropriate methods are
 * present. The component may implement only one of the three methods.
 * 
 * @author Paul Hammant
 * @author Mauro Talevi
 * @author J&ouml;rg Schaible
 * @see org.picocontainer.Startable
 * @see org.picocontainer.Disposable
 * @see org.picocontainer.defaults.DefaultLifecycleStrategy
 * @since 1.2
 */
public class ReflectionLifecycleStrategy extends AbstractMonitoringLifecylceStrategy {

    private final static int START = 0;
    private final static int STOP = 1;
    private final static int DISPOSE = 2;
    private String[] methodNames;
    private transient Method[] methods;

    /**
     * Construct a ReflectionLifecycleStrategy.
     * 
     * @param monitor the monitor to use
     * @throws NullPointerException if the monitor is <code>null</code>
     */
    public ReflectionLifecycleStrategy(ComponentMonitor monitor) {
        this(monitor, "start", "stop", "dispose");
    }

    /**
     * Construct a ReflectionLifecycleStrategy with individual method names. Note, that a lifecycle
     * method does not have any arguments.
     * 
     * @param monitor the monitor to use
     * @param startMethodName the name of the start method
     * @param stopMethodName the name of the start method
     * @param dosposeMethodName the name of the start method
     * @throws NullPointerException if the monitor is <code>null</code>
     */
    public ReflectionLifecycleStrategy(
            ComponentMonitor monitor, String startMethodName, String stopMethodName,
            String dosposeMethodName) {
        super(monitor);
        methodNames = new String[]{startMethodName, stopMethodName, dosposeMethodName};
    }

    public void start(Object component) {
        init(component.getClass());
        invokeMethod(component, methods[START]);
    }

    public void stop(Object component) {
        init(component.getClass());
        invokeMethod(component, methods[STOP]);
    }

    public void dispose(Object component) {
        init(component.getClass());
        invokeMethod(component, methods[DISPOSE]);
    }

    private void invokeMethod(Object component, Method method) {
        if (component != null && method != null) {
            try {
                long str = System.currentTimeMillis();
                currentMonitor().invoking(method, component);
                method.invoke(component, new Object[0]);
                currentMonitor().invoked(method, component, System.currentTimeMillis() - str);
            } catch (IllegalAccessException e) {
                throw new ReflectionLifecycleException(method.getName(), e);
            } catch (InvocationTargetException e) {
                throw new ReflectionLifecycleException(method.getName(), e);
            }
        }
    }

    /**
     * {@inheritDoc} The component has a lifecylce if at least one of the three methods is present.
     */
    public boolean hasLifecycle(Class type) {
        init(type);
        for (int i = 0; i < methods.length; i++) {
            if (methods[i] != null) {
                return true;
            }
        }
        return false;
    }

    private void init(Class type) {
        if (methods == null) {
            methods = new Method[methodNames.length];
            for (int i = 0; i < methods.length; i++) {
                try {
                    methods[i] = type.getMethod(methodNames[i], new Class[0]);
                } catch (NoSuchMethodException e) {
                    continue;
                }
            }
        }
    }
}
