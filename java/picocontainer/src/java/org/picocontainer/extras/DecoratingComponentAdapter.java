/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Jon Tirsen                                               *
 *****************************************************************************/

package org.picocontainer.extras;

import org.picocontainer.*;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.NotConcreteRegistrationException;

import java.io.Serializable;

/**
 * @author Jon Tirsen (tirsen@codehaus.org)
 * @author Aslak Hellesoy
 * @version $Revision$
 */
public class DecoratingComponentAdapter implements ComponentAdapter, Serializable {

    private final ComponentAdapter delegate;

    public DecoratingComponentAdapter(ComponentAdapter delegate) {
        this.delegate = delegate;
    }

    public Object getComponentKey() {
        return delegate.getComponentKey();
    }

    public Class getComponentImplementation() {
        return delegate.getComponentImplementation();
    }

    public Object getComponentInstance(MutablePicoContainer picoContainer) throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        picoContainer.registerOrderedComponentAdapter(this);
        Object instance = delegate.getComponentInstance(picoContainer);
        picoContainer.addOrderedComponentAdapter(this);
        return instance;
    }

    public void verify(PicoContainer picoContainer) {
        delegate.verify(picoContainer);
    }

    public ComponentAdapter getDelegate() {
        return delegate;
    }
}
