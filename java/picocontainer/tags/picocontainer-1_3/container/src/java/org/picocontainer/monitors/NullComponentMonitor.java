/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant & Obie Fernandez & Aslak Helles&oslash;y    *
 *****************************************************************************/

package org.picocontainer.monitors;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.picocontainer.ComponentMonitor;

/**
 * A {@link ComponentMonitor} which does nothing. 
 * 
 * @author Paul Hammant
 * @author Obie Fernandez
 * @version $Revision$
 */
public class NullComponentMonitor implements ComponentMonitor, Serializable {

    private static NullComponentMonitor instance;

    public void instantiating(Constructor constructor) {
    }

    public void instantiated(Constructor constructor, long duration) {
    }

    public void instantiationFailed(Constructor constructor, Exception e) {
    }

    public void instantiated(Constructor constructor, Object instantiated, Object[] injected, long duration) {
    }

    public void invoking(Method method, Object instance) {
    }

    public void invoked(Method method, Object instance, long duration) {
    }

    public void invocationFailed(Method method, Object instance, Exception e) {
    }

    public void lifecycleInvocationFailed(Method method, Object instance, RuntimeException cause) {
    }

    public static synchronized NullComponentMonitor getInstance() {
        if (instance == null) {
            instance = new NullComponentMonitor();
        }
        return instance;
    }
}
