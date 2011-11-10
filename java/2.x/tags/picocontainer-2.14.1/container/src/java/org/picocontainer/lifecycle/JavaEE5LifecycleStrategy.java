/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.lifecycle;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.PicoLifecycleException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * Java EE 5 has some annotations PreDestroy and PostConstruct that map to start() and dispose() in our world
 *
 * @author Paul Hammant
 */
@SuppressWarnings("serial")
public final class JavaEE5LifecycleStrategy extends AbstractMonitoringLifecycleStrategy {

    /**
     * Construct a JavaEE5LifecycleStrategy.
     *
     * @param monitor the monitor to use
     * @throws NullPointerException if the monitor is <code>null</code>
     */
    public JavaEE5LifecycleStrategy(final ComponentMonitor monitor) {
        super(monitor);
    }

    /** {@inheritDoc} **/
    public void start(final Object component) {
        doLifecycleMethod(component, PostConstruct.class, true);
    }

	/** {@inheritDoc} **/
    public void stop(final Object component) {
    }

    /** {@inheritDoc} **/
    public void dispose(final Object component) {
        doLifecycleMethod(component, PreDestroy.class, false);
    }

    private void doLifecycleMethod(final Object component, Class<? extends Annotation> annotation, boolean superFirst) {
        doLifecycleMethod(component, annotation, component.getClass(), superFirst, new HashSet<String>());
    }

    private void doLifecycleMethod(Object component, Class<? extends Annotation> annotation, Class<? extends Object> clazz, boolean superFirst, Set<String> doneAlready) {
        Class<?> parent = clazz.getSuperclass();
        if (superFirst && parent != Object.class) {
            doLifecycleMethod(component, annotation, parent, superFirst, doneAlready);
        }
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            String signature = signature(method);
            if (method.isAnnotationPresent(annotation) && !doneAlready.contains(signature)) {
                try {
                    long str = System.currentTimeMillis();
                    currentMonitor().invoking(null, null, method, component, new Object[0]);
                    method.invoke(component);
                    doneAlready.add(signature);
                    currentMonitor().invoked(null, null, method, component, System.currentTimeMillis() - str, new Object[0], null);
                } catch (IllegalAccessException e) {
                    throw new PicoLifecycleException(method, component, e);
                } catch (InvocationTargetException e) {
                    throw new PicoLifecycleException(method, component, e);
                }
            }
        }
        if (!superFirst && parent != Object.class) {
            doLifecycleMethod(component, annotation, parent, superFirst, doneAlready);
        }
    }

    private static String signature(Method method) {
        StringBuilder sb = new StringBuilder(method.getName());
        Class<?>[] pt = method.getParameterTypes();
        for (Class<?> objectClass : pt) {
            sb.append(objectClass.getName());
        }
        return sb.toString();
    }


    /**
     * {@inheritDoc} The component has a lifecycle PreDestroy or PostConstruct are on a method
     */
    public boolean hasLifecycle(final Class<?> type) {
        Method[] methods = type.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (method.isAnnotationPresent(PreDestroy.class) || method.isAnnotationPresent(PostConstruct.class)) {
                return true;
            }
        }
        return false;
    }

}