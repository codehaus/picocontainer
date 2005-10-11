/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.gems.lifecycle;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.picocontainer.defaults.LifecycleStrategy;


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
 */
public class ReflectionLifecycleStrategy implements LifecycleStrategy, Serializable {

    private final static int START = 0;
    private final static int STOP = 1;
    private final static int DISPOSE = 2;
    private final static String[] METHOD_NAMES = new String[]{"start", "stop", "dispose"};
    private transient Method[] methods;

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
                method.invoke(component, new Object[0]);
            } catch (IllegalAccessException e) {
                throw new ReflectionLifecycleException(method.getName(), e);
            } catch (InvocationTargetException e) {
                throw new ReflectionLifecycleException(method.getName(), e);
            }
        }
    }

    /**
     * {@inheritDoc}
     * The component has a lifecylce if at least one of the three methods is present.
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
            methods = new Method[METHOD_NAMES.length];
            for (int i = 0; i < methods.length; i++) {
                try {
                    methods[i] = type.getMethod(METHOD_NAMES[i], new Class[0]);
                } catch (NoSuchMethodException e) {
                    continue;
                }
            }
        }
    }
}
