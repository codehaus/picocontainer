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

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.PicoVerificationException;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.nanocontainer.SoftCompositionPicoContainer;

import java.io.Serializable;
import java.net.URL;
import java.util.Collection;
import java.util.List;

/**
 * This is a MutablePicoContainer that also supports soft composition. i.e. assembly by class name rather that class
 * reference.
 *
 * In terms of implementation it adopts the behaviour of DefaultPicoContainer and DefaulReflectionContainerAdapter
 *
 * @author Paul Hammant
 * @version $Revision$
 */
public class DefaultSoftCompositionPicoContainer implements SoftCompositionPicoContainer, Serializable {

    private final MutablePicoContainer delegate;

    // Serializable cannot be cascaded into DefaultReflectionContainerAdapter's referenced classes
    // need to implement custom Externalisable regime.
    private transient ReflectionContainerAdapter reflectionAdapter;

    public DefaultSoftCompositionPicoContainer(ClassLoader classLoader, ComponentAdapterFactory caf, PicoContainer parent) {
        delegate = new DefaultSoftCompositionPicoContainer.InnerMutablePicoContainer(caf, parent);

        reflectionAdapter = new DefaultReflectionContainerAdapter(classLoader, delegate);
    }

    public DefaultSoftCompositionPicoContainer(ClassLoader classLoader, PicoContainer parent) {
        delegate = new DefaultSoftCompositionPicoContainer.InnerMutablePicoContainer(new DefaultComponentAdapterFactory(), parent);

        reflectionAdapter = new DefaultReflectionContainerAdapter(classLoader, delegate);
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

    public Object getComponentInstance(Object componentKey) {
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
        componentAdapter.setContainer(this);
        return delegate.registerComponent(componentAdapter);
    }

    public ComponentAdapter unregisterComponent(Object componentKey) {
        return delegate.unregisterComponent(componentKey);
    }

    public ComponentAdapter unregisterComponentByInstance(Object componentInstance) {
        return delegate.unregisterComponentByInstance(componentInstance);
    }

    public PicoContainer getImmutable() {
        return delegate.getImmutable();
    }

    public MutablePicoContainer makeChildContainer() {
        ClassLoader currentClassloader = reflectionAdapter.getComponentClassLoader();
        DefaultSoftCompositionPicoContainer pc = new DefaultSoftCompositionPicoContainer(currentClassloader, this);
        delegate.addChildContainer(pc);
        return pc;
    }

    public void addChildContainer(MutablePicoContainer child) {
        delegate.addChildContainer(child);
    }

    public void removeChildContainer(MutablePicoContainer child) {
        delegate.removeChildContainer(child);
    }

    // --------------------

    public ComponentAdapter registerComponentImplementation(String componentImplementationClassName) throws PicoRegistrationException, ClassNotFoundException, PicoIntrospectionException {
        return reflectionAdapter.registerComponentImplementation(componentImplementationClassName);
    }

    public ComponentAdapter registerComponentImplementation(Object key, String componentImplementationClassName) throws ClassNotFoundException {
        return reflectionAdapter.registerComponentImplementation(key, componentImplementationClassName);
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
        return reflectionAdapter.getPicoContainer();
    }

    public ClassLoader getComponentClassLoader() {
        return reflectionAdapter.getComponentClassLoader();
    }

    private class InnerMutablePicoContainer extends DefaultPicoContainer {
        public InnerMutablePicoContainer(ComponentAdapterFactory componentAdapterFactory, PicoContainer parent) {
            super(componentAdapterFactory, parent);
        }

        protected void setComponentAdaptersContainer(ComponentAdapter componentAdapter) {
            componentAdapter.setContainer(DefaultSoftCompositionPicoContainer.this);
        }
    }

}
