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

import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInstantiationException;

import java.util.Set;
import java.util.Collections;
import java.io.Serializable;

public class NullContainer implements PicoContainer, Serializable {
    public boolean hasComponent(Object compType) {
        return false;
    }

    public Object getComponent(Object compType) {
        return null;
    }

    public Set getComponents() {
        return Collections.EMPTY_SET;
    }

    public Set getComponentKeys() {
        return Collections.EMPTY_SET;
    }

    public void instantiateComponents() throws PicoInstantiationException {
    }

    public Object getCompositeComponent()
    {
        return null;
    }

    public Object getCompositeComponent(boolean callInInstantiationOrder, boolean callUnmanagedComponents)
    {
        return null;
    }
}

