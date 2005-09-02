/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant                                             *
 *****************************************************************************/

package org.picocontainer.defaults;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleManager;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;


/**
 * A customizable LifeclycleManager. The implementation is based on three provided methods called on the components of
 * the container, if they implement the method's declaring class type. Lifecylce methods are expected to take no
 * arguments.
 * 
 * @author J&ouml;rg Schaible
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @since 1.2
 */
public class CustomLifecycleManager implements LifecycleManager, Serializable {

    private transient Method startMethod;
    private transient Method stopMethod;
    private transient Method disposeMethod;
    private final ComponentMonitor componentMonitor;

    /**
     * Construct a CustomLifecycleManager.
     * 
     * @param startMethod the method called when the component instances are started
     * @param stopMethod the method called when the component instances are stopped
     * @param disposeMethod the method called when the component instances are disposed
     * @param componentMonitor the {@link ComponentMonitor} receiving the invocation events
     * @throws NullPointerException if one of the arguments is null
     * @throws IllegalArgumentException if one of the methods has parameters
     * @since 1.2
     */
    public CustomLifecycleManager(
            Method startMethod, Method stopMethod, Method disposeMethod, 
            ComponentMonitor componentMonitor) {
        if (startMethod == null || stopMethod == null || disposeMethod == null || componentMonitor == null) {
            throw new NullPointerException();
        }
        if (startMethod.getParameterTypes().length
                + stopMethod.getParameterTypes().length
                + disposeMethod.getParameterTypes().length != 0) {
            throw new IllegalArgumentException("Not a lifecycle method");
        }
        this.startMethod = startMethod;
        this.stopMethod = stopMethod;
        this.disposeMethod = disposeMethod;
        this.componentMonitor = componentMonitor;
    }

    /**
     * {@inheritDoc} This implementation will collect all components implementing the declaring class type of the
     * "start" method and invoke this method on the single component instances.
     */
    public void start(PicoContainer node) {
        List startables = node.getComponentInstancesOfType(startMethod.getDeclaringClass());
        for (int i = 0; i < startables.size(); i++) {
            doMethod(startMethod, startables.get(i));
        }
    }

    /**
     * {@inheritDoc} This implementation will collect all components implementing the declaring class type of the "stop"
     * method and invoke this method on the single component instances.
     */
    public void stop(PicoContainer node) {
        List startables = node.getComponentInstancesOfType(stopMethod.getDeclaringClass());
        for (int i = startables.size() - 1; 0 <= i; i--) {
            doMethod(stopMethod, startables.get(i));
        }
    }

    /**
     * {@inheritDoc} This implementation will collect all components implementing the declaring class type of the
     * "dispose" method and invoke this method on the single component instances.
     */
    public void dispose(PicoContainer node) {
        List disposables = node.getComponentInstancesOfType(disposeMethod.getDeclaringClass());
        for (int i = disposables.size() - 1; 0 <= i; i--) {
            doMethod(disposeMethod, disposables.get(i));
        }
    }

    /**
     * Execute the method call. The implementation will fire the monitoring events.
     * 
     * @param method
     * @param instance
     */
    protected void doMethod(Method method, Object instance) {
        componentMonitor.invoking(method, instance);
        try {
            long beginTime = System.currentTimeMillis();
            method.invoke(instance, (Object[])null);
            componentMonitor.invoked(method, instance, System.currentTimeMillis() - beginTime);
        } catch (Exception e) {
            invocationFailed(method, instance, e);
        }
    }

    /**
     * Handle a failed invocation. The implementation will fire the monitoring events.
     * 
     * @param method the called method
     * @param instance the component instance
     * @param e the thrown exception
     */
    protected void invocationFailed(Method method, Object instance, Exception e) {
        componentMonitor.invocationFailed(method, instance, e);
        throw new PicoInitializationException("Method '"
                + method.getName()
                + "' failed on instance '"
                + instance
                + "' for reason '"
                + e.getMessage()
                + "'", e);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(startMethod.getDeclaringClass());
        out.writeUTF(startMethod.getName());
        out.writeObject(stopMethod.getDeclaringClass());
        out.writeUTF(stopMethod.getName());
        out.writeObject(disposeMethod.getDeclaringClass());
        out.writeUTF(disposeMethod.getName());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        try {
            Class type = (Class)in.readObject();
            startMethod = type.getMethod(in.readUTF(), (Class[])null);
            type = (Class)in.readObject();
            stopMethod = type.getMethod(in.readUTF(), (Class[])null);
            type = (Class)in.readObject();
            disposeMethod = type.getMethod(in.readUTF(), (Class[])null);
        } catch (NoSuchMethodException e) {
            throw new InvalidObjectException("Cannot find lifecylce method: " + e.getMessage());
        }
    }

}
