/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/


package org.picocontainer.defaults;

import org.picocontainer.PicoContainer;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoVerificationException;

import java.util.List;
import java.util.Collection;

public class NullPicoContainer implements PicoContainer {
    public Object getComponentInstance(Object componentKey) {
        return null;
    }

    public Object getComponentInstanceOfType(Class componentType) {
        return null;
    }

    public List getComponentInstances() {
        return null;
    }

    public PicoContainer getParent() {
        return null;
    }

    public ComponentAdapter getComponentAdapter(Object componentKey) {
        return null;
    }

    public ComponentAdapter getComponentAdapterOfType(Class componentType) {
        return null;
    }

    public Collection getComponentAdapters() {
        return null;
    }

    public void verify() throws PicoVerificationException {
    }

    public void addOrderedComponentAdapter(ComponentAdapter componentAdapter) {
    }

    public void start() {
    }

    public void stop() {
    }

    public void dispose() {
    }
}
