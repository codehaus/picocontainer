/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Paul Hammant                      *
 *****************************************************************************/

package org.picocontainer.extras;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * A tiny helper for simple instantiation of components.
 * @author Paul Hammant
 */
public class ThrowawayPicoInstantiator {
    private final Object instance;

    public ThrowawayPicoInstantiator(MutablePicoContainer parentContainer, Class classToInstantiate) {
        DefaultPicoContainer dpc = new DefaultPicoContainer();
        dpc.addParent(parentContainer);
        dpc.registerComponentImplementation(classToInstantiate);
        instance = dpc.getComponentInstance(classToInstantiate);
    }

    public Object getInstance() {
        return instance;
    }
}
