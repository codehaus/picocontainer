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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public class ConsoleComponentMonitor implements ComponentMonitor {

    ///CLOVER:OFF

    public void instantiating(Constructor constructor) {
        System.out.println("ComonentMonitor: instantiating constructor: " + constructor.toString());
    }

    public void instantiated(Constructor constructor, long beforeTime, long duration) {
        System.out.println("ComonentMonitor: instantiated constructor: " + constructor.toString() + "[ " + duration + "millis]");
    }

    public void instantiationFailed(Constructor constructor, Exception e) {
        System.out.println("ComonentMonitor: instantiationFailed constructor: " + constructor.toString() + ", reason '" + e.getMessage() + "'");
    }

    public void invoking(Method method, Object instance) {
        System.out.println("ComonentMonitor: invoking constructor: " + method.toString());
    }

    public void invoked(Method method, Object instance, long duration) {
        System.out.println("ComonentMonitor: invoking constructor: " + method.toString() + "[instance " + System.identityHashCode(instance) + "]");
    }

    public void invocationFailed(Method method, Object instance, Exception e) {
        System.out.println("ComonentMonitor: invoking constructor: " + method.toString() + "instance " + System.identityHashCode(instance) + "], reason '" + e.getMessage() + "'");
    }

    ///CLOVER:ON

}
