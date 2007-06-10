/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.behaviors;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.ComponentCharacteristic;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.ComponentFactory;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.BehaviorFactory;
import org.picocontainer.PicoInitializationException;

import java.io.Serializable;

public class AbstractBehaviorFactory implements ComponentFactory, Serializable, BehaviorFactory {

    private ComponentFactory delegate;

    public ComponentFactory forThis(ComponentFactory delegate) {
        this.delegate = delegate;
        return this;
    }
    public ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, ComponentCharacteristic componentCharacteristic, Object componentKey,
                                                   Class componentImplementation,
                                                   Parameter... parameters) throws PicoIntrospectionException,
                                                                                   PicoInitializationException
    {
        return delegate.createComponentAdapter(componentMonitor, lifecycleStrategy, componentCharacteristic, componentKey, componentImplementation, parameters);
    }

}
