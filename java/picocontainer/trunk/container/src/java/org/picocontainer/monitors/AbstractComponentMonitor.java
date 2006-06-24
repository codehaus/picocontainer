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

import java.text.MessageFormat;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.picocontainer.ComponentMonitor;

/**
 * An abstract {@link ComponentMonitor} which supports all the message formats.
 * 
 * @author Mauro Talevi
 * @version $Revision: $
 */
public abstract class AbstractComponentMonitor implements ComponentMonitor {

    public final static String INSTANTIATING = "PicoContainer: instantiating {0}";
    public final static String INSTANTIATED = "PicoContainer: instantiated {0} [{1} ms]";
    public final static String INSTANTIATED2 = "PicoContainer: instantiated {0} [{1} ms], component {2}, injected [{3}]";
    public final static String INSTANTIATION_FAILED = "PicoContainer: instantiation failed: {0}, reason: {1}";
    public final static String INVOKING = "PicoContainer: invoking {0} on {1}";
    public final static String INVOKED = "PicoContainer: invoked {0} on {1} [{2} ms]";
    public final static String INVOCATION_FAILED = "PicoContainer: invocation failed: {0} on {1}, reason: {2}";
    public final static String LIFECYCLE_INVOCATION_FAILED = "PicoContainer: lifecycle invocation failed: {0} on {1}, reason: {2}";

    public static String format(String template, Object[] arguments) {
        return MessageFormat.format(template, arguments);
    }

    public static String toString(Object[] injected) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < injected.length; i++) {
            String s = injected[i].getClass().getName();
            sb.append(s);
            if (i < injected.length-1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public static String toString(Constructor constructor) {
        Class[] params = constructor.getParameterTypes();
        StringBuffer sb = new StringBuffer(constructor.getName());
        sb.append("(");
        for (int i = 0; i < params.length; i++) {
            String s = params[i].getName();
            sb.append(s);
            if (i < params.length-1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    public static String toString(Method method) {
        Class[] params = method.getParameterTypes();
        StringBuffer sb = new StringBuffer(method.getName());
        sb.append("(");
        for (int i = 0; i < params.length; i++) {
            String s = params[i].getName();
            sb.append(s);
            if (i < params.length-1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }


}
