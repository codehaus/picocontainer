package org.picocontainer.defaults;

import org.picocontainer.extras.ComponentMulticasterFactory;
import org.picocontainer.*;

import java.io.Serializable;
import java.util.*;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public abstract class AbstractPicoContainer implements MutablePicoContainer, Serializable {
    // Keeps track of unmanaged components - components instantiated outside this internals
    private final List unmanagedComponents = new ArrayList();

    // Keeps track of the instantiation order
    private final List orderedComponents = new ArrayList();

    protected ComponentAdapterFactory componentAdapterFactory;

    public abstract Collection getComponentKeys();
    public abstract List getComponentAdapters();
    public abstract void registerComponent(ComponentAdapter compSpec) throws PicoRegistrationException;
    public abstract ComponentAdapter findComponentAdapter(Object componentKey) throws AmbiguousComponentResolutionException;

    public void registerComponentInstance(Object component) throws PicoRegistrationException{
        registerComponentInstance(component.getClass(), component);
    }

    public void registerComponentInstance(Object componentKey, Object componentInstance) throws PicoRegistrationException {
        ComponentAdapter componentAdapter = new InstanceComponentAdapter(componentKey, componentInstance);
        registerComponent(componentAdapter);

        addOrderedComponentInstance(componentInstance);
        unmanagedComponents.add(componentInstance);
    }

    public void registerComponentImplementation(Class componentImplementation) throws PicoRegistrationException {
        registerComponentImplementation(componentImplementation, componentImplementation);
    }

    public Object getComponentMulticaster(boolean callInInstantiationOrder, boolean callUnmanagedComponents) throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        // TODO: take a MultiCasterFactory as argument. That way we can support
        // multicasters based on reflection (e.g. look for execute() methods) too.
        // Nice for ppl who want lifecycle by following naming conventions
        // on methods instead of implementing interfaces. --Aslak
        ComponentMulticasterFactory componentMulticasterFactory = new DefaultComponentMulticasterFactory();

        getComponentInstances();
        List componentsToMulticast = getOrderedComponents();
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

    public Object getComponentMulticaster() throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        return getComponentMulticaster(true, false);
    }

    public void registerComponentImplementation(Object componentKey, Class componentImplementation) throws PicoRegistrationException {
        registerComponentImplementation(componentKey, componentImplementation, null);
    }

    public void registerComponentImplementation(Object componentKey, Class componentImplementation, Parameter[] parameters) throws PicoRegistrationException {
        ComponentAdapter componentAdapter = componentAdapterFactory.createComponentAdapter(componentKey, componentImplementation, parameters);
        registerComponent(componentAdapter);
    }

    public List getOrderedComponents() {
        return new ArrayList(orderedComponents);
    }

    public void addOrderedComponentInstance(Object componentInstance) {
        orderedComponents.add(componentInstance);
    }

    public Object getComponentInstance(Object componentKey) throws PicoIntrospectionException, PicoInitializationException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        ComponentAdapter componentAdapter = findComponentAdapter(componentKey);
        if(componentAdapter != null) {
            return componentAdapter.getComponentInstance(this);
        } else {
            return null;
        }
    }

    public Collection getComponentInstances() throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        ArrayList componentInstances = new ArrayList(getComponentKeys().size());
        for (Iterator iterator = getComponentKeys().iterator(); iterator.hasNext();) {
            Object componentInstance = getComponentInstance(iterator.next());
            componentInstances.add(componentInstance);
        }
        return Collections.unmodifiableCollection(componentInstances);
    }

    public Object findComponentInstance(Class componentType) throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
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
        } else if(foundKeys.size() > 1) {
            throw new AmbiguousComponentResolutionException(componentType, foundKeys.toArray());
        }

        return result;
    }

    public boolean hasComponent(Object componentKey) {
        return getComponentKeys().contains(componentKey);
    }

    public ComponentAdapter findImplementingComponentAdapter(Class componentType) throws AmbiguousComponentResolutionException {
        List found = new ArrayList();
        for (Iterator iterator = getComponentAdapters().iterator(); iterator.hasNext();) {
            ComponentAdapter componentAdapter = (ComponentAdapter) iterator.next();

            if (componentType.isAssignableFrom(componentAdapter.getComponentImplementation())) {
                found.add(componentAdapter);
            }
        }

        if (found.size() == 0) {
            return null;
        } else if (found.size() > 1) {
            Class[] foundClasses = new Class[found.size()];
            for (int i = 0; i < foundClasses.length; i++) {
                foundClasses[i] = ((ComponentAdapter) found.get(i)).getComponentImplementation();
            }
            throw new AmbiguousComponentResolutionException(componentType, foundClasses);
        }

        return found.isEmpty() ? null : ((ComponentAdapter) found.get(0));
    }

}
