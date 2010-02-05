/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.injectors;

import org.picocontainer.Parameter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationTargetException;

@SuppressWarnings("serial")
public class AnnotatedMethodInjector extends SetterInjector {

 	private final Class<? extends Annotation> injectionAnnotation;

    public AnnotatedMethodInjector(Object key,
                                   Class<?> impl,
                                   Parameter[] parameters,
                                   ComponentMonitor monitor,
                                   Class<? extends Annotation> injectionAnnotation, boolean useNames) {
        super(key, impl, parameters, monitor, "", useNames);
        this.injectionAnnotation = injectionAnnotation;
    }

    protected Object injectIntoMember(AccessibleObject member, Object componentInstance, Object toInject)
        throws IllegalAccessException, InvocationTargetException {
        return ((Method)member).invoke(componentInstance, toInject);
    }

    protected final boolean isInjectorMethod(Method method) {
        return method.getAnnotation(injectionAnnotation) != null;
    }

    public String toString() {
        return "MethodInjection-" + super.toString();
    }

}
