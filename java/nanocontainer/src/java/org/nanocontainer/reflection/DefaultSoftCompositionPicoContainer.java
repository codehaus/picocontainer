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

import org.nanocontainer.SoftCompositionPicoContainer;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.PicoVerificationException;
import org.picocontainer.PicoVisitor;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;

import java.io.Serializable;
import java.net.URL;
import java.util.Collection;
import java.util.List;

/**
 * This is a MutablePicoContainer that also supports soft composition. i.e. assembly by class name rather that class
 * reference.
 * <p/>
 * In terms of implementation it adopts the behaviour of DefaultPicoContainer and DefaulReflectionContainerAdapter
 *
 * @author Paul Hammant
 * @author Mauro Talevi
 * @version $Revision$
 */
public class DefaultSoftCompositionPicoContainer extends AbstractSoftCompositionPicoContainer implements SoftCompositionPicoContainer, Serializable {

    private final MutablePicoContainer delegate;

    // Serializable cannot be cascaded into DefaultReflectionContainerAdapter's referenced classes
    // need to implement custom Externalisable regime.
    private transient ReflectionContainerAdapter reflectionAdapter;

    public DefaultSoftCompositionPicoContainer(ClassLoader classLoader, ComponentAdapterFactory caf, PicoContainer parent) {
        delegate = new DefaultPicoContainer(caf, parent);

        reflectionAdapter = new DefaultReflectionContainerAdapter(classLoader, delegate);
    }

    public DefaultSoftCompositionPicoContainer(ClassLoader classLoader, PicoContainer parent) {
        delegate = new DefaultPicoContainer(new DefaultComponentAdapterFactory(), parent);

        reflectionAdapter = new DefaultReflectionContainerAdapter(classLoader, delegate);
    }

    public DefaultSoftCompositionPicoContainer(ComponentAdapterFactory caf) {
        delegate = new DefaultPicoContainer(caf, null);

        reflectionAdapter = new DefaultReflectionContainerAdapter(DefaultSoftCompositionPicoContainer.class.getClassLoader(), delegate);
    }

    public DefaultSoftCompositionPicoContainer(PicoContainer pc) {
        this(DefaultSoftCompositionPicoContainer.class.getClassLoader(), pc);
    }

    public DefaultSoftCompositionPicoContainer(ClassLoader classLoader) {
        this(classLoader, null);
    }

    public DefaultSoftCompositionPicoContainer() {
        this(DefaultSoftCompositionPicoContainer.class.getClassLoader(), null);
    }


    public Object getComponentInstanceFromDelegate(Object componentKey) throws PicoException {
        return delegate.getComponentInstance(componentKey);
    }

    public Object getComponentInstanceOfType(Class componentType) {
        return delegate.getComponentInstanceOfType(componentType);
    }

    public Object getComponentInstanceOfType(String componentType) {
        return reflectionAdapter.getComponentInstanceOfType(componentType);
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

    // --------------------

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
        ClassLoader currentClassloader = reflectionAdapter.getComponentClassLoader();
        DefaultSoftCompositionPicoContainer child = new DefaultSoftCompositionPicoContainer(currentClassloader, this);
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

    public List getComponentInstancesOfType(Class type) throws PicoException {
        return delegate.getComponentInstancesOfType(type);
    }

    // ----  from reflection adapter -----

    public ComponentAdapter registerComponentImplementation(String componentImplementationClassName) throws PicoRegistrationException, ClassNotFoundException, PicoIntrospectionException {
        return reflectionAdapter.registerComponentImplementation(componentImplementationClassName);
    }

    public ComponentAdapter registerComponentImplementation(Object key, String componentImplementationClassName) throws ClassNotFoundException {
        return reflectionAdapter.registerComponentImplementation(key, componentImplementationClassName);
    }
    
    public ComponentAdapter registerComponentImplementation(Object key, String componentImplementationClassName, Parameter[] parameters) throws ClassNotFoundException {
        return reflectionAdapter.registerComponentImplementation(key, componentImplementationClassName, parameters);
    }

    public ComponentAdapter registerComponentImplementation(Object key, String componentImplementationClassName, String[] parameterTypesAsString, String[] parameterValuesAsString) throws PicoRegistrationException, ClassNotFoundException, PicoIntrospectionException {
        return reflectionAdapter.registerComponentImplementation(key, componentImplementationClassName, parameterTypesAsString, parameterValuesAsString);
    }

    public ComponentAdapter registerComponentImplementation(String componentImplementationClassName, String[] parameterTypesAsString, String[] parameterValuesAsString) throws PicoRegistrationException, ClassNotFoundException, PicoIntrospectionException {
        return reflectionAdapter.registerComponentImplementation(componentImplementationClassName, parameterTypesAsString, parameterValuesAsString);
    }

    public void addClassLoaderURL(URL url) {
        reflectionAdapter.addClassLoaderURL(url);
    }

    public MutablePicoContainer getPicoContainer() {
        return this;
    }

    public ClassLoader getComponentClassLoader() {
        return reflectionAdapter.getComponentClassLoader();
    }

    public boolean equals(Object obj) {
        return delegate.equals(obj);
    }

    public void accept(PicoVisitor visitor, Class componentType, boolean visitInInstantiationOrder) {
        delegate.accept(visitor, componentType, visitInInstantiationOrder);
    }

}
