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
import org.picocontainer.PicoVisitor;
import org.picocontainer.ComponentCharacteristic;

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

    public MutablePicoContainer addComponent(Object componentKey, Object componentImplementationOrInstance, Parameter... parameters) throws PicoRegistrationException {
        return delegate.addComponent(componentKey, componentImplementationOrInstance, parameters);
    }

    public MutablePicoContainer addComponent(Object implOrInstance) throws PicoRegistrationException {
        return delegate.addComponent(implOrInstance);
    }

    public MutablePicoContainer addAdapter(ComponentAdapter componentAdapter) throws PicoRegistrationException {
        return delegate.addAdapter(componentAdapter);
    }

    public ComponentAdapter removeComponent(Object componentKey) {
        return delegate.removeComponent(componentKey);
    }

    public ComponentAdapter removeComponentByInstance(Object componentInstance) {
        return delegate.removeComponentByInstance(componentInstance);
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

    public PicoContainer getParent() {
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
        return delegate.equals(obj) || this == obj;
    }

    public ComponentAdapter lastCA() {
        return null; 
    }

    public MutablePicoContainer change(ComponentCharacteristic... characteristics) {
        return delegate.change(characteristics); 
    }

    public MutablePicoContainer as(ComponentCharacteristic... characteristics) {
        return delegate.as(characteristics);
    }
}
