/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant                                             *
 *****************************************************************************/
package org.picocontainer.alternatives;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoException;
import org.picocontainer.PicoVerificationException;
import org.picocontainer.PicoVisitor;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

// TODO: replace this with a proxy? It don't do nothing! (AH)

/**
 * @author Paul Hammant
 * @version $Revision$
 * @since 1.1
 */
public class ImmutablePicoContainer implements PicoContainer, Serializable {

    private PicoContainer delegate;

    public ImmutablePicoContainer(PicoContainer delegate) {
        if(delegate == null) throw new NullPointerException();
        this.delegate = delegate;
    }

    public Object getComponentInstance(Object componentKey) {
        return delegate.getComponentInstance(componentKey);
    }

    public Object getComponentInstanceOfType(Class componentType) {
        return delegate.getComponentInstanceOfType(componentType);
    }

    public List getComponentInstances() {
        return delegate.getComponentInstances();
    }

    public synchronized PicoContainer getParent() {
        return delegate.getParent();
    }

    public ComponentAdapter getComponentAdapter(Object componentKey) {
        return delegate.getComponentAdapter(componentKey);
    }

    public ComponentAdapter getComponentAdapterOfType(Class componentType) {
        return delegate.getComponentAdapterOfType(componentType);
    }

    public Collection getComponentAdapters() {
        return delegate.getComponentAdapters();
    }

    public List getComponentAdaptersOfType(Class componentType) {
        return delegate.getComponentAdaptersOfType(componentType);
    }

    public void verify() throws PicoVerificationException {
        delegate.verify();
    }

    public List getComponentInstancesOfType(Class type) throws PicoException {
        return delegate.getComponentInstancesOfType(type);
    }

    public void accept(PicoVisitor visitor, Class componentType, boolean visitInInstantiationOrder) {
        visitor.visitContainer(this);
        delegate.accept(visitor, componentType, visitInInstantiationOrder);
    }

    public void start() {
        // This is false security. As long as components can be accessed with getComponentInstance(), they can also be started. (AH).
        throw new UnsupportedOperationException("This container is immutable, start() is not allowed");
    }

    public void stop() {
        // This is false security. As long as components can be accessed with getComponentInstance(), they can also be stopped. (AH).
        throw new UnsupportedOperationException("This container is immutable, stop() is not allowed");
    }

    public void dispose() {
        // This is false security. As long as components can be accessed with getComponentInstance(), they can also be disposed. (AH).
        throw new UnsupportedOperationException("This container is immutable, dispose() is not allowed");
    }
}
