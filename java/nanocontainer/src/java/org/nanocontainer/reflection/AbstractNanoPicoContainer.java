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

import org.nanocontainer.NanoPicoContainer;
import org.nanocontainer.NanoContainer;
import org.nanocontainer.DefaultNanoContainer;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoException;
import org.picocontainer.PicoVerificationException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoVisitor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.net.URL;
import java.io.Serializable;

/**
 * A base class for SoftCompositionPicoContainers. As well as the functionality indicated by the interface it
 * implements, extenders of this class will have named child component capability.
 *
 * @author Paul Hammant
 * @version $Revision$
 */
public abstract class AbstractNanoPicoContainer implements NanoPicoContainer, Serializable {

    protected MutablePicoContainer delegate;
    protected Map namedChildContainers = new HashMap();

    // Serializable cannot be cascaded into DefaultNanoContainer's referenced classes
    // need to implement custom Externalisable regime.
    protected transient NanoContainer container;

    protected AbstractNanoPicoContainer() {
    }

    protected AbstractNanoPicoContainer(MutablePicoContainer delegate, ClassLoader classLoader) {
        this.delegate = delegate;
        container = new DefaultNanoContainer(classLoader, delegate);
    }

    public final Object getComponentInstance(Object componentKey) throws PicoException {

        Object instance = delegate.getComponentInstance(componentKey);

        if (instance != null) {
            return instance;
        }

        ComponentAdapter componentAdapter = null;
        if (componentKey.toString().startsWith("*")) {
            String candidateClassName = componentKey.toString().substring(1);
            Collection cas = getComponentAdapters();
            for (Iterator it = cas.iterator(); it.hasNext();) {
                ComponentAdapter ca = (ComponentAdapter) it.next();
                Object key = ca.getComponentKey();
                if (key instanceof Class && candidateClassName.equals(((Class) key).getName())) {
                    componentAdapter = ca;
                    break;
                }
            }
        }
        if (componentAdapter != null) {
            return componentAdapter.getComponentInstance(this);
        } else {
            return getComponentInstanceFromChildren(componentKey);
        }
    }

    private final Object getComponentInstanceFromChildren(Object componentKey) {
        String componentKeyPath = componentKey.toString();
        int ix = componentKeyPath.indexOf('/');
        if (ix != -1) {
            String firstElement = componentKeyPath.substring(0, ix);
            String remainder = componentKeyPath.substring(ix + 1, componentKeyPath.length());
            Object o = getNamedContainers().get(firstElement);
            if (o != null) {
                MutablePicoContainer child = (MutablePicoContainer) o;
                return child.getComponentInstance(remainder);
            }
        }
        return null;
    }

    public final MutablePicoContainer makeChildContainer() {
        return makeChildContainer("containers" + namedChildContainers.size());
    }

    public void removeChildContainer(PicoContainer child) {
        delegate.removeChildContainer(child);
        Iterator children = namedChildContainers.entrySet().iterator();
        while (children.hasNext()) {
            Map.Entry e = (Map.Entry) children.next();
            PicoContainer pc = (PicoContainer) e.getValue();
            if (pc == child) {
                children.remove();
            }
        }
    }

    protected final Map getNamedContainers() {
        return namedChildContainers;
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

    public void addClassLoaderURL(URL url) {
        container.addClassLoaderURL(url);
    }

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


    //TODO Should this method be the NanoContainer interface only?
    public MutablePicoContainer getPico() {
        return this;
    }

    public ClassLoader getComponentClassLoader() {
        return container.getComponentClassLoader();
    }

    public void accept(PicoVisitor visitor) {
        delegate.accept(visitor);
    }

    public List getComponentInstancesOfType(Class type) throws PicoException {
        return delegate.getComponentInstancesOfType(type);
    }

    public void addChildContainer(PicoContainer child) {
        delegate.addChildContainer(child);
        namedChildContainers.put("containers" + namedChildContainers.size(), child);
    }

    public void addChildContainer(String name, PicoContainer child) {
        delegate.addChildContainer(child);
        namedChildContainers.put(name, child);
    }

    public boolean equals(Object o) {
        return delegate.equals(o);
    }
}
