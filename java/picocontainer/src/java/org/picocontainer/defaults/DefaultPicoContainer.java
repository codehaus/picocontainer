/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.defaults;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.PicoVerificationException;
import org.picocontainer.Startable;
import org.picocontainer.Disposable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The Standard {@link PicoContainer}/{@link MutablePicoContainer} implementation.
 * Constructing a container c with a parent p container will cause c to look up components
 * in p if they cannot be found inside c itself.
 *
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Jon Tirs&eacute;n
 * @author Thomas Heller
 * @version $Revision: 1.8 $
 */
public class DefaultPicoContainer implements MutablePicoContainer, Serializable {

    private final Map componentKeyToAdapterCache = new HashMap();
    private final ComponentAdapterFactory componentAdapterFactory;
    private PicoContainer parent;
    private final List componentAdapters = new ArrayList();

    // Keeps track of instantiation order.
    private final List orderedComponentAdapters = new ArrayList();
    private boolean started = false;
    private boolean disposed = false;

    public DefaultPicoContainer(ComponentAdapterFactory componentAdapterFactory, PicoContainer parent) {
        this.componentAdapterFactory = componentAdapterFactory;
        setParent(parent);
    }

    public DefaultPicoContainer(PicoContainer parent) {
        this(new DefaultComponentAdapterFactory(), parent);
    }

    public DefaultPicoContainer(ComponentAdapterFactory componentAdapterFactory) {
        this(componentAdapterFactory, null);
    }

    public DefaultPicoContainer() {
        this(new DefaultComponentAdapterFactory(), null);
    }

    private Map createComponentAdapterMap(PicoContainer container) {
        Collection componentAdapters = container.getComponentAdapters();
        Map result = new HashMap(componentAdapters.size());
        for (Iterator iterator = componentAdapters.iterator(); iterator.hasNext();) {
            ComponentAdapter componentAdapter = (ComponentAdapter) iterator.next();
            result.put(componentAdapter.getComponentKey(), componentAdapter);
        }
        return result;
    }

    /**
     * @return a Map of {@link ComponentAdapter}. The keys are the keys of the adapters, the values the
     *         adapters (including parent container adapters).
     */
    private Map getComponentAdapterMapFromEntireHierarchyWithChildrenMaskingParents() {
        Map result;
        if (parent != null) {
            result = createComponentAdapterMap(parent);
        } else {
            result = new HashMap();
        }
        // Add ourself at last. This will override parent entries.
        result.putAll(componentKeyToAdapterCache);
        return result;
    }

    public Collection getComponentAdapters() {
        return Collections.unmodifiableList(componentAdapters);
    }

    public final ComponentAdapter getComponentAdapter(Object componentKey) throws AmbiguousComponentResolutionException {
        return (ComponentAdapter) getComponentAdapterMapFromEntireHierarchyWithChildrenMaskingParents().get(componentKey);
    }

    public ComponentAdapter getComponentAdapterOfType(Class componentType) {
        // See http://jira.codehaus.org/secure/ViewIssue.jspa?key=PICO-115
        ComponentAdapter adapterByKey = getComponentAdapter(componentType);
        if (adapterByKey != null) {
            return adapterByKey;
        }

        List found = new ArrayList();
        for (Iterator iterator = getComponentAdapters().iterator(); iterator.hasNext();) {
            ComponentAdapter componentAdapter = (ComponentAdapter) iterator.next();

            if (componentType.isAssignableFrom(componentAdapter.getComponentImplementation())) {
                found.add(componentAdapter);
            }
        }

        if (found.size() == 1) {
            return ((ComponentAdapter) found.get(0));
        } else if (found.size() == 0) {
            if (parent != null) {
                return parent.getComponentAdapterOfType(componentType);
            } else {
                return null;
            }
        } else {
            Class[] foundClasses = new Class[found.size()];
            for (int i = 0; i < foundClasses.length; i++) {
                ComponentAdapter componentAdapter = (ComponentAdapter) found.get(i);
                foundClasses[i] = componentAdapter.getComponentImplementation();
            }

            throw new AmbiguousComponentResolutionException(componentType, foundClasses);
        }
    }

    /**
     * {@inheritDoc}
     * This method can be used to override the ComponentAdapter created by the {@link ComponentAdapterFactory}
     * passed to the constructor of this container.
     */
    public void registerComponent(ComponentAdapter componentAdapter) throws DuplicateComponentKeyRegistrationException {
        Object componentKey = componentAdapter.getComponentKey();
        if (componentKeyToAdapterCache.containsKey(componentKey)) {
            throw new DuplicateComponentKeyRegistrationException(componentKey);
        }
        componentAdapter.setContainer(this);
        componentAdapters.add(componentAdapter);
        componentKeyToAdapterCache.put(componentKey, componentAdapter);
    }

    public ComponentAdapter unregisterComponent(Object componentKey) {
        ComponentAdapter adapter = (ComponentAdapter) componentKeyToAdapterCache.remove(componentKey);
        componentAdapters.remove(adapter);
        orderedComponentAdapters.remove(adapter);
        return adapter;
    }

    /**
     * {@inheritDoc}
     * The returned ComponentAdapter will be an {@link InstanceComponentAdapter}.
     */
    public ComponentAdapter registerComponentInstance(Object component) throws PicoRegistrationException {
        return registerComponentInstance(component.getClass(), component);
    }

    /**
     * {@inheritDoc}
     * The returned ComponentAdapter will be an {@link InstanceComponentAdapter}.
     */
    public ComponentAdapter registerComponentInstance(Object componentKey, Object componentInstance) throws PicoRegistrationException {
        if(componentInstance == this)
            throw new PicoRegistrationException() {
                public String getMessage() {
                    return "Cannot register a container to itself. The container is already implicitly registered.";
                }
            };
        ComponentAdapter componentAdapter = new InstanceComponentAdapter(componentKey, componentInstance);
        registerComponent(componentAdapter);
        return componentAdapter;
    }

    /**
     * {@inheritDoc}
     * The returned ComponentAdapter will be instantiated by the {@link ComponentAdapterFactory}
     * passed to the container's constructor.
     */
    public ComponentAdapter registerComponentImplementation(Class componentImplementation) throws PicoRegistrationException {
        return registerComponentImplementation(componentImplementation, componentImplementation);
    }

    /**
     * {@inheritDoc}
     * The returned ComponentAdapter will be instantiated by the {@link ComponentAdapterFactory}
     * passed to the container's constructor.
     */
    public ComponentAdapter registerComponentImplementation(Object componentKey, Class componentImplementation) throws PicoRegistrationException {
        return registerComponentImplementation(componentKey, componentImplementation, null);
    }

    /**
     * {@inheritDoc}
     * The returned ComponentAdapter will be instantiated by the {@link ComponentAdapterFactory}
     * passed to the container's constructor.
     */
    public ComponentAdapter registerComponentImplementation(Object componentKey, Class componentImplementation, Parameter[] parameters) throws PicoRegistrationException {
        ComponentAdapter componentAdapter = componentAdapterFactory.createComponentAdapter(componentKey, componentImplementation, parameters);
        registerComponent(componentAdapter);
        return componentAdapter;
    }

    public void addOrderedComponentAdapter(ComponentAdapter componentAdapter) {
        if (!orderedComponentAdapters.contains(componentAdapter)) {
            orderedComponentAdapters.add(componentAdapter);
        }
    }

    public List getComponentInstances() throws PicoException {
        Map adapterToInstanceMap = new HashMap();
        for (Iterator iterator = componentAdapters.iterator(); iterator.hasNext();) {
            ComponentAdapter componentAdapter = (ComponentAdapter) iterator.next();
            Object componentInstance = componentAdapter.getComponentInstance();
            adapterToInstanceMap.put(componentAdapter, componentInstance);

            // This is to ensure all are added. (Indirect dependencies will be added
            // from InstantiatingComponentAdapter).
            addOrderedComponentAdapter(componentAdapter);
        }
        List result = new ArrayList();
        for (Iterator iterator = orderedComponentAdapters.iterator(); iterator.hasNext();) {
            Object componentAdapter = iterator.next();
            final Object componentInstance = adapterToInstanceMap.get(componentAdapter);
            if(componentInstance != null) {
                // may be null in the case of the "implicit" adapter
                // representing "this".
                result.add(componentInstance);
            }
        }
        return Collections.unmodifiableList(result);
    }

    public Object getComponentInstance(Object componentKey) throws PicoException {
        ComponentAdapter componentAdapter = getComponentAdapter(componentKey);
        if (componentAdapter != null) {
            return componentAdapter.getComponentInstance();
        } else {
            return null;
        }
    }

    public Object getComponentInstanceOfType(Class componentType) {
        final ComponentAdapter componentAdapter = getComponentAdapterOfType(componentType);
        return componentAdapter == null ? null : componentAdapter.getComponentInstance();
    }

    public PicoContainer getParent() {
        return parent;
    }

    public void setParent(PicoContainer parent) {
        this.parent = parent;
    }

    public ComponentAdapter unregisterComponentByInstance(Object componentInstance) {
        Collection componentAdapters = getComponentAdapters();
        for (Iterator iterator = componentAdapters.iterator(); iterator.hasNext();) {
            ComponentAdapter componentAdapter = (ComponentAdapter) iterator.next();
            if(componentAdapter.getComponentInstance().equals(componentInstance)) {
                return unregisterComponent(componentAdapter.getComponentKey());
            }
        }
        return null;
    }

    public void verify() throws PicoVerificationException {
        List nestedVerificationExceptions = new ArrayList();
        for (Iterator iterator = getComponentAdapters().iterator(); iterator.hasNext();) {
            ComponentAdapter componentAdapter = (ComponentAdapter) iterator.next();
            try {
                componentAdapter.verify();
            } catch (UnsatisfiableDependenciesException e) {
                nestedVerificationExceptions.add(e);
            }
        }

        if (!nestedVerificationExceptions.isEmpty()) {
            throw new PicoVerificationException(nestedVerificationExceptions);
        }
    }

    public void start() {
        if(started) throw new IllegalStateException("Already started");
        if(disposed) throw new IllegalStateException("Already disposed");
        List componentAdapters = new ArrayList(getComponentAdapters());
        for (Iterator iterator = componentAdapters.iterator(); iterator.hasNext();) {
            ComponentAdapter componentAdapter = (ComponentAdapter) iterator.next();
            if(Startable.class.isAssignableFrom(componentAdapter.getComponentImplementation())) {
                Startable startable = (Startable) componentAdapter.getComponentInstance();
                startable.start();
            }
        }
        started = true;
    }

    public void stop(){
        if(!started) throw new IllegalStateException("Not started");
        if(disposed) throw new IllegalStateException("Already disposed");
        List componentAdapters = new ArrayList(getComponentAdapters());
        Collections.reverse(componentAdapters);
        for (Iterator iterator = componentAdapters.iterator(); iterator.hasNext();) {
            ComponentAdapter componentAdapter = (ComponentAdapter) iterator.next();
            if(Startable.class.isAssignableFrom(componentAdapter.getComponentImplementation())) {
                Startable startable = (Startable) componentAdapter.getComponentInstance();
                startable.stop();
            }
        }
        started = false;
    }

    public void dispose() {
        if(disposed) throw new IllegalStateException("Already disposed");
        List componentAdapters = new ArrayList(getComponentAdapters());
        Collections.reverse(componentAdapters);
        for (Iterator iterator = componentAdapters.iterator(); iterator.hasNext();) {
            ComponentAdapter componentAdapter = (ComponentAdapter) iterator.next();
            if(Disposable.class.isAssignableFrom(componentAdapter.getComponentImplementation())) {
                Disposable disposable = (Disposable) componentAdapter.getComponentInstance();
                disposable.dispose();
            }
        }
        disposed = true;
    }

}
