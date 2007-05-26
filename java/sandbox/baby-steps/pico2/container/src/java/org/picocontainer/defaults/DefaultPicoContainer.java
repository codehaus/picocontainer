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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleManager;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoVisitor;
import org.picocontainer.Startable;
import org.picocontainer.Disposable;
import org.picocontainer.ComponentCharacteristic;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.adapters.CachingComponentAdapterFactory;
import org.picocontainer.alternatives.AbstractDelegatingMutablePicoContainer;
import org.picocontainer.alternatives.EmptyPicoContainer;
import org.picocontainer.adapters.AnyInjectionComponentAdapterFactory;
import org.picocontainer.adapters.InstanceComponentAdapter;
import org.picocontainer.lifecycle.StartableLifecycleStrategy;
import org.picocontainer.monitors.NullComponentMonitor;

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
 * registered with a {@link java.lang.Class} key of the corresponding type, this addComponent
 * will take precedence over other components during type resolution.
 * </p>
 * <p/>
 * Another place where keys that are classes make a subtle difference is in
 * {@link org.picocontainer.adapters.ImplementationHidingComponentAdapter}.
 * </p>
 * <p/>
 * This implementation of {@link MutablePicoContainer} also supports
 * {@link ComponentMonitorStrategy}.
 * </p>
 *
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Jon Tirs&eacute;n
 * @author Thomas Heller
 * @author Mauro Talevi
 * @version $Revision: 1.8 $
 */
public class DefaultPicoContainer implements MutablePicoContainer, ComponentMonitorStrategy, Serializable {
    private Map<Object, ComponentAdapter> componentKeyToAdapterCache = new HashMap<Object, ComponentAdapter>();
    private ComponentAdapterFactory componentAdapterFactory;
    private PicoContainer parent;
    private Set<PicoContainer> children = new HashSet<PicoContainer>();

    private List<ComponentAdapter> componentAdapters = new ArrayList<ComponentAdapter>();
    // Keeps track of instantiation order.
    private List<ComponentAdapter> orderedComponentAdapters = new ArrayList<ComponentAdapter>();

    // Keeps track of the container started status
    private boolean started = false;
    // Keeps track of the container disposed status
    private boolean disposed = false;
    // Keeps track of child containers started status
    private Set<Integer> childrenStarted = new HashSet<Integer>();

    private LifecycleManager lifecycleManager = new OrderedComponentAdapterLifecycleManager();
    private LifecycleStrategy lifecycleStrategy;
    private ComponentCharacteristic componentCharacteristic = new ComponentCharacteristic() {
        public void mergeInto(ComponentCharacteristic rc) {
        }
        public boolean isSoCharacterized(ComponentCharacteristic rc) {
            return false;
        }
    };
    private ComponentMonitor componentMonitor = NullComponentMonitor.getInstance();

    /**
     * Creates a new container with a custom ComponentAdapterFactory and a parent container.
     * <p/>
     * <em>
     * Important note about caching: If you intend the components to be cached, you should pass
     * in a factory that creates {@link org.picocontainer.adapters.CachingComponentAdapter} instances, such as for example
     * {@link org.picocontainer.adapters.CachingComponentAdapterFactory}. CachingComponentAdapterFactory can delegate to
     * other ComponentAdapterFactories.
     * </em>
     *
     * @param componentAdapterFactory the factory to use for creation of ComponentAdapters.
     * @param parent                  the parent container (used for addComponent dependency lookups).
     */
    public DefaultPicoContainer(ComponentAdapterFactory componentAdapterFactory, PicoContainer parent) {
        this(componentAdapterFactory, new StartableLifecycleStrategy(NullComponentMonitor.getInstance()), parent);
    }

    /**
     * Creates a new container with a custom ComponentAdapterFactory, LifecycleStrategy for instance registration,
     *  and a parent container.
     * <p/>
     * <em>
     * Important note about caching: If you intend the components to be cached, you should pass
     * in a factory that creates {@link org.picocontainer.adapters.CachingComponentAdapter} instances, such as for example
     * {@link org.picocontainer.adapters.CachingComponentAdapterFactory}. CachingComponentAdapterFactory can delegate to
     * other ComponentAdapterFactories.
     * </em>
     *
     * @param componentAdapterFactory the factory to use for creation of ComponentAdapters.
     * @param lifecycleStrategyForInstanceRegistrations the lifecylce strategy chosen for regiered
     *          instance (not implementations!)
     * @param parent                  the parent container (used for addComponent dependency lookups).
     */
    public DefaultPicoContainer(ComponentAdapterFactory componentAdapterFactory,
                                LifecycleStrategy lifecycleStrategyForInstanceRegistrations,
                                PicoContainer parent) {
        if (componentAdapterFactory == null) throw new NullPointerException("componentAdapterFactory");
        if (lifecycleStrategyForInstanceRegistrations == null) throw new NullPointerException("lifecycleStrategy");
        this.componentAdapterFactory = componentAdapterFactory;
        this.lifecycleStrategy = lifecycleStrategyForInstanceRegistrations;
        this.parent = parent;
        if (parent != null && !(parent instanceof EmptyPicoContainer)) {
            this.parent = ImmutablePicoContainerProxyFactory.newProxyInstance(parent);
        }
    }

    /**
      * Creates a new container with the AnyInjectionComponentAdapterFactory using a
      * custom ComponentMonitor
      *
      * @param monitor the ComponentMonitor to use
      * @param parent the parent container (used for addComponent dependency lookups).
      */
    public DefaultPicoContainer(ComponentMonitor monitor, PicoContainer parent) {
        this(new CachingComponentAdapterFactory().forThis(new AnyInjectionComponentAdapterFactory()), parent);
        lifecycleStrategy = new StartableLifecycleStrategy(monitor);
        componentMonitor = monitor;
    }

    /**
      * Creates a new container with the AnyInjectionComponentAdapterFactory using a
      * custom ComponentMonitor and lifecycle strategy
      *
      * @param monitor the ComponentMonitor to use
      * @param lifecycleStrategy the lifecycle strategy to use.
      * @param parent the parent container (used for addComponent dependency lookups).
      */
    public DefaultPicoContainer(ComponentMonitor monitor, LifecycleStrategy lifecycleStrategy, PicoContainer parent) {
        this(new CachingComponentAdapterFactory().forThis(new AnyInjectionComponentAdapterFactory()), lifecycleStrategy,  parent);
        componentMonitor = monitor;

    }


    // to assist PicoBuilder
    public DefaultPicoContainer(ComponentAdapterFactory caf, ComponentMonitor monitor, LifecycleStrategy lifecycleStrategy, PicoContainer parent) {
        this(caf, lifecycleStrategy,  parent);
        componentMonitor = monitor;
    }

    /**
      * Creates a new container with the AnyInjectionComponentAdapterFactory using a
      * custom lifecycle strategy
      *
      * @param lifecycleStrategy the lifecycle strategy to use.
      * @param parent the parent container (used for addComponent dependency lookups).
      */
    public DefaultPicoContainer(LifecycleStrategy lifecycleStrategy, PicoContainer parent) {
        this(NullComponentMonitor.getInstance(), lifecycleStrategy, parent);
    }


    /**
     * Creates a new container with a custom ComponentAdapterFactory and no parent container.
     *
     * @param componentAdapterFactory the ComponentAdapterFactory to use.
     */
    public DefaultPicoContainer(ComponentAdapterFactory componentAdapterFactory) {
        this(componentAdapterFactory, null);
    }

    /**
      * Creates a new container with the AnyInjectionComponentAdapterFactory using a
      * custom ComponentMonitor
      *
      * @param monitor the ComponentMonitor to use
      */
    public DefaultPicoContainer(ComponentMonitor monitor) {
        this(monitor, new StartableLifecycleStrategy(monitor), null);
    }

    /**
     * Creates a new container with a (caching) {@link AnyInjectionComponentAdapterFactory}
     * and a parent container.
     *
     * @param parent the parent container (used for addComponent dependency lookups).
     */
    public DefaultPicoContainer(PicoContainer parent) {
        this(new CachingComponentAdapterFactory().forThis(new AnyInjectionComponentAdapterFactory()), parent);
    }

    /**
     * Creates a new container with a (caching) {@link AnyInjectionComponentAdapterFactory} and no parent container.
     */
    public DefaultPicoContainer() {
        this(new CachingComponentAdapterFactory().forThis(new AnyInjectionComponentAdapterFactory()), null);
    }

    public Collection<ComponentAdapter> getComponentAdapters() {
        return Collections.unmodifiableList(componentAdapters);
    }

    public final ComponentAdapter getComponentAdapter(Object componentKey) {
        ComponentAdapter adapter = componentKeyToAdapterCache.get(componentKey);
        if (adapter == null && parent != null) {
            adapter = parent.getComponentAdapter(componentKey);
        }
        return adapter;
    }

    public ComponentAdapter getComponentAdapter(Class componentType) {
        // See http://jira.codehaus.org/secure/ViewIssue.jspa?key=PICO-115
        ComponentAdapter adapterByKey = getComponentAdapter((Object) componentType);
        if (adapterByKey != null) {
            return adapterByKey;
        }

        List<ComponentAdapter> found = getComponentAdapters(componentType);

        if (found.size() == 1) {
            return ((ComponentAdapter) found.get(0));
        } else if (found.size() == 0) {
            if (parent != null) {
                return parent.getComponentAdapter(componentType);
            } else {
                return null;
            }
        } else {
            Class[] foundClasses = new Class[found.size()];
            for (int i = 0; i < foundClasses.length; i++) {
                foundClasses[i] = found.get(i).getComponentImplementation();
            }

            throw new AmbiguousComponentResolutionException(componentType, foundClasses);
        }
    }

    public List<ComponentAdapter> getComponentAdapters(Class componentType) {
        if (componentType == null) {
            return (List<ComponentAdapter>) Collections.EMPTY_LIST;
        }
        List<ComponentAdapter> found = new ArrayList<ComponentAdapter>();
        for (ComponentAdapter componentAdapter : getComponentAdapters()) {
            if (componentType.isAssignableFrom(componentAdapter.getComponentImplementation())) {
                found.add(componentAdapter);
            }
        }
        return found;
    }

    /**
     * {@inheritDoc}
     * This method can be used to override the ComponentAdapter created by the {@link ComponentAdapterFactory}
     * passed to the constructor of this container.
     */
    public MutablePicoContainer addAdapter(ComponentAdapter componentAdapter) {
        Object componentKey = componentAdapter.getComponentKey();
        if (componentKeyToAdapterCache.containsKey(componentKey)) {
            throw new DuplicateComponentKeyRegistrationException(componentKey);
        }
        componentAdapters.add(componentAdapter);
        componentKeyToAdapterCache.put(componentKey, componentAdapter);
        return new TemporaryAdapterReturningPicoContainer(componentAdapter);
    }

    public ComponentAdapter removeComponent(Object componentKey) {
        ComponentAdapter adapter = componentKeyToAdapterCache.remove(componentKey);
        componentAdapters.remove(adapter);
        orderedComponentAdapters.remove(adapter);
        return adapter;
    }

    /**
     * {@inheritDoc}
     * The returned ComponentAdapter will be an {@link org.picocontainer.adapters.InstanceComponentAdapter}.
     */
    public MutablePicoContainer addComponent(Object implOrInstance) {
        Class clazz;
        if (implOrInstance instanceof CharacterizedObject) {
            CharacterizedObject co = (CharacterizedObject) implOrInstance;
            if (co.implOrInst instanceof Class) {
                clazz = (Class) co.implOrInst;
            } else {
                clazz = co.getClass();
            }
        } else if (implOrInstance instanceof Class) {
            clazz = (Class) implOrInstance;
        } else {
            clazz = implOrInstance.getClass();
        }
        return addComponent(clazz, implOrInstance);
    }

    /**
     * {@inheritDoc}
     * The returned ComponentAdapter will be instantiated by the {@link ComponentAdapterFactory}
     * passed to the container's constructor.
     */
    public MutablePicoContainer addComponent(Object componentKey, Object componentImplementationOrInstance, Parameter... parameters) {
        ComponentCharacteristic characteristic = this.componentCharacteristic;
        if (componentImplementationOrInstance instanceof CharacterizedObject) {
            characteristic = ((CharacterizedObject) componentImplementationOrInstance).characteristic;
            componentImplementationOrInstance = ((CharacterizedObject) componentImplementationOrInstance).implOrInst;
        }
        if (parameters != null && parameters.length == 0 && parameters != Parameter.ZERO) {
            parameters = null; // backwards compatibility!  solve this better later - Paul
        }
        if (componentImplementationOrInstance instanceof Class) {
            ComponentAdapter componentAdapter = componentAdapterFactory.createComponentAdapter(componentMonitor, lifecycleStrategy, characteristic, componentKey,
                    (Class) componentImplementationOrInstance, parameters);
            return addAdapter(componentAdapter);
        } else {
            ComponentAdapter componentAdapter = new InstanceComponentAdapter(componentKey, componentImplementationOrInstance, lifecycleStrategy);
            return addAdapter(componentAdapter);
        }
    }

    protected ComponentCharacteristic getComponentCharacteristic() {
        return componentCharacteristic;
    }

    private void addOrderedComponentAdapter(ComponentAdapter componentAdapter) {
        if (!orderedComponentAdapters.contains(componentAdapter)) {
            orderedComponentAdapters.add(componentAdapter);
        }
    }

    public List getComponents() throws PicoException {
        return getComponents(Object.class);
    }

    public List getComponents(Class componentType) {
        if (componentType == null) {
            return Collections.EMPTY_LIST;
        }

        Map<ComponentAdapter, Object> adapterToInstanceMap = new HashMap<ComponentAdapter, Object>();
        for (ComponentAdapter componentAdapter : componentAdapters) {
            if (componentType.isAssignableFrom(componentAdapter.getComponentImplementation())) {
                Object componentInstance = getInstance(componentAdapter);
                adapterToInstanceMap.put(componentAdapter, componentInstance);

                // This is to ensure all are added. (Indirect dependencies will be added
                // from InstantiatingComponentAdapter).
                addOrderedComponentAdapter(componentAdapter);
            }
        }
        List<Object> result = new ArrayList<Object>();
        for (Object componentAdapter : orderedComponentAdapters) {
            final Object componentInstance = adapterToInstanceMap.get(componentAdapter);
            if (componentInstance != null) {
                // may be null in the case of the "implicit" addAdapter
                // representing "this".
                result.add(componentInstance);
            }
        }
        return result;
    }

    public Object getComponent(Object componentKeyOrType) {
        Object retVal;
        if (componentKeyOrType instanceof Class) {
            final ComponentAdapter componentAdapter = getComponentAdapter((Class) componentKeyOrType);
            retVal = componentAdapter == null ? null : getInstance(componentAdapter);
        } else {
            ComponentAdapter componentAdapter = getComponentAdapter(componentKeyOrType);
            retVal = componentAdapter == null ? null : getInstance(componentAdapter);
        }
        if (retVal == null) {
            componentMonitor.noComponent(componentKeyOrType);
        }
        return retVal;
    }

    public <T> T getComponent(Class<T> componentType) {
        return (T) getComponent((Object) componentType);
    }

    private Object getInstance(ComponentAdapter componentAdapter) {
        // check wether this is our addAdapter
        // we need to check this to ensure up-down dependencies cannot be followed
        final boolean isLocal = componentAdapters.contains(componentAdapter);

        if (isLocal) {
            Object instance = null;
            try {
                instance = componentAdapter.getComponentInstance(this);
            } catch (CyclicDependencyException e) {
                if (parent != null) {
                    instance = parent.getComponent(componentAdapter.getComponentKey());
                    if( instance != null ) {
                        return instance;
                    }
                }
                throw e;
            }
            addOrderedComponentAdapter(componentAdapter);

            return instance;
        } else if (parent != null) {
            return parent.getComponent(componentAdapter.getComponentKey());
        }

        return null;
    }


    public PicoContainer getParent() {
        return parent;
    }

    public ComponentAdapter removeComponentByInstance(Object componentInstance) {
        Collection<ComponentAdapter> componentAdapters = getComponentAdapters();
        for (ComponentAdapter componentAdapter : componentAdapters) {
            if (getInstance(componentAdapter).equals(componentInstance)) {
                return removeComponent(componentAdapter.getComponentKey());
            }
        }
        return null;
    }

    /**
     * Start the components of this PicoContainer and all its logical child containers.
     * The starting of the child container is only attempted if the parent
     * container start successfully.  The child container for which start is attempted
     * is tracked so that upon stop, only those need to be stopped.
     * The lifecycle operation is delegated to the addComponent addAdapter,
     * if it is an instance of {@link LifecycleManager lifecycle manager}.
     * The actual {@link LifecycleStrategy lifecycle strategy} supported
     * depends on the concrete implementation of the addAdapter.
     *
     * @see LifecycleManager
     * @see LifecycleStrategy
     * @see #makeChildContainer()
     * @see #addChildContainer(PicoContainer)
     * @see #removeChildContainer(PicoContainer)
     */
    public void start() {
        if (disposed) throw new IllegalStateException("Already disposed");
        if (started) throw new IllegalStateException("Already started");
        started = true;
        this.lifecycleManager.start(this);
        childrenStarted.clear();
        for (PicoContainer child : children) {
            childrenStarted.add(child.hashCode());
            if (child instanceof Startable) {
                ((Startable) child).start();
            }
        }
    }

    /**
     * Stop the components of this PicoContainer and all its logical child containers.
     * The stopping of the child containers is only attempted for those that have been
     * started, possibly not successfully.
     * The lifecycle operation is delegated to the addComponent addAdapter,
     * if it is an instance of {@link LifecycleManager lifecycle manager}.
     * The actual {@link LifecycleStrategy lifecycle strategy} supported
     * depends on the concrete implementation of the addAdapter.
     *
     * @see LifecycleManager
     * @see LifecycleStrategy
     * @see #makeChildContainer()
     * @see #addChildContainer(PicoContainer)
     * @see #removeChildContainer(PicoContainer)
     */
    public void stop() {
        if (disposed) throw new IllegalStateException("Already disposed");
        if (!started) throw new IllegalStateException("Not started");
        for (PicoContainer child : children) {
            if (childStarted(child)) {
                if (child instanceof Startable) {
                    ((Startable) child).stop();
                }
            }
        }
        this.lifecycleManager.stop(this);
        started = false;
    }

    /**
     * Checks the status of the child container to see if it's been started
     * to prevent IllegalStateException upon stop
     * @param child the child PicoContainer
     * @return A boolean, <code>true</code> if the container is started
     */
    private boolean childStarted(PicoContainer child) {
        return childrenStarted.contains(new Integer(child.hashCode()));
    }

    /**
     * Dispose the components of this PicoContainer and all its logical child containers.
     * The lifecycle operation is delegated to the addComponent addAdapter,
     * if it is an instance of {@link LifecycleManager lifecycle manager}.
     * The actual {@link LifecycleStrategy lifecycle strategy} supported
     * depends on the concrete implementation of the addAdapter.
     *
     * @see LifecycleManager
     * @see LifecycleStrategy
     * @see #makeChildContainer()
     * @see #addChildContainer(PicoContainer)
     * @see #removeChildContainer(PicoContainer)
     */
    public void dispose() {
        if (disposed) throw new IllegalStateException("Already disposed");
        for (PicoContainer child : children) {
            if (child instanceof MutablePicoContainer) {
                ((Disposable) child).dispose();
            }
        }
        this.lifecycleManager.dispose(this);
        disposed = true;
    }

    public MutablePicoContainer makeChildContainer() {
        DefaultPicoContainer pc = new DefaultPicoContainer(componentAdapterFactory, lifecycleStrategy, this);
        addChildContainer(pc);
        return pc;
    }

    public boolean addChildContainer(PicoContainer child) {
        if (children.add(child)) {
            // @todo Should only be added if child container has also be started
            if (started) {
                childrenStarted.add(child.hashCode());
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean removeChildContainer(PicoContainer child) {
        final boolean result = children.remove(child);
        childrenStarted.remove(new Integer(child.hashCode()));
        return result;
    }

    public ComponentAdapter lastCA() {
        return null; 
    }

    public MutablePicoContainer change(ComponentCharacteristic... characteristics) {
        for (ComponentCharacteristic c : characteristics) {
            c.mergeInto(this.componentCharacteristic);
        }
        return this;
    }

    public MutablePicoContainer as(ComponentCharacteristic... characteristics) {
        return new TemporaryCharacterizedPicoContainer(characteristics);
    }

    public void accept(PicoVisitor visitor) {
        visitor.visitContainer(this);
        final List<ComponentAdapter> componentAdapters = new ArrayList<ComponentAdapter>(getComponentAdapters());
        for (ComponentAdapter componentAdapter : componentAdapters) {
            componentAdapter.accept(visitor);
        }
        final List<PicoContainer> allChildren = new ArrayList<PicoContainer>(children);
        for (PicoContainer child : allChildren) {
            child.accept(visitor);
        }
    }

    /**
     * Changes monitor in the ComponentAdapterFactory, the addComponent adapters
     * and the child containers, if these support a ComponentMonitorStrategy.
     * {@inheritDoc}
     */
    public void changeMonitor(ComponentMonitor monitor) {
        this.componentMonitor = monitor;
        if (lifecycleStrategy instanceof ComponentMonitorStrategy) {
            ((ComponentMonitorStrategy) lifecycleStrategy).changeMonitor(monitor);
        }
        for (ComponentAdapter adapter : componentAdapters) {
            if (adapter instanceof ComponentMonitorStrategy) {
                ((ComponentMonitorStrategy) adapter).changeMonitor(monitor);
            }
        }
        for (PicoContainer child : children) {
            if (child instanceof ComponentMonitorStrategy) {
                ((ComponentMonitorStrategy) child).changeMonitor(monitor);
            }
        }
    }

    /**
     * Returns the first current monitor found in the ComponentAdapterFactory, the addComponent adapters
     * and the child containers, if these support a ComponentMonitorStrategy.
     * {@inheritDoc}
     * @throws PicoIntrospectionException if no addComponent monitor is found in container or its children
     */
    public ComponentMonitor currentMonitor() {
        return componentMonitor;
    }

   /**
    * <p>
    * Implementation of lifecycle manager which delegates to the container's addComponent adapters.
    * The addComponent adapters will be ordered by dependency as registered in the container.
    * This LifecycleManager will delegate calls on the lifecycle methods to the addComponent adapters
    * if these are themselves LifecycleManagers.
    * </p>
    *
    * @author Mauro Talevi
    * @since 1.2
    */
    private class OrderedComponentAdapterLifecycleManager implements LifecycleManager, Serializable {

        /** List collecting the CAs which have been successfully started */
        private List<ComponentAdapter> startedComponentAdapters = new ArrayList<ComponentAdapter>();

        /**
         * {@inheritDoc}
         * Loops over all addComponent adapters and invokes
         * start(PicoContainer) method on the ones which are LifecycleManagers
         */
        public void start(PicoContainer node) {
            Collection<ComponentAdapter> adapters = getComponentAdapters();
            for (ComponentAdapter adapter : adapters) {
                if (adapter instanceof LifecycleManager) {
                    LifecycleManager manager = (LifecycleManager) adapter;
                    if (manager.hasLifecycle()) {
                        // create an instance, it will be added to the ordered CA list
                        adapter.getComponentInstance(node);
                        addOrderedComponentAdapter(adapter);
                    }
                }
            }
            adapters = orderedComponentAdapters;
            // clear list of started CAs
            startedComponentAdapters.clear();
            for (final ComponentAdapter adapter : adapters) {
                if (adapter instanceof LifecycleManager) {
                    LifecycleManager manager = (LifecycleManager) adapter;
                    manager.start(node);
                    startedComponentAdapters.add(adapter);
                }
            }
        }

        /**
         * {@inheritDoc}
         * Loops over started addComponent adapters (in inverse order) and invokes
         * stop(PicoContainer) method on the ones which are LifecycleManagers
         */
        public void stop(PicoContainer node) {
            List<ComponentAdapter> adapters = startedComponentAdapters;
            for (int i = adapters.size() - 1; 0 <= i; i--) {
                ComponentAdapter adapter = adapters.get(i);
                if ( adapter instanceof LifecycleManager ){
                    LifecycleManager manager = (LifecycleManager)adapter;
                    manager.stop(node);
                }
            }
        }

        /**
         * {@inheritDoc}
         * Loops over all addComponent adapters (in inverse order) and invokes
         * dispose(PicoContainer) method on the ones which are LifecycleManagers
         */
        public void dispose(PicoContainer node) {
            List<ComponentAdapter> adapters = orderedComponentAdapters;
            for (int i = adapters.size() - 1; 0 <= i; i--) {
                ComponentAdapter adapter = adapters.get(i);
                if ( adapter instanceof LifecycleManager ){
                    LifecycleManager manager = (LifecycleManager)adapter;
                    manager.dispose(node);
                }
            }
        }

        public boolean hasLifecycle() {
            throw new UnsupportedOperationException("Should not have been called");
        }

    }

    private class TemporaryAdapterReturningPicoContainer extends AbstractDelegatingMutablePicoContainer {
        private final ComponentAdapter componentAdapter;

        public TemporaryAdapterReturningPicoContainer(ComponentAdapter componentAdapter) {
            super(DefaultPicoContainer.this);
            this.componentAdapter = componentAdapter;
        }

        public MutablePicoContainer makeChildContainer() {
            return getDelegate().makeChildContainer();
        }

        public ComponentAdapter lastCA() {
            return componentAdapter;
        }
    }

    private class TemporaryCharacterizedPicoContainer extends AbstractDelegatingMutablePicoContainer {
        private final ComponentCharacteristic[] characteristics;

        public TemporaryCharacterizedPicoContainer(ComponentCharacteristic... characteristics) {
            super(DefaultPicoContainer.this);
            this.characteristics = characteristics;
        }

        public MutablePicoContainer makeChildContainer() {
            return getDelegate().makeChildContainer();
        }

        public MutablePicoContainer addComponent(Object componentKey, Object componentImplementationOrInstance, Parameter... parameters) throws PicoRegistrationException {
            return super.addComponent(componentKey, makeCharacterizedImplOrInstance(componentImplementationOrInstance), parameters);
        }

        public MutablePicoContainer addComponent(Object implOrInstance) throws PicoRegistrationException {
            return super.addComponent(makeCharacterizedImplOrInstance(implOrInstance));
        }


        private CharacterizedObject makeCharacterizedImplOrInstance(Object componentImplementationOrInstance) {
            ComponentCharacteristic tempCharacteristic = (ComponentCharacteristic) componentCharacteristic.clone();
            for (ComponentCharacteristic c : characteristics) {
                c.mergeInto(tempCharacteristic);
            }
            return new CharacterizedObject(tempCharacteristic, componentImplementationOrInstance);
        }

    }

    private static class CharacterizedObject {
        private final ComponentCharacteristic characteristic;
        private final Object implOrInst;

        public CharacterizedObject(ComponentCharacteristic tempCharacteristic, Object componentImplementationOrInstance) {
            characteristic = tempCharacteristic;
            implOrInst = componentImplementationOrInstance;
        }
    }


}
