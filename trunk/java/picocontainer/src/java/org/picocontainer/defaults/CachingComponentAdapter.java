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
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.extras.DecoratingComponentAdapter;

/**
 * This ComponentAdapter caches the instance.
 */
public class CachingComponentAdapter extends DecoratingComponentAdapter {

    private Object componentInstance;

    public CachingComponentAdapter(ComponentAdapter delegate) {
        super(delegate);
    }

    public Object getComponentInstance(MutablePicoContainer picoContainer)
            throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        if (componentInstance == null) {
            componentInstance = super.getComponentInstance(picoContainer);
            picoContainer.addOrderedComponentAdapter(this);
        }
        return componentInstance;
    }
}
