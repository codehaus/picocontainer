/*****************************************************************************
 * Copyright (C) OldPicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.extras;

import org.picocontainer.PicoInstantiationException;
import org.picocontainer.PicoContainer;

import java.util.Collections;
import java.util.Collection;
import java.io.Serializable;

public class NullContainer implements PicoContainer, Serializable {
    public boolean hasComponent(Object compType) {
        return false;
    }

    public Object getComponentInstance(Object compType) {
        return null;
    }

    public Collection getComponentInstances() {
        return Collections.EMPTY_SET;
    }

    public Collection getComponentKeys() {
        return Collections.EMPTY_SET;
    }

    public void instantiateComponents() throws PicoInstantiationException {
    }

    public Object getComponentMulticaster() {
        return null;
    }

    public Object getComponentMulticaster(boolean callInInstantiationOrder, boolean callUnmanagedComponents) {
        return null;
    }
}

