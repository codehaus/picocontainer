package org.picocontainer.defaults;

import org.picocontainer.extras.ComponentMulticasterFactory;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoException;
import org.picocontainer.PicoVerificationException;
import org.picocontainer.Parameter;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Collections;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.8 $
 */
public class DefaultPicoContainer implements MutablePicoContainer, Serializable {

    // WeakReferences to aid GC and avoid circular references.
    private final List parentWeakReferences = new ArrayList();

    // Child containers.
    private final Collection children = new ArrayList();

    // Keeps track of unmanaged components - components instantiated outside the container
    private final List unmanagedComponents = new ArrayList();

    // Keeps track of the instantiation order (dependency order)
    private final List instantiantionOrderedComponentAdapters = new ArrayList();

    private final ComponentAdapterFactory componentAdapterFactory;

    private final Map componentKeyToAdapterMap = new HashMap();

    public DefaultPicoContainer(ComponentAdapterFactory componentAdapterFactory) {
        this.componentAdapterFactory = componentAdapterFactory;
    }

    public DefaultPicoContainer() {
        this(new DefaultComponentAdapterFactory());
    }

    public final Collection getComponentKeys() {
        Set result = new HashSet();
        result.addAll(componentKeyToAdapterMap.keySet());
        synchronized (parentWeakReferences) {
            for (Iterator iterator = parentWeakReferences.iterator(); iterator.hasNext();) {
                WeakReference wr = (WeakReference) iterator.next();
                if (wr.get() == null) {
                    parentWeakReferences.remove(wr);
                } else {
                    MutablePicoContainer delegate = (DefaultPicoContainer) wr.get();
                    result.addAll(delegate.getComponentKeys());
                }
            }
        }
        return result;
    }

    // TODO shouldn't have to be a public method.
    public List getComponentAdapters() {
        return new ArrayList(componentKeyToAdapterMap.values());
    }

    public void registerComponent(ComponentAdapter componentAdapter) throws DuplicateComponentKeyRegistrationException {
        Object componentKey = componentAdapter.getComponentKey();
        if (componentKeyToAdapterMap.keySet().contains(componentKey)) {
            throw new DuplicateComponentKeyRegistrationException(componentKey);
        }
        componentKeyToAdapterMap.put(componentKey, componentAdapter);
    }

    public ComponentAdapter unregisterComponent(Object componentKey) {
        ComponentAdapter result = (ComponentAdapter) componentKeyToAdapterMap.remove(componentKey);
        if(result != null) {
            Object instance = result.getComponentInstance(this);
            componentKeyToAdapterMap.remove(componentKey);
            instantiantionOrderedComponentAdapters.remove(result);
            unmanagedComponents.remove(instance);
        }
        return result;
    }

    public final ComponentAdapter findComponentAdapter(Object componentKey) throws AmbiguousComponentResolutionException {
        ComponentAdapter result = findComponentAdapterImpl(componentKey);
        if (result != null) {
            return result;
        } else {
            synchronized (parentWeakReferences) {
                for (Iterator iterator = parentWeakReferences.iterator(); iterator.hasNext();) {
                    WeakReference wr = (WeakReference) iterator.next();
                    if (wr.get() == null) {
                        parentWeakReferences.remove(wr);
                    } else {
                        MutablePicoContainer delegate = (MutablePicoContainer) wr.get();
                        ComponentAdapter componentAdapter = delegate.findComponentAdapter(componentKey);
                        if (componentAdapter != null) {
                            return componentAdapter;
                        }
                    }
                }
                return null;
            }
        }
    }

    private ComponentAdapter findComponentAdapterImpl(Object componentKey) throws AmbiguousComponentResolutionException {
        ComponentAdapter result = (ComponentAdapter) componentKeyToAdapterMap.get(componentKey);
        if (result == null && componentKey instanceof Class) {
            // see if we find a matching one if the key is a class
            Class classKey = (Class) componentKey;
            result = findImplementingComponentAdapter(classKey);
        }
        return result;
    }

    public ComponentAdapter registerComponentInstance(Object component) throws PicoRegistrationException {
        return registerComponentInstance(component.getClass(), component);
    }

    public ComponentAdapter registerComponentInstance(Object componentKey, Object componentInstance) throws PicoRegistrationException {
        ComponentAdapter componentAdapter = new InstanceComponentAdapter(componentKey, componentInstance);
        registerComponent(componentAdapter);

        addOrderedComponentAdapter(componentAdapter);
        unmanagedComponents.add(componentInstance);
        return componentAdapter;
    }

    public ComponentAdapter registerComponentImplementation(Class componentImplementation) throws PicoRegistrationException {
        return registerComponentImplementation(componentImplementation, componentImplementation);
    }

    /**
     * Returns an object (in fact, a dynamic proxy) that implements the union
     * of all the interfaces of the currently registered components.
     * <p>
     * Casting this object to any of those interfaces and then calling a method
     * on it will result in that call being multicast to all the components implementing
     * that given interface.
     * <p>
     * This is a simple yet extremely powerful way to handle lifecycle of components.
     * Component writers can invent their own lifecycle interfaces, and then use the multicaster
     * to invoke the method in one go.
     *
     * @param callInInstantiationOrder whether or not to call the method in the order of instantiation,
     *    which depends on the components' inter-dependencies.
     * @param callUnmanagedComponents whether or not to multicast to components that are not managed
     *    by this container.
     * @return a multicaster object.
     */
    public Object getComponentMulticaster(boolean callInInstantiationOrder, boolean callUnmanagedComponents) throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        // TODO: take a MultiCasterFactory as argument. That way we can support
        // multicasters based on reflection (e.g. look for execute() methods) too.
        // Nice for ppl who want lifecycle by following naming conventions
        // on methods instead of implementing interfaces. --Aslak
        // P.S. That functionality is implemented in InvokingComponentAdapterFactory D.S.
        ComponentMulticasterFactory componentMulticasterFactory = new DefaultComponentMulticasterFactory();

        List componentsToMulticast = getComponentInstances();
        if (!callUnmanagedComponents) {
            for (Iterator iterator = unmanagedComponents.iterator(); iterator.hasNext();) {
                componentsToMulticast.remove(iterator.next());
            }
        }
        return componentMulticasterFactory.createComponentMulticaster(
                getClass().getClassLoader(),
                componentsToMulticast,
                callInInstantiationOrder
        );
    }

    /**
     * Shorthand for {@link #getComponentMulticaster(boolean, boolean)}<pre>(true, false)</pre>,
     * which is the most common usage scenario.
     *
     * @return a multicaster object.
     * @throws PicoException
     */
    public Object getComponentMulticaster() throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        return getComponentMulticaster(true, false);
    }

    public ComponentAdapter registerComponentImplementation(Object componentKey, Class componentImplementation) throws PicoRegistrationException {
        return registerComponentImplementation(componentKey, componentImplementation, null);
    }

    public ComponentAdapter registerComponentImplementation(Object componentKey, Class componentImplementation, Parameter[] parameters) throws PicoRegistrationException {
        ComponentAdapter componentAdapter = componentAdapterFactory.createComponentAdapter(componentKey, componentImplementation, parameters);
        registerComponent(componentAdapter);
        return componentAdapter;
    }

    public void addOrderedComponentAdapter(ComponentAdapter componentAdapter) {
        instantiantionOrderedComponentAdapters.add(componentAdapter);
    }

    public List getComponentInstances() throws PicoException {
        for (Iterator iterator = getComponentKeys().iterator(); iterator.hasNext();) {
            // This will result in the list of ComponentAdapters being populated
            getComponentInstance(iterator.next());
        }
        List result = new ArrayList();
        for (Iterator componentAdapters = instantiantionOrderedComponentAdapters.iterator(); componentAdapters.hasNext();) {
            ComponentAdapter componentAdapter = (ComponentAdapter) componentAdapters.next();
            result.add(componentAdapter.getComponentInstance(this));
        }
        return result;
    }

    public Object getComponentInstance(Object componentKey) throws PicoException {
        ComponentAdapter componentAdapter = findComponentAdapter(componentKey);
        if (componentAdapter != null) {
            return componentAdapter.getComponentInstance(this);
        } else {
            return null;
        }
    }

    public Object findComponentInstance(Class componentType) throws PicoException {
        List foundKeys = new ArrayList();
        Object result = null;
        for (Iterator iterator = getComponentKeys().iterator(); iterator.hasNext();) {
            Object key = iterator.next();
            Object componentInstance = getComponentInstance(key);
            if (componentType.isInstance(componentInstance)) {
                result = componentInstance;
                foundKeys.add(key);
            }
        }

        if (foundKeys.size() == 0) {
            return null;
        } else if (foundKeys.size() > 1) {
            throw new AmbiguousComponentResolutionException(componentType, foundKeys.toArray());
        }

        return result;
    }

    public boolean hasComponent(Object componentKey) {
        return getComponentKeys().contains(componentKey);
    }

    public ComponentAdapter findImplementingComponentAdapter(Class componentType) throws PicoException {
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
            return null;
        } else {
            Class[] foundClasses = new Class[found.size()];
            for (int i = 0; i < foundClasses.length; i++) {
                ComponentAdapter componentAdapter = (ComponentAdapter) found.get(i);
                foundClasses[i] = componentAdapter.getComponentImplementation();
            }

            throw new AmbiguousComponentResolutionException(componentType, foundClasses);
        }
    }

    public Collection getChildContainers() {
        return Collections.unmodifiableCollection(children);
    }

    public List getParentContainers() {
        ArrayList retval = new ArrayList();
        synchronized (parentWeakReferences) {
            for (int i = 0; i < parentWeakReferences.size(); i++) {
                WeakReference wr = (WeakReference) parentWeakReferences.get(i);
                if (wr.get() == null) {
                    parentWeakReferences.remove(wr);
                } else {
                    MutablePicoContainer mutablePicoContainer = (MutablePicoContainer) wr.get();
                    retval.add(mutablePicoContainer);
                }

            }
        }
        return Collections.unmodifiableList(retval);
    }

    public boolean addChild(MutablePicoContainer child) {
        boolean result = false;
        if (!children.contains(child)) {
            result = children.add(child);
        }
        if (!child.getParentContainers().contains(this)) {
            child.addParent(this);
        }
        return result;
    }

    public boolean addParent(MutablePicoContainer parent) {
        boolean result = false;
        boolean found = false;
        synchronized (parentWeakReferences) {
            for (int i = 0; i < parentWeakReferences.size(); i++) {
                WeakReference wr = (WeakReference) parentWeakReferences.get(i);
                if (wr.get() != null && wr.get().equals(parent)) {
                    found = true;
                }
            }
            if (!found) {
                result = parentWeakReferences.add(new WeakReference(parent));
            }
        }
        if (!parent.getChildContainers().contains(this)) {
            parent.addChild(this);
        }
        return result;
    }

    public boolean removeChild(MutablePicoContainer child) {
        boolean removed = children.remove(child);
        if (child.getParentContainers().contains(this)) {
            child.removeParent(this);
        }
        return removed;
    }

    public boolean removeParent(MutablePicoContainer parent) {
        boolean removed = false;
        synchronized (parentWeakReferences) {
            for (int i = 0; i < parentWeakReferences.size(); i++) {
                WeakReference wr = (WeakReference) parentWeakReferences.get(i);
                if (wr.get() != null && wr.get().equals(parent)) {
                    removed = parentWeakReferences.remove(wr);
                }
            }
        }
        if (parent.getChildContainers().contains(this)) {
            parent.removeChild(this);
        }
        return removed;
    }

    public void verify() throws PicoVerificationException {
        List nestedVerificationExceptions = new ArrayList();
        for (Iterator iterator = getComponentAdapters().iterator(); iterator.hasNext();) {
            ComponentAdapter componentAdapter = (ComponentAdapter) iterator.next();
            try {
                componentAdapter.verify(this);
            } catch (NoSatisfiableConstructorsException e) {
                nestedVerificationExceptions.add(e);
            }
        }

        if (!nestedVerificationExceptions.isEmpty()) {
            throw new PicoVerificationException(nestedVerificationExceptions);
        }
    }
}
