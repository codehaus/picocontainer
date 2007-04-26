/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by the committers                                           *
 *****************************************************************************/
package org.picocontainer.alternatives;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.PicoVerificationException;
import org.picocontainer.PicoVisitor;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public abstract class AbstractDelegatingMutablePicoContainer implements MutablePicoContainer, Serializable {

    private MutablePicoContainer delegate;

    public AbstractDelegatingMutablePicoContainer(MutablePicoContainer delegate) {
        this.delegate = delegate;
    }

    protected MutablePicoContainer getDelegate() {
        return delegate;
    }

    public ComponentAdapter registerComponent(Object componentKey, Object componentImplementationOrInstance, Parameter... parameters) throws PicoRegistrationException {
        return delegate.registerComponent(componentKey, componentImplementationOrInstance, parameters);
    }

    public ComponentAdapter registerComponent(Class componentImplementation) throws PicoRegistrationException {
        return delegate.registerComponent(componentImplementation);
    }

    public ComponentAdapter registerComponent(Object componentInstance) throws PicoRegistrationException {
        return delegate.registerComponent(componentInstance);
    }

    public ComponentAdapter registerComponent(ComponentAdapter componentAdapter) throws PicoRegistrationException {
        return delegate.registerComponent(componentAdapter);
    }

    public ComponentAdapter unregisterComponent(Object componentKey) {
        return delegate.unregisterComponent(componentKey);
    }

    public ComponentAdapter unregisterComponentByInstance(Object componentInstance) {
        return delegate.unregisterComponentByInstance(componentInstance);
    }

    public Object getComponent(Object componentKeyOrType) {
        return delegate.getComponent(componentKeyOrType);
    }

    public List getComponents() {
        return delegate.getComponents();
    }

    public PicoContainer getParent() {
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

    public void start() {
        delegate.start();
    }

    public void stop() {
        delegate.stop();
    }

    public void dispose() {
        delegate.dispose();
    }

    public boolean addChildContainer(PicoContainer child) {
        return delegate.addChildContainer(child);
    }

    public boolean removeChildContainer(PicoContainer child) {
        return delegate.removeChildContainer(child);
    }

    public void accept(PicoVisitor visitor) {
        delegate.accept(visitor);
    }

    public List getComponents(Class type) throws PicoException {
        return delegate.getComponents(type);
    }

    public boolean equals(Object obj) {
        // required to make it pass on both jdk 1.3 and jdk 1.4. Btw, what about overriding hashCode()? (AH)
        final boolean result = delegate.equals(obj) || this == obj;
        return result;
    }

}
