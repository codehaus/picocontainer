/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by the committers                                           *
 *****************************************************************************/
package org.picocontainer.gems;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.PicoVerificationException;
import org.picocontainer.PicoVisitor;
import org.picocontainer.alternatives.ImplementationHidingPicoContainer;
import org.picocontainer.alternatives.ImplementationHidingComponentAdapter;
import org.picocontainer.defaults.CachingComponentAdapterFactory;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.defaults.CachingComponentAdapter;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * This special MutablePicoContainer hides implementations of components if the key is an interface.
 * It's very simple. Instances that are registered directly and components registered without key
 * are not hidden.
 *
 * @author Paul Hammant
 * @version $Revision$
 * @since 1.1
 */
public class ImplementationHidingCachingPicoContainer implements MutablePicoContainer, Serializable {

    private final MutablePicoContainer delegate;
    private final ComponentAdapterFactory caf;

    /**
     * Creates a new container with a parent container.
     */
    public ImplementationHidingCachingPicoContainer(ComponentAdapterFactory caf, PicoContainer parent) {
        delegate = new ImplementationHidingPicoContainer(new CachingComponentAdapterFactory(caf), parent);
        this.caf = caf;
    }

    /**
     * Creates a new container with a parent container.
     */
    public ImplementationHidingCachingPicoContainer(PicoContainer parent) {
        this(new DefaultComponentAdapterFactory(), parent);
    }


    /**
     * Creates a new container with no parent container.
     */
    public ImplementationHidingCachingPicoContainer() {
        this(null);
    }

    public ComponentAdapter registerComponentImplementation(Object componentKey, Class componentImplementation) throws PicoRegistrationException {
        if (componentKey instanceof Class) {
            Class clazz = (Class) componentKey;
            if (clazz.isInterface()) {
                ComponentAdapter delegate = caf.createComponentAdapter(componentKey, componentImplementation, null);
                return this.delegate.registerComponent(new CachingComponentAdapter(new ImplementationHidingComponentAdapter(delegate, true)));
            }
        }
        return delegate.registerComponentImplementation(componentKey, componentImplementation);
    }

    public ComponentAdapter registerComponentImplementation(Object componentKey, Class componentImplementation, Parameter[] parameters) throws PicoRegistrationException {
        if (componentKey instanceof Class) {
            Class clazz = (Class) componentKey;
            if (clazz.isInterface()) {
                ComponentAdapter delegate = caf.createComponentAdapter(componentKey, componentImplementation, parameters);
                ImplementationHidingComponentAdapter ihDelegate = new ImplementationHidingComponentAdapter(delegate, true);
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

    /**
     * @deprecated since 1.1 - Use accept(new VerifyingVisitor())
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

    public MutablePicoContainer makeChildContainer() {
        ImplementationHidingCachingPicoContainer pc = new ImplementationHidingCachingPicoContainer(this);
        delegate.addChildContainer(pc);
        return pc;

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
        return delegate.equals(obj);
    }

}
