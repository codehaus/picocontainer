/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.gems;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import org.picocontainer.Disposable;
import org.picocontainer.Startable;
import org.picocontainer.defaults.LifecycleStrategy;

/**
 * Reflection lifecycle strategy.  Starts, stops, disposes of component if appropriate methods are present
 * 
 * @author Paul Hammant
 * @see org.picocontainer.Startable
 * @see org.picocontainer.Disposable
 * @see org.picocontainer.defaults.DefaultLifecycleStrategy
 */
public class ReflectionLifecycleStrategy implements LifecycleStrategy, Serializable {

    public void start(Object component) {
        invokeMethod(component, "start");
    }

    public void stop(Object component) {
        invokeMethod(component, "stop");
    }

    public void dispose(Object component) {
        invokeMethod(component, "dispose");
    }

    private void invokeMethod(Object component, String methodName) {
        if ( component != null){
            try {
                Method start = component.getClass().getMethod(methodName, new Class[0]);
                start.invoke(component, new Object[0]);
            } catch (NoSuchMethodException e) {
                // not startable.
            } catch (IllegalAccessException e) {
                throw new ReflectionLifecycleException(methodName, e);
            } catch (InvocationTargetException e) {
                throw new ReflectionLifecycleException(methodName, e);
            }
        }
    }


}
