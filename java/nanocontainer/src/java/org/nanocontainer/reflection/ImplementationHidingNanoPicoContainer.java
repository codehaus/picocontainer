/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant                                             *
 *****************************************************************************/

package org.nanocontainer.reflection;

import org.nanocontainer.DefaultNanoContainer;
import org.nanocontainer.NanoContainer;
import org.nanocontainer.NanoPicoContainer;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.PicoVerificationException;
import org.picocontainer.PicoVisitor;
import org.picocontainer.alternatives.ImplementationHidingPicoContainer;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;

import java.io.Serializable;
import java.net.URL;
import java.util.Collection;
import java.util.List;

/**
 * This is a MutablePicoContainer that supports soft composition and hides implementations where it can.
 * <p/>
 * In terms of implementation it adopts the behaviour of ImplementationHidingPicoContainer
 * and DefaulReflectionContainerAdapter
 *
 * @author Paul Hammant
 * @version $Revision$
 * @deprecated Use {@link org.nanocontainer.DefaultNanoContainer)
 *             constructed with a {@link org.picocontainer.alternatives.ImplementationHidingPicoContainer}.
 */
public class ImplementationHidingNanoPicoContainer extends AbstractNanoPicoContainer implements NanoPicoContainer, Serializable {

    private final MutablePicoContainer delegate;

    // Serializable cannot be cascaded into DefaultNanoContainer's referenced classes
    // need to implement custom Externalisable regime.
    private transient NanoContainer container;

    public ImplementationHidingNanoPicoContainer(ClassLoader classLoader, ComponentAdapterFactory caf, PicoContainer parent) {
        delegate = new ImplementationHidingPicoContainer(caf, parent);

        container = new DefaultNanoContainer(classLoader, delegate);
    }

    public ImplementationHidingNanoPicoContainer(ClassLoader classLoader, PicoContainer parent) {
        delegate = new ImplementationHidingPicoContainer(new DefaultComponentAdapterFactory(), parent);

        container = new DefaultNanoContainer(classLoader, delegate);
    }

    public ImplementationHidingNanoPicoContainer(PicoContainer pc) {
        this(ImplementationHidingNanoPicoContainer.class.getClassLoader(), pc);
    }

    public ImplementationHidingNanoPicoContainer(ClassLoader classLoader) {
        this(classLoader, null);
    }

    public ImplementationHidingNanoPicoContainer() {
        this(ImplementationHidingNanoPicoContainer.class.getClassLoader(), null);
    }

    public Object getComponentInstanceFromDelegate(Object componentKey) {
        return delegate.getComponentInstance(componentKey);
    }

    public Object getComponentInstanceOfType(Class componentType) {
        return delegate.getComponentInstanceOfType(componentType);
    }

    public Object getComponentInstanceOfType(String componentType) {
        return container.getComponentInstanceOfType(componentType);
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

    public void start() {
        delegate.start();
    }

    public void stop() {
        delegate.stop();
    }

    public void dispose() {
        delegate.dispose();
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

    public MutablePicoContainer makeChildContainer(String name) {
        ClassLoader currentClassloader = container.getComponentClassLoader();
        ImplementationHidingNanoPicoContainer child = new ImplementationHidingNanoPicoContainer(currentClassloader, this);
        delegate.addChildContainer(child);
        namedChildContainers.put(name, child);
        return child;
    }

    public void addChildContainer(PicoContainer child) {
        delegate.addChildContainer(child);
        namedChildContainers.put("containers" + namedChildContainers.size(), child);
    }

    public void addChildContainer(String name, PicoContainer child) {
        delegate.addChildContainer(child);
        namedChildContainers.put(name, child);
    }

    public void removeChildContainer(PicoContainer child) {
        delegate.removeChildContainer(child);
        super.removeChildContainer(child);
    }

    public void accept(PicoVisitor visitor) {
        delegate.accept(visitor);
    }

    public List getComponentInstancesOfType(Class type) throws PicoException {
        return delegate.getComponentInstancesOfType(type);
    }

    // --------------------

    public ComponentAdapter registerComponentImplementation(String componentImplementationClassName) throws PicoRegistrationException, ClassNotFoundException, PicoIntrospectionException {
        return container.registerComponentImplementation(componentImplementationClassName);
    }

    public ComponentAdapter registerComponentImplementation(Object key, String componentImplementationClassName) throws ClassNotFoundException {
        return container.registerComponentImplementation(key, componentImplementationClassName);
    }

    public ComponentAdapter registerComponentImplementation(Object key, String componentImplementationClassName, Parameter[] parameters) throws ClassNotFoundException {
        return container.registerComponentImplementation(key, componentImplementationClassName, parameters);
    }

    public ComponentAdapter registerComponentImplementation(Object key, String componentImplementationClassName, String[] parameterTypesAsString, String[] parameterValuesAsString) throws PicoRegistrationException, ClassNotFoundException, PicoIntrospectionException {
        return container.registerComponentImplementation(key, componentImplementationClassName, parameterTypesAsString, parameterValuesAsString);
    }

    public ComponentAdapter registerComponentImplementation(String componentImplementationClassName, String[] parameterTypesAsString, String[] parameterValuesAsString) throws PicoRegistrationException, ClassNotFoundException, PicoIntrospectionException {
        return container.registerComponentImplementation(componentImplementationClassName, parameterTypesAsString, parameterValuesAsString);
    }

    public void addClassLoaderURL(URL url) {
        container.addClassLoaderURL(url);
    }

    //TODO Should this method be the NanoContainer interface only?
    public MutablePicoContainer getPico() {
        return this;
    }

    public ClassLoader getComponentClassLoader() {
        return container.getComponentClassLoader();
    }

    public boolean equals(Object obj) {
        return delegate.equals(obj);
    }

}
