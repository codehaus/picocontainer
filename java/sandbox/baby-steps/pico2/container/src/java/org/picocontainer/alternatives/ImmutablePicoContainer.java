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
import org.picocontainer.PicoVisitor;

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

    public Object getComponent(Object componentKeyOrType) {
        return delegate.getComponent(componentKeyOrType);
    }

    public <T> T getComponent(Class<T> componentType) {
        return (T) getComponent((Object) componentType);
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

    public Collection<ComponentAdapter> getComponentAdapters() {
        return delegate.getComponentAdapters();
    }

    public List<ComponentAdapter> getComponentAdapters(Class componentType) {
        return delegate.getComponentAdapters(componentType);
    }

    public List getComponents(Class type) throws PicoException {
        return delegate.getComponents(type);
    }

    public void accept(PicoVisitor visitor) {
        delegate.accept(visitor);
    }

}
