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
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoException;
import org.picocontainer.PicoVerificationException;
import org.picocontainer.PicoVisitor;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author Paul Hammant
 * @version $Revision$
 * @since 1.1
 */
public class ImmutablePicoContainer implements PicoContainer, Serializable {

    private PicoContainer delegate;
    private PicoContainer parent;

    public ImmutablePicoContainer(PicoContainer pc) {
        this.delegate = pc;
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
        if (parent == null) {
            PicoContainer par = delegate.getParent();
            if (par instanceof ImmutablePicoContainer) {
                parent = par;
            } else {
                parent = new ImmutablePicoContainer((MutablePicoContainer) par);
            }
        }
        return parent;
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
        throw new UnsupportedOperationException("This container is immutable, start() is not allowed");
    }

    public void stop() {
        throw new UnsupportedOperationException("This container is immutable, stop() is not allowed");
    }

    public void dispose() {
        throw new UnsupportedOperationException("This container is immutable, dispose() is not allowed");
    }

    public int hashCode() {
        return delegate.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof ImmutablePicoContainer) {
            return obj.hashCode() == delegate.hashCode();
        }
        return false;
    }
}
