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

import org.picocontainer.defaults.ComponentMonitor;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class ConsoleComponentMonitor implements ComponentMonitor {
    private PrintWriter out;

    public ConsoleComponentMonitor(Writer out) {
        this.out = new PrintWriter(out);
    }

    public void instantiating(Constructor constructor) {
        out.println("PicoContainer: instantiating " + constructor.toString());
    }

    public void instantiated(Constructor constructor, long beforeTime, long duration) {
        out.println("PicoContainer: instantiated " + constructor.toString() + " [" + duration + "ms]");
    }

    public void instantiationFailed(Constructor constructor, Exception e) {
        out.println("PicoContainer: instantiation failed: " + constructor.toString() + ", reason: '" + e.getMessage() + "'");
    }

    public void invoking(Method method, Object instance) {
        out.println("PicoContainer: invoking " + method.toString() + " on " + instance);
    }

    public void invoked(Method method, Object instance, long duration) {
        out.println("PicoContainer: invoked " + method.toString() + " on " + instance + " [" + duration + "ms]");
    }

    public void invocationFailed(Method method, Object instance, Exception e) {
        out.println("PicoContainer: invocation failed: " + method.toString() + " on " + instance + ", reason: '" + e.getMessage() + "'");
    }
}
