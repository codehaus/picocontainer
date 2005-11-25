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

import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.picocontainer.ComponentMonitor;

/**
 * A {@link ComponentMonitor} which writes to a {@link OutputStream}. 
 * This is typically used to write to a console.
 * 
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Mauro Talevi
 * @version $Revision$
 */
public class ConsoleComponentMonitor extends AbstractLoggingComponentMonitor {

    private PrintStream out;
    private final ComponentMonitor delegate;

    public ConsoleComponentMonitor(OutputStream out) {
        this.out = new PrintStream(out);
        delegate = new DefaultComponentMonitor();
    }

    public ConsoleComponentMonitor(OutputStream out, ComponentMonitor delegate) {
        this.out = new PrintStream(out);
        this.delegate = new DefaultComponentMonitor();
    }

    public void instantiating(Constructor constructor) {
        out.println(format(INSTANTIATING, new Object[]{constructor}));
        delegate.instantiating(constructor);
    }

    public void instantiated(Constructor constructor, long duration) {
        out.println(format(INSTANTIATED, new Object[]{constructor, new Long(duration)}));
        delegate.instantiated(constructor, duration);
    }

    public void instantiationFailed(Constructor constructor, Exception cause) {
        out.println(format(INSTANTIATION_FAILED, new Object[]{constructor, cause.getMessage()}));
        delegate.instantiationFailed(constructor, cause);
    }

    public void invoking(Method method, Object instance) {
        out.println(format(INVOKING, new Object[]{method, instance}));
        delegate.invoking(method, instance);
    }

    public void invoked(Method method, Object instance, long duration) {
        out.println(format(INVOKED, new Object[]{method, instance, new Long(duration)}));
        delegate.invoked(method, instance, duration);
    }

    public void invocationFailed(Method method, Object instance, Exception cause) {
        out.println(format(INVOCATION_FAILED, new Object[]{method, instance, cause.getMessage()}));
        delegate.invocationFailed(method, instance, cause);
    }

    public void lifecycleFailure(Method method, Object instance, RuntimeException cause) {
        out.println(format(LIFECYCLE_FAILURE, new Object[]{method, instance, cause.getMessage()}));
        delegate.lifecycleFailure(method, instance, cause);
    }

}
