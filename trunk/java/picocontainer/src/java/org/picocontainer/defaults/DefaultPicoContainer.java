package org.picocontainer.defaults;

import org.picocontainer.*;

import java.io.Serializable;
import java.util.*;

/**
 * The Standard {@link PicoContainer}/{@link MutablePicoContainer} implementation.
 * Constructing a container c with a parent p container will cause c to look up components
 * in p if they cannot be found inside c itself.
 * 
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Jon Tirs&eacute;n
 * @version $Revision: 1.8 $
 */
public class DefaultPicoContainer implements MutablePicoContainer, Serializable {

    private final LifecycleAdapter lifecycleAdapter = new LifecycleAdapter(this);

    private final Map componentKeyToAdapterCache = new HashMap();
    private final ComponentAdapterFactory componentAdapterFactory;
    private PicoContainer parent;
    private final List componentAdapters = new ArrayList();

    // Keeps track of instantiation order. Will be cleared regularly.
    private final List orderedComponentAdapters = new ArrayList();

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

    public final Collection getComponentKeys() {
        return createComponentAdapterMap(this).keySet();
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
    private Map getComponentAdapterMap() {
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
        return (ComponentAdapter) getComponentAdapterMap().get(componentKey);
    }

    public ComponentAdapter getComponentAdapterOfType(Class componentType) {
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
        componentAdapter.setContainer(this);
        componentAdapters.add(componentAdapter);
        Object componentKey = componentAdapter.getComponentKey();
        if (componentKeyToAdapterCache.containsKey(componentKey)) {
            throw new DuplicateComponentKeyRegistrationException(componentKey);
        }
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

    public boolean hasComponent(Object componentKey) {
        return getComponentKeys().contains(componentKey);
    }

    public PicoContainer getParent() {
        return parent;
    }

    public void setParent(PicoContainer parent) {
        this.parent = parent;
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
        lifecycleAdapter.start();
    }

    public void stop(){
        lifecycleAdapter.stop();
    }

    public void dispose() {
        lifecycleAdapter.dispose();
    }

    public boolean isStarted() {
        return lifecycleAdapter.isStarted();
    }

    public boolean isStopped() {
        return lifecycleAdapter.isStopped();
    }

    public boolean isDisposed() {
        return lifecycleAdapter.isDisposed();
    }
}
