/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.alternatives;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.PicoVerificationException;
import org.picocontainer.PicoException;
import org.picocontainer.PicoVisitor;
import org.picocontainer.defaults.CachingComponentAdapter;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * This special MutablePicoContainer hides implementations of components if the key is an interface.
 * It's very simple. Instances that are registered directly and components registered without key
 * are not hidden.
 *
 * @author Paul Hammant
 * @version $Revision$
 * @since 1.1
 */
public class ImplementationHidingPicoContainer implements MutablePicoContainer, Serializable {

    private final InnerMutablePicoContainer delegate;
    private final ComponentAdapterFactory caf;

    /**
     * Creates a new container with a parent container.
     */
    public ImplementationHidingPicoContainer(ComponentAdapterFactory caf, PicoContainer parent) {
        this.caf = caf;
        delegate = new InnerMutablePicoContainer(caf, parent);
    }

    /**
     * Creates a new container with a parent container.
     */
    public ImplementationHidingPicoContainer(PicoContainer parent) {
        this(new DefaultComponentAdapterFactory(), parent);
    }


    /**
     * Creates a new container with no parent container.
     */
    public ImplementationHidingPicoContainer() {
        this(null);
    }


    public ComponentAdapter registerComponentImplementation(Object componentKey, Class componentImplementation) throws PicoRegistrationException {
        if (componentKey instanceof Class) {
            Class clazz = (Class) componentKey;
            if (clazz.isInterface()) {
                ComponentAdapter delegate = caf.createComponentAdapter(componentKey, componentImplementation, new Parameter[0]);
                return this.delegate.registerComponent(new CachingComponentAdapter(new ImplementationHidingComponentAdapter(delegate)));
            }
        }
        return delegate.registerComponentImplementation(componentKey, componentImplementation);
    }

    public ComponentAdapter registerComponentImplementation(Object componentKey, Class componentImplementation, Parameter[] parameters) throws PicoRegistrationException {
        if (componentKey instanceof Class) {
            Class clazz = (Class) componentKey;
            if (clazz.isInterface()) {
                ComponentAdapter delegate = caf.createComponentAdapter(componentKey, componentImplementation, parameters);
                ImplementationHidingComponentAdapter ihDelegate = new ImplementationHidingComponentAdapter(delegate);
                return this.delegate.registerComponent(new CachingComponentAdapter(ihDelegate));
            }
        }
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

    public void verify() throws PicoVerificationException {
        delegate.verify();
    }

    public void addOrderedComponentAdapter(ComponentAdapter componentAdapter) {
        delegate.addOrderedComponentAdapter(componentAdapter);
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

    public MutablePicoContainer makeChildContainer() {
        ImplementationHidingPicoContainer pc = new ImplementationHidingPicoContainer(this);
        delegate.addChildContainer(pc);
        return pc;

    }

    public MutablePicoContainer makeChildContainer(String name) {
        ImplementationHidingPicoContainer pc = new ImplementationHidingPicoContainer(this);
        delegate.addChildContainer(name, pc);
        return pc;
    }

    public void addChildContainer(PicoContainer child) {
        delegate.addChildContainer(child);
    }

    public void addChildContainer(String name, PicoContainer child) {
        delegate.addChildContainer(name, child);
    }

    public void removeChildContainer(PicoContainer child) {
        delegate.removeChildContainer(child);
    }

    public void accept(PicoVisitor visitor, Class componentType, boolean visitInInstantiationOrder) {
        delegate.accept(visitor, componentType, visitInInstantiationOrder);
    }

    public List getComponentInstancesOfType(Class type) throws PicoException {
        return delegate.getComponentInstancesOfType(type);
    }

    protected Map getNamedContainers() {
        return delegate.getNamedContainers();    
    }

    // TODO: Paul this is a vanilla class now ... remove it and use DPC directly ?
    private class InnerMutablePicoContainer extends DefaultPicoContainer {
        public InnerMutablePicoContainer(ComponentAdapterFactory componentAdapterFactory, PicoContainer parent) {
            super(componentAdapterFactory, parent);
        }

        public Map getNamedContainers() {
            return super.getNamedContainers();
        }
    }
}
