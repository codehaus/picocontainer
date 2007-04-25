/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammaant                                            *
 *****************************************************************************/

package org.picocontainer.monitors;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.PicoLifecycleException;

import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.io.Serializable;

public class DefaultComponentMonitor implements ComponentMonitor, Serializable {

    private static DefaultComponentMonitor instance;

    public void instantiating(Constructor constructor) {
    }

    public void instantiated(Constructor constructor, Object instantiated, Object[] injected, long duration) {
    }

    public void instantiationFailed(Constructor constructor, Exception e) {
    }

    public void invoking(Method method, Object instance) {
    }

    public void invoked(Method method, Object instance, long duration) {
    }

    public void invocationFailed(Method method, Object instance, Exception e) {
    }

    public void lifecycleInvocationFailed(Method method, Object instance, RuntimeException cause) {
        throw new PicoLifecycleException(method, instance, cause);
    }

    public static synchronized DefaultComponentMonitor getInstance() {
        if (instance == null) {
            instance = new DefaultComponentMonitor();
        }
        return instance;
    }


}
