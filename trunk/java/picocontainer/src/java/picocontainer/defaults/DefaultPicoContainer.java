/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package picocontainer.defaults;

import picocontainer.RegistrationPicoContainer;
import picocontainer.ComponentFactory;
import picocontainer.PicoInitializationException;
import picocontainer.PicoIntrospectionException;
import picocontainer.PicoRegistrationException;
import picocontainer.Parameter;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Abstract baseclass for various PicoContainer implementations.
 *
 * @author Aslak Hellesoy
 * @version $Revision: 1.8 $
 */
public class DefaultPicoContainer implements RegistrationPicoContainer {

    private final ComponentFactory componentFactory;
    private List registeredComponents = new ArrayList();
    private Map componentKeyToInstanceMap = new HashMap();

    // Keeps track of the instantiation order
    protected List orderedComponents = new ArrayList();

    // Keeps track of unmanaged components - components instantiated outside this container
    protected List unmanagedComponents = new ArrayList();

    private boolean initialized;
    private Map componentToSpec = new HashMap();
    private CompositeProxyFactory compositeProxyFactory = new DefaultCompositeProxyFactory();

    public static class Default extends DefaultPicoContainer {
        public Default() {
            super(new DefaultComponentFactory());
        }
    }

    public DefaultPicoContainer(ComponentFactory componentFactory) {
        if (componentFactory == null) {
            throw new NullPointerException("componentFactory cannot be null");
        }
        this.componentFactory = componentFactory;
    }

    public Object[] getComponents() {
       /* <ASLAK>
        * TODO: make final again
        *
        * The reason why we're not simply doing
        *
        * return componentKeyToInstanceMap.values().toArray();
        *
        * is that we want a simple way to override the behaviour
        * of getComponents() and getComponentKeys(). These methods
        * are conceptually related, and one should therefore depend
        * on the other. (Like the below implementation).
        *
        * (Overriding one and not another will probably break the container)
        *
        * Ideally, this method should be final, so we can avoid unexpected
        * behaviour. Making this method non-final is bad (IMHO) because
        * if someone overrides only this method, and forget to override
        * getComponentKeys() *will* result in strange behaviour.
        *
        * Paul removed the final specifier in order to open up for some of James'
        * stuff, but as I'm trying to explain, I think this is very dangerous.
        *
        * </ASLAK>
        */

        Object[] componentKeys = getComponentKeys();
        Object[] components = new Object[componentKeys.length];
        for (int i = 0; i < componentKeys.length; i++) {
            Object componentKey = componentKeys[i];
            components[i] = getComponent(componentKey);
        }
        return components;
    }

    /**
     * @deprecated Use {@link #getCompositeComponent} instead
     */
    public final Object getAggregateComponentProxy() {
        return getCompositeComponent();
    }

    /**
     * @deprecated Use getCompositeComponent instead
     */
    public final Object getAggregateComponentProxy(boolean callInInstantiationOrder, boolean callUnmanagedComponents) {
        return getCompositeComponent(callInInstantiationOrder, callUnmanagedComponents);
    }

    // see PicoContainer interface for Javadocs
    public final Object getCompositeComponent()
    {
        return getCompositeComponent(true, false);
    }

    // see PicoContainer interface for Javadocs
    public final Object getCompositeComponent(boolean callInInstantiationOrder, boolean callUnmanagedComponents)
    {
        List aggregateComponents = new ArrayList(orderedComponents);
        if(!callUnmanagedComponents) {
            for (Iterator iterator = unmanagedComponents.iterator(); iterator.hasNext();) {
                aggregateComponents.remove( iterator.next() );
            }
        }
        return compositeProxyFactory.createCompositeProxy(
                getClass().getClassLoader(),
                aggregateComponents,
                callInInstantiationOrder
        );
    }

    public void registerComponent(Object componentKey, Class componentImplementation) throws DuplicateComponentKeyRegistrationException, AssignabilityRegistrationException, NotConcreteRegistrationException, PicoIntrospectionException {
        checkConcrete(componentImplementation);
        checkTypeCompatibility(componentKey, componentImplementation);
        checkKeyDuplication(componentImplementation);

        registerComponent(new ComponentSpecification(componentFactory, componentKey, componentImplementation));
    }

    public void registerComponent(Object componentKey, Class componentImplementation, Parameter[] parameters) throws NotConcreteRegistrationException, AssignabilityRegistrationException, DuplicateComponentKeyRegistrationException {
        checkConcrete(componentImplementation);
        checkTypeCompatibility(componentKey, componentImplementation);
        checkKeyDuplication(componentImplementation);

        registerComponent(new ComponentSpecification(componentFactory, componentKey, componentImplementation, parameters));
    }

    private void registerComponent(ComponentSpecification compSpec) {
        componentToSpec.put(compSpec.getComponentImplementation(), compSpec);
        registeredComponents.add(compSpec);
    }

    private void checkKeyDuplication(Object componentKey) throws DuplicateComponentKeyRegistrationException {
        for (Iterator iterator = registeredComponents.iterator(); iterator.hasNext();) {
            Object key = ((ComponentSpecification) iterator.next()).getComponentKey();
            if (key == componentKey) {
                throw new DuplicateComponentKeyRegistrationException(key);
            }
        }
    }

    private void checkTypeCompatibility(Object componentKey, Class componentImplementation) throws AssignabilityRegistrationException {
        if (componentKey instanceof Class) {
            Class componentType = (Class) componentKey;
            if (!componentType.isAssignableFrom(componentImplementation)) {
                throw new AssignabilityRegistrationException(componentType, componentImplementation);
            }
        }
    }

    private void checkConcrete(Class componentImplementation) throws NotConcreteRegistrationException {
        // Assert that the component class is concrete.
        boolean isAbstract = (componentImplementation.getModifiers() & Modifier.ABSTRACT) == Modifier.ABSTRACT;
        if (componentImplementation.isInterface() || isAbstract) {
            throw new NotConcreteRegistrationException(componentImplementation);
        }
    }

    public void registerComponentByInstance(Object component) throws PicoRegistrationException, PicoIntrospectionException {
        registerComponent(component.getClass(), component);
    }

    public void registerComponent(Object componentKey, Object component) throws PicoRegistrationException, PicoIntrospectionException {
        checkTypeCompatibility(componentKey, component.getClass());
        checkKeyDuplication(componentKey);
        registerComponent(new ComponentSpecification(defaultComponentFactory(), componentKey, component.getClass(), null));
        componentKeyToInstanceMap.put(componentKey, component);
        orderedComponents.add(component);
        unmanagedComponents.add(component);
    }

    private ComponentFactory defaultComponentFactory() {
        return componentFactory;
    }

    public void addParameterToComponent(Object componentKey, Class parameter, Object arg) throws PicoIntrospectionException {
        ComponentSpecification componentSpec = ((ComponentSpecification) componentToSpec.get(componentKey));
        componentSpec.addConstantParameterBasedOnType(parameter, arg);
    }

    public void registerComponentByClass(Class componentImplementation) throws DuplicateComponentKeyRegistrationException, AssignabilityRegistrationException, NotConcreteRegistrationException, PicoIntrospectionException {
        registerComponent(componentImplementation, componentImplementation);
    }

    public void instantiateComponents() throws PicoInitializationException, PicoInvocationTargetInitializationException {
        if (initialized == false) {
            initializeComponents();
            initialized = true;
        } else {
            throw new IllegalStateException("PicoContainer Started Already");
        }
    }

    // This is Lazy and NOT public :-)
    private void initializeComponents() throws PicoInitializationException {
        for (Iterator iterator = registeredComponents.iterator(); iterator.hasNext();) {
            ComponentSpecification componentSpec = (ComponentSpecification) iterator.next();
            createComponent(componentSpec);
        }
    }

    Object createComponent(ComponentSpecification componentSpecification) throws PicoInitializationException {
        if (!componentKeyToInstanceMap.containsKey(componentSpecification.getComponentKey())) {

            Object component = componentSpecification.instantiateComponent(this);
            orderedComponents.add(component);

            componentKeyToInstanceMap.put(componentSpecification.getComponentKey(), component);

            return component;
        } else {
            return componentKeyToInstanceMap.get(componentSpecification.getComponentKey());
        }
    }

    public Object getComponent(Object componentKey) {
        return componentKeyToInstanceMap.get(componentKey);
    }

    Object createComponent(Class componentType) throws PicoInitializationException {
        Object componentInstance = getComponent(componentType);

        if (componentInstance != null) {
            return componentInstance;
        }

        componentInstance = findImplementingComponent(componentType);

        if (componentInstance != null) {
            return componentInstance;
        }

        // try to find components that satisfy the interface (implements the component service asked for)
        ComponentSpecification componentSpecification = findImplementingComponentSpecification(componentType);
        if (componentSpecification == null) {
            return null;
        }

        // if the component does not exist yet, instantiate it lazily
        componentInstance = createComponent(componentSpecification);

        return componentInstance;
    }

    Object findImplementingComponent(Class componentType) throws AmbiguousComponentResolutionException {
        List found = new ArrayList();

        for (Iterator iterator = componentKeyToInstanceMap.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Object key = entry.getKey();
            Object component = entry.getValue();
            if(componentType.isInstance(component)) {
                found.add(key);
            }
        }

        if (found.size() > 1) {
            Object[] ambiguousKeys = found.toArray();
            throw new AmbiguousComponentResolutionException(componentType, ambiguousKeys);
        }

        return found.isEmpty() ? null : componentKeyToInstanceMap.get(found.get(0));
    }

    Object getComponent(ComponentSpecification componentSpec) {
        return componentSpec == null ? null : getComponent(componentSpec.getComponentKey());
    }

    ComponentSpecification findImplementingComponentSpecification(Class componentType) throws AmbiguousComponentResolutionException {
        List found = new ArrayList();
        for (Iterator iterator = registeredComponents.iterator(); iterator.hasNext();) {
            ComponentSpecification componentSpecification = (ComponentSpecification) iterator.next();

            if (componentType.isAssignableFrom(componentSpecification.getComponentImplementation())) {
                found.add(componentSpecification);
            }
        }

        if (found.size() > 1) {
            Class[] foundClasses = new Class[found.size()];
            for (int i = 0; i < foundClasses.length; i++) {
                foundClasses[i] = ((ComponentSpecification) found.get(i)).getComponentImplementation();
            }
            throw new AmbiguousComponentResolutionException(componentType, foundClasses);
        }

        return found.isEmpty() ? null : ((ComponentSpecification) found.get(0));
    }

    public Object[] getComponentKeys() {
        // Get my own
        Set types = componentKeyToInstanceMap.keySet();
        return (Class[]) types.toArray(new Class[types.size()]);
    }

    public final boolean hasComponent(Object componentKey) {
        return getComponent(componentKey) != null;
    }

    public ComponentSpecification getComponentSpecification(Object componentKey) {
        for (Iterator iterator = registeredComponents.iterator(); iterator.hasNext();) {
            ComponentSpecification componentSpecification = (ComponentSpecification) iterator.next();
            if (componentSpecification.getComponentKey().equals(componentKey)) {
                return componentSpecification;
            }
        }
        return null;
    }
}
