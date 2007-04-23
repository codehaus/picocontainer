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
import org.picocontainer.defaults.VerifyingVisitor;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

// TODO: replace this with a proxy? It don't do nothing! (AH)
// Am open to elegant solution. This, at least, is instantiable (PH)

/**
 * @author Paul Hammant
 * @version $Revision$
 * @since 1.1
 * @deprecated since 1.2, use the {@link org.picocontainer.defaults.ImmutablePicoContainerProxyFactory}
 */
public class ImmutablePicoContainer implements PicoContainer, Serializable {

    private PicoContainer delegate;

    public ImmutablePicoContainer(PicoContainer delegate) {
        if(delegate == null) throw new NullPointerException("You must pass in a picoContainer instance");
        this.delegate = delegate;
    }

    public Object getComponent(Object componentKey) {
        return delegate.getComponent(componentKey);
    }

    public Object getComponent(Class componentType) {
        return delegate.getComponent(componentType);
    }

    public List getComponents() {
        return delegate.getComponents();
    }

    public synchronized PicoContainer getParent() {
        return delegate.getParent();
    }

    public ComponentAdapter getComponentAdapter(Object componentKey) {
        return delegate.getComponentAdapter(componentKey);
    }

    public ComponentAdapter getComponentAdapter(Class componentType) {
        return delegate.getComponentAdapter(componentType);
    }

    public Collection getComponentAdapters() {
        return delegate.getComponentAdapters();
    }

    public List getComponentAdapters(Class componentType) {
        return delegate.getComponentAdapters(componentType);
    }

    /**
     * @deprecated since 1.1 - Use "new VerifyingVisitor().traverse(this)"
     */
    public void verify() throws PicoVerificationException {
        new VerifyingVisitor().traverse(this);
    }

    public List getComponents(Class type) throws PicoException {
        return delegate.getComponents(type);
    }

    public void accept(PicoVisitor visitor) {
        delegate.accept(visitor);
    }

    public void start() {
        // This is false security. As long as components can be accessed with getComponent(), they can also be started. (AH).
        throw new UnsupportedOperationException("This container is immutable, start() is not allowed");
    }

    public void stop() {
        // This is false security. As long as components can be accessed with getComponent(), they can also be stopped. (AH).
        throw new UnsupportedOperationException("This container is immutable, stop() is not allowed");
    }

    public void dispose() {
        // This is false security. As long as components can be accessed with getComponent(), they can also be disposed. (AH).
        throw new UnsupportedOperationException("This container is immutable, dispose() is not allowed");
    }
}
