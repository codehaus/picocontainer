/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.defaults;

import org.picocontainer.ComponentFactory;
import org.picocontainer.ComponentRegistry;
import org.picocontainer.Parameter;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.RegistrationPicoContainer;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Abstract baseclass for various PicoContainer implementations.
 *
 * @author Aslak Hellesoy
 * @version $Revision: 1.8 $
 */
public class DefaultPicoContainer implements RegistrationPicoContainer {


    private final ComponentRegistry componentRegistry;
    private final ComponentFactory componentFactory;

    // Keeps track of unmanaged components - components instantiated outside this container
    protected List unmanagedComponents = new ArrayList();

    private boolean initialized;
    private CompositeProxyFactory compositeProxyFactory = new DefaultCompositeProxyFactory();

    public static class Default extends DefaultPicoContainer {
        public Default() {
            super(new DefaultComponentFactory(), new DefaultComponentRegistry());
        }
    }

    public DefaultPicoContainer(ComponentFactory componentFactory, ComponentRegistry componentRegistry) {
        if (componentFactory == null) {
            throw new NullPointerException("componentFactory cannot be null");
        }
        if (componentRegistry == null) {
            throw new NullPointerException("componentRegistry cannot be null");
        }
        this.componentFactory = componentFactory;
        this.componentRegistry = componentRegistry;
    }

    // see PicoContainer interface for Javadocs
    public final Object getCompositeComponent()
    {
        return getCompositeComponent(true, false);
    }

    // see PicoContainer interface for Javadocs
    public final Object getCompositeComponent(boolean callInInstantiationOrder, boolean callUnmanagedComponents)
    {
        List aggregateComponents = componentRegistry.getOrderedComponents();
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
        componentRegistry.registerComponent(compSpec);
    }

    private void checkKeyDuplication(Object componentKey) throws DuplicateComponentKeyRegistrationException {
        for (Iterator iterator = componentRegistry.getRegisteredComponentIterator(); iterator.hasNext();) {
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
        componentRegistry.putComponent(componentKey, component);

        componentRegistry.addOrderedComponent(component);
        unmanagedComponents.add(component);
    }

    private ComponentFactory defaultComponentFactory() {
        return componentFactory;
    }

    public void addParameterToComponent(Object componentKey, Class parameter, Object arg) throws PicoIntrospectionException {
        ComponentSpecification componentSpec = componentRegistry.getComponentSpec(componentKey);
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
        for (Iterator iterator = componentRegistry.getRegisteredComponentIterator(); iterator.hasNext();) {
            ComponentSpecification componentSpec = (ComponentSpecification) iterator.next();
            createComponent(componentSpec);
        }
    }

    Object createComponent(ComponentSpecification componentSpecification) throws PicoInitializationException {
        if (!componentRegistry.contains(componentSpecification.getComponentKey())) {
            Object component = componentSpecification.instantiateComponent(this);
            componentRegistry.addOrderedComponent(component);

            componentRegistry.putComponent(componentSpecification.getComponentKey(), component);

            return component;
        } else {
            return componentRegistry.getComponentInstance(componentSpecification.getComponentKey());
        }
    }

    public Object getComponent(Object componentKey) {
        return componentRegistry.getComponentInstance(componentKey);
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

        for (Iterator iterator = componentRegistry.getInstanceMapIterator(); iterator.hasNext();) {
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

        return found.isEmpty() ? null : componentRegistry.getComponentInstance(found.get(0));
    }

    Object getComponent(ComponentSpecification componentSpec) {
        return componentSpec == null ? null : getComponent(componentSpec.getComponentKey());
    }

    ComponentSpecification findImplementingComponentSpecification(Class componentType) throws AmbiguousComponentResolutionException {
        List found = new ArrayList();
        for (Iterator iterator = componentRegistry.getRegisteredComponentIterator(); iterator.hasNext();) {
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

    public Object[] getComponents() {
       /* <ASLAK>
        * TODO: make final again
        *
        * There is a reason why we're not simply doing
        *
        * return componentKeyToInstanceMap.values().toArray();
        *
        * getComponents() and getComponentKeys() are tightly related.
        * They have a "contract" between each other. More specifically:
        *
        * 1) They should always return equally sized arrays.
        * 2) For each key returned by getComponentKeys() the call to getComponent(key)
        *    should never return null.
        *
        * If Java had supported DBC, we would have expressed this contract on the PicoContainer
        * interface itself, forcing that contract to be respected through the whole hierarchy.
        * Since this isn't possible in Java, we as programmers use other means (comments and final
        * being some of them) to "enforce" the contract to be respected.
        *
        * Overriding getComponents() and not getComponentType() has the potential danger in that
        * it might violate the contract. Making one of the methods final (that would naturally be
        * getComponents()) and finalising the contract in that final method prevents the contract
        * from being violated. Ever.
        *
        * Using final on methods is a way to avoid contracts being broken.
        *
        * Ideally, this method should be final, so we can avoid the contract being accidentally
        * broken.
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

    //TODO - remove from PicoContainer interface?
    public Object[] getComponentKeys() {
        return componentRegistry.getInstanceMapKeyArray();
    }

    public final boolean hasComponent(Object componentKey) {
        return getComponent(componentKey) != null;
    }

    public ComponentSpecification getComponentSpecification(Object componentKey) {
        for (Iterator iterator = componentRegistry.getRegisteredComponentIterator(); iterator.hasNext();) {
            ComponentSpecification componentSpecification = (ComponentSpecification) iterator.next();
            if (componentSpecification.getComponentKey().equals(componentKey)) {
                return componentSpecification;
            }
        }
        return null;
    }
}
