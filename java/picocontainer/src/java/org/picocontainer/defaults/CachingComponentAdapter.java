/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.defaults;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;

import java.io.Serializable;

/**
 * This ComponentAdapter caches the instance.
 */
public class CachingComponentAdapter implements ComponentAdapter, Serializable {

    private Object componentInstance;
    private ComponentAdapter delegate;

    public CachingComponentAdapter(ComponentAdapter delegate) {
        this.delegate = delegate;
    }

    public Object getComponentKey() {
        return delegate.getComponentKey();
    }

    public Class getComponentImplementation() {
        return delegate.getComponentImplementation();
    }

    public Object getComponentInstance(MutablePicoContainer picoContainer)
            throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        if (componentInstance == null) {
            componentInstance = delegate.getComponentInstance(picoContainer);
            picoContainer.addOrderedComponentAdapter(this);
        }
        return componentInstance;
    }

    public void verify(PicoContainer picoContainer) throws NoSatisfiableConstructorsException {
        delegate.verify(picoContainer);
    }

}
