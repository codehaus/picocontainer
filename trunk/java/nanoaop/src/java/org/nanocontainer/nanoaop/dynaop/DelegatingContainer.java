/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.nanoaop.dynaop;

import java.util.Collection;
import java.util.List;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.PicoVerificationException;
import org.picocontainer.PicoException;
import org.picocontainer.PicoVisitor;

/**
 * @author Stephen Molitor
 */
public class DelegatingContainer implements MutablePicoContainer {

    private final MutablePicoContainer delegate;

    public DelegatingContainer(MutablePicoContainer delegate) {
        this.delegate = delegate;
    }

    public void addChildContainer(PicoContainer child) {
        delegate.addChildContainer(child);
    }

    public void addChildContainer(String name, PicoContainer child) {
        delegate.addChildContainer(name, child);
    }

    public void addOrderedComponentAdapter(ComponentAdapter componentAdapter) {
        delegate.addOrderedComponentAdapter(componentAdapter);
    }

    public void dispose() {
        delegate.dispose();
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

    public MutablePicoContainer makeChildContainer() {
        return delegate.makeChildContainer();
    }

    public MutablePicoContainer makeChildContainer(String name) {
        return delegate.makeChildContainer(name);
    }

    public ComponentAdapter registerComponent(ComponentAdapter componentAdapter) throws PicoRegistrationException {
        return delegate.registerComponent(componentAdapter);
    }

    public ComponentAdapter registerComponentImplementation(Class componentImplementation)
            throws PicoRegistrationException {
        return delegate.registerComponentImplementation(componentImplementation);
    }

    public ComponentAdapter registerComponentImplementation(Object componentKey, Class componentImplementation)
            throws PicoRegistrationException {
        return delegate.registerComponentImplementation(componentKey, componentImplementation);
    }

    public ComponentAdapter registerComponentImplementation(Object componentKey, Class componentImplementation,
            Parameter[] parameters) throws PicoRegistrationException {
        return delegate.registerComponentImplementation(componentKey, componentImplementation, parameters);
    }

    public ComponentAdapter registerComponentInstance(Object componentInstance) throws PicoRegistrationException {
        return delegate.registerComponentInstance(componentInstance);
    }

    public ComponentAdapter registerComponentInstance(Object componentKey, Object componentInstance)
            throws PicoRegistrationException {
        return delegate.registerComponentInstance(componentKey, componentInstance);
    }

    public void removeChildContainer(PicoContainer child) {
        delegate.removeChildContainer(child);
    }

    public void start() {
        delegate.start();
    }

    public void stop() {
        delegate.stop();
    }

    public ComponentAdapter unregisterComponent(Object componentKey) {
        return delegate.unregisterComponent(componentKey);
    }

    public ComponentAdapter unregisterComponentByInstance(Object componentInstance) {
        return delegate.unregisterComponentByInstance(componentInstance);
    }

    public void verify() throws PicoVerificationException {
        delegate.verify();
    }
    
    public void accept(PicoVisitor visitor, Class componentType, boolean visitInInstantiationOrder) {
        delegate.accept(visitor, componentType, visitInInstantiationOrder);
    }

    public List getComponentInstancesOfType(Class type) throws PicoException {
        return delegate.getComponentInstancesOfType(type);
    }

}