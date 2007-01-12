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

    public ComponentAdapter registerComponentImplementation(Object componentKey, Class componentImplementation) throws PicoRegistrationException {
        return delegate.registerComponentImplementation(componentKey, componentImplementation);
    }

    public ComponentAdapter registerComponentImplementation(Object componentKey, Class componentImplementation, Parameter[] parameters) throws PicoRegistrationException {
        return delegate.registerComponentImplementation(componentKey, componentImplementation, parameters);
    }


    public ComponentAdapter registerComponentImplementation(Class componentImplementation) throws PicoRegistrationException {
        return delegate.registerComponentImplementation(componentImplementation);
    }

    public ComponentAdapter registerComponentInstance(Object componentInstance) throws PicoRegistrationException {
        return delegate.registerComponentInstance(componentInstance);
    }

    public ComponentAdapter registerComponentInstance(Object componentKey, Object componentInstance) throws PicoRegistrationException {
        return delegate.registerComponentInstance(componentKey, componentInstance);
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

    public Object getComponentInstance(Object componentKey) {
        return delegate.getComponentInstance(componentKey);
    }

    public Object getComponentInstanceOfType(Class componentType) {
        return delegate.getComponentInstanceOfType(componentType);
    }

    public List getComponentInstances() {
        return delegate.getComponentInstances();
    }

    public PicoContainer getParent() {
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

    /**
     * @deprecated since 1.1 - Use new VerifyingVisitor().traverse(this)
     */
   public void verify() throws PicoVerificationException {
        delegate.verify();
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

    public List getComponentInstancesOfType(Class type) throws PicoException {
        return delegate.getComponentInstancesOfType(type);
    }

    public boolean equals(Object obj) {
        // required to make it pass on both jdk 1.3 and jdk 1.4. Btw, what about overriding hashCode()? (AH)
        final boolean result = delegate.equals(obj) || this == obj;
        return result;
    }

}
