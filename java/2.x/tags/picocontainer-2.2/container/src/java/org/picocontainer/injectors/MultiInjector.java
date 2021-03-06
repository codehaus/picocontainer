/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.injectors;

import org.picocontainer.Parameter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.annotations.Inject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Set;

/** @author Paul Hammant */
public class MultiInjector extends CompositeInjector {

    private AnnotatedMethodInjector annotatedMethodInjector;
    private static final long serialVersionUID = 9010039547575057092L;

    public MultiInjector(Object componentKey,
                         Class componentImplementation,
                         Parameter[] parameters,
                         ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, String setterPrefix, boolean useNames) {
        super(componentKey, componentImplementation, parameters, componentMonitor, lifecycleStrategy, useNames,
                new ConstructorInjector(componentKey, componentImplementation, parameters, componentMonitor, lifecycleStrategy, useNames),
                new SetterInjector(componentKey, componentImplementation, parameters, componentMonitor, lifecycleStrategy, setterPrefix, useNames),
                new AnnotatedMethodInjector(componentKey, componentImplementation, parameters, componentMonitor, lifecycleStrategy, Inject.class, useNames),
                new AnnotatedFieldInjector(componentKey, componentImplementation, parameters, componentMonitor, lifecycleStrategy, Inject.class, useNames));

    }

    public String getDescriptor() {
        return "MultiInjector";
    }
}
