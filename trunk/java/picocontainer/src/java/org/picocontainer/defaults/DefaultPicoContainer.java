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
import org.picocontainer.Disposable;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.PicoVerificationException;
import org.picocontainer.Startable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p/>
 * The Standard {@link PicoContainer}/{@link MutablePicoContainer} implementation.
 * Constructing a container c with a parent p container will cause c to look up components
 * in p if they cannot be found inside c itself.
 * </p>
 * <p/>
 * Using {@link Class} objects as keys to the various registerXXX() methods makes
 * a subtle semantic difference:
 * </p>
 * <p/>
 * If there are more than one registered components of the same type and one of them are
 * registered with a {@link java.lang.Class} key of the corresponding type, this component
 * will take precedence over other components during type resolution.
 * </p>
 * <p/>
 * Another place where keys that are classes make a subtle difference is in
 * {@link org.picocontainer.alternatives.ImplementationHidingComponentAdapter}.
 * </p>
 *
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Jon Tirs&eacute;n
 * @author Thomas Heller
 * @version $Revision: 1.8 $
 */
public class DefaultPicoContainer implements MutablePicoContainer, Serializable {

    private Map componentKeyToAdapterCache = new HashMap();
    private ComponentAdapterFactory componentAdapterFactory;
    private PicoContainer parent;
    private List componentAdapters = new ArrayList();

    // Keeps track of instantiation order.
    private List orderedComponentAdapters = new ArrayList();
    private boolean started = false;
    private boolean disposed = false;

    protected Map namedChildContainers = new HashMap();

    /**
     * Creates a new container with a custom ComponentAdapterFactory and a parent container.
     * <p/>
     * <em>
     * Important note about caching: If you intend the components to be cached, you should pass
     * in a factory that creates {@link CachingComponentAdapter} instances, such as for example
     * {@link CachingComponentAdapterFactory}. CachingComponentAdapterFactory can delegate to
     * other ComponentAdapterFactories.
     * </em>
     *
     * @param componentAdapterFactory the factory to use for creation of ComponentAdapters.
     * @param parent                  the parent container.
     */
    public DefaultPicoContainer(ComponentAdapterFactory componentAdapterFactory, PicoContainer parent) {
        this.componentAdapterFactory = componentAdapterFactory;
        this.parent = parent;
    }

    /**
     * Creates a new container with a (caching) {@link DefaultComponentAdapterFactory} and a parent container.
     */
    public DefaultPicoContainer(PicoContainer parent) {
        this(new DefaultComponentAdapterFactory(), parent);
    }

    /**
     * Creates a new container with a custom ComponentAdapterFactory and no parent container.
     */
    public DefaultPicoContainer(ComponentAdapterFactory componentAdapterFactory) {
        this(componentAdapterFactory, null);
    }

    /**
     * Creates a new container with a (caching) {@link DefaultComponentAdapterFactory} and no parent container.
     */
    public DefaultPicoContainer() {
        this(new DefaultComponentAdapterFactory(), null);
    }

    public Collection getComponentAdapters() {
        return Collections.unmodifiableList(componentAdapters);
    }

    public final ComponentAdapter getComponentAdapter(Object componentKey) throws AmbiguousComponentResolutionException {
        ComponentAdapter adapter = (ComponentAdapter) componentKeyToAdapterCache.get(componentKey);
        if (adapter == null && parent != null) {
            adapter = parent.getComponentAdapter(componentKey);
        }
        return adapter;
    }

    public ComponentAdapter getComponentAdapterOfType(Class componentType) {
        // See http://jira.codehaus.org/secure/ViewIssue.jspa?key=PICO-115
        ComponentAdapter adapterByKey = getComponentAdapter(componentType);
        if (adapterByKey != null) {
            return adapterByKey;
        }

        List found = getComponentAdaptersOfType(componentType);

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

    public List getComponentAdaptersOfType(Class componentType) {
        List found = new ArrayList();
        for (Iterator iterator = getComponentAdapters().iterator(); iterator.hasNext();) {
            ComponentAdapter componentAdapter = (ComponentAdapter) iterator.next();

            if (componentType.isAssignableFrom(componentAdapter.getComponentImplementation())) {
                found.add(componentAdapter);
            }
        }
        return Collections.unmodifiableList(found);
    }

    /**
     * {@inheritDoc}
     * This method can be used to override the ComponentAdapter created by the {@link ComponentAdapterFactory}
     * passed to the constructor of this container.
     */
    public ComponentAdapter registerComponent(ComponentAdapter componentAdapter) throws DuplicateComponentKeyRegistrationException {
        Object componentKey = componentAdapter.getComponentKey();
        if (componentKeyToAdapterCache.containsKey(componentKey)) {
            throw new DuplicateComponentKeyRegistrationException(componentKey);
        }
        setComponentAdaptersContainer(componentAdapter);
        componentAdapters.add(componentAdapter);
        componentKeyToAdapterCache.put(componentKey, componentAdapter);
        return componentAdapter;
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
        if (componentInstance instanceof MutablePicoContainer) {
            MutablePicoContainer pc = (MutablePicoContainer) componentInstance;
            Object contrivedKey = new Object();
            String contrivedComp = "";
            pc.registerComponentInstance(contrivedKey, contrivedComp);
            try {
                if (this.getComponentInstance(contrivedKey) != null) {
                    throw new PicoRegistrationException("Cannot register a container to itself. The container is already implicitly registered.");
                }
            } finally {
                pc.unregisterComponent(contrivedKey);
            }

        }
        ComponentAdapter componentAdapter = new InstanceComponentAdapter(componentKey, componentInstance);
        registerComponent(componentAdapter);
        return componentAdapter;
    }

    protected void setComponentAdaptersContainer(ComponentAdapter componentAdapter) {
        componentAdapter.setContainer(this);
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
        return registerComponentImplementation(componentKey, componentImplementation, (Parameter[]) null);
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

    /**
     * Same as {@link #registerComponentImplementation(java.lang.Object, java.lang.Class, org.picocontainer.Parameter[])}
     * but with parameters as a {@link List}. Makes it possible to use with Groovy arrays (which are actually Lists).
     */
    public ComponentAdapter registerComponentImplementation(Object componentKey, Class componentImplementation, List parameters) throws PicoRegistrationException {
        Parameter[] parametersAsArray = (Parameter[]) parameters.toArray(new Parameter[parameters.size()]);
        return registerComponentImplementation(componentKey, componentImplementation, parametersAsArray);
    }

    public void addOrderedComponentAdapter(ComponentAdapter componentAdapter) {
        if (!orderedComponentAdapters.contains(componentAdapter)) {
            orderedComponentAdapters.add(componentAdapter);
        }
    }

    public List getComponentInstances() throws PicoException {
        return getComponentInstancesOfType(null);
    }

    public List getComponentInstancesOfType(Class type) throws PicoException {
        Map adapterToInstanceMap = new HashMap();
        for (Iterator iterator = componentAdapters.iterator(); iterator.hasNext();) {
            ComponentAdapter componentAdapter = (ComponentAdapter) iterator.next();
            if (type == null || type.isAssignableFrom(componentAdapter.getComponentImplementation())) {
                Object componentInstance = componentAdapter.getComponentInstance();
                adapterToInstanceMap.put(componentAdapter, componentInstance);

                // This is to ensure all are added. (Indirect dependencies will be added
                // from InstantiatingComponentAdapter).
                addOrderedComponentAdapter(componentAdapter);
            }
        }
        List result = new ArrayList();
        for (Iterator iterator = orderedComponentAdapters.iterator(); iterator.hasNext();) {
            Object componentAdapter = iterator.next();
            final Object componentInstance = adapterToInstanceMap.get(componentAdapter);
            if (componentInstance != null) {
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

    public ComponentAdapter unregisterComponentByInstance(Object componentInstance) {
        Collection componentAdapters = getComponentAdapters();
        for (Iterator iterator = componentAdapters.iterator(); iterator.hasNext();) {
            ComponentAdapter componentAdapter = (ComponentAdapter) iterator.next();
            if (componentAdapter.getComponentInstance().equals(componentInstance)) {
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
        if (disposed) throw new IllegalStateException("Already disposed");
        if (started) throw new IllegalStateException("Already started");
        List componentInstances = getComponentInstancesOfTypeWithContainerAdaptersLast(Startable.class);
        for (Iterator iterator = componentInstances.iterator(); iterator.hasNext();) {
            Startable startable = ((Startable) iterator.next());
            startable.start();
        }
        Iterator it = namedChildContainers.values().iterator();
        while (it.hasNext()) {
            PicoContainer pc = (PicoContainer) it.next();
            pc.getComponentInstances();
            pc.start();
        }
        started = true;
    }

    public void stop() {
        if (disposed) throw new IllegalStateException("Already disposed");
        if (!started) throw new IllegalStateException("Not started");
        Iterator it = namedChildContainers.values().iterator();
        while (it.hasNext()) {
            PicoContainer pc = (PicoContainer) it.next();
            pc.stop();
        }
        List componentInstances = getComponentInstancesOfTypeWithContainerAdaptersLast(Startable.class);
        Collections.reverse(componentInstances);
        for (Iterator iterator = componentInstances.iterator(); iterator.hasNext();) {
            ((Startable) iterator.next()).stop();
        }
        started = false;
    }

    public void dispose() {
        if (disposed) throw new IllegalStateException("Already disposed");
        Iterator it = namedChildContainers.values().iterator();
        while (it.hasNext()) {
            PicoContainer pc = (PicoContainer) it.next();
            pc.dispose();
        }
        List componentInstances = getComponentInstancesOfTypeWithContainerAdaptersLast(Disposable.class);
        Collections.reverse(componentInstances);
        for (Iterator iterator = componentInstances.iterator(); iterator.hasNext();) {
            ((Disposable) iterator.next()).dispose();
        }
        if (parent instanceof MutablePicoContainer) {
            ((MutablePicoContainer) parent).removeChildContainer(this);
        }
        parent = null;
        namedChildContainers = null;
        disposed = true;

    }

    private List getComponentInstancesOfTypeWithContainerAdaptersLast(Class type) {
        List result = new ArrayList();
        result.addAll(getComponentInstancesOfType(type));
        Collections.sort(result, new StackContainersAtEndComparator());
        return result;
    }

    public MutablePicoContainer makeChildContainer() {
        return makeChildContainer("containers" + namedChildContainers.values().size());
    }

    public MutablePicoContainer makeChildContainer(String name) {
        DefaultPicoContainer pc = new DefaultPicoContainer(componentAdapterFactory, this);
        addChildContainer(name, pc);
        return pc;
    }

    public void addChildContainer(PicoContainer child) {
        addChildContainer(null, child);
    }

    public void addChildContainer(String name, PicoContainer child) {
        if (name == null) {
            name = "containers" + namedChildContainers.values().size();
        }
        namedChildContainers.put(name, child);
    }

    public void removeChildContainer(MutablePicoContainer child) {
        Iterator children = namedChildContainers.entrySet().iterator();
        while (children.hasNext()) {
            Map.Entry e = (Map.Entry) children.next();
            PicoContainer pc = (PicoContainer) e.getValue();
            if (pc == child) {
                children.remove();
            }
        }
    }

    /**
     * This comparator makes sure containers are always stacked at the end of the collection,
     * leaving the order of the others unchanged. This is needed in order to have proper
     * breadth-first traversal when calling lifecycle methods on container hierarchies.
     *
     * @author Aslak Helles&oslash;y
     * @version $Revision: 1.4 $
     */
    class StackContainersAtEndComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            if (PicoContainer.class.isInstance(o1)) {
                return 1;
            }
            if (PicoContainer.class.isInstance(o2)) {
                return -1;
            }
            return 0;
        }
    }

    protected Map getNamedContainers() {
        return namedChildContainers;
    }

}
