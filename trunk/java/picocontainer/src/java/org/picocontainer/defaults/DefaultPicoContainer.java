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

import org.picocontainer.internals.ComponentFactory;
import org.picocontainer.internals.ComponentRegistry;
import org.picocontainer.internals.Parameter;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.RegistrationPicoContainer;
import org.picocontainer.extras.CompositeProxyFactory;
import org.picocontainer.internals.ComponentSpecification;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Collection;


/**
 * Abstract baseclass for various PicoContainer implementations.
 *
 * @author Aslak Hellesoy
 * @version $Revision: 1.8 $
 */
public class DefaultPicoContainer implements RegistrationPicoContainer, Serializable {

    private final ComponentRegistry componentRegistry;
    private final ComponentFactory componentFactory;

    // Keeps track of unmanaged components - components instantiated outside this internals
    protected List unmanagedComponents = new ArrayList();

    private boolean initialized;
    private CompositeProxyFactory compositeProxyFactory = new DefaultCompositeProxyFactory();

    public static class Default extends DefaultPicoContainer {
        public Default() {
            super(new DefaultComponentFactory(), new DefaultComponentRegistry());
        }
    }

    public static class WithComponentFactory extends DefaultPicoContainer {
        public WithComponentFactory(ComponentFactory componentFactory) {
            super(componentFactory, new DefaultComponentRegistry());
        }
    }

    public static class WithComponentRegistry extends DefaultPicoContainer {
        public WithComponentRegistry(ComponentRegistry componentRegistry) {
            super(new DefaultComponentFactory(), componentRegistry);
        }
    }

    public DefaultPicoContainer(ComponentFactory componentFactory, ComponentRegistry componentRegistry) {
        if (componentFactory == null) {
            throw new NullPointerException("componentFactory cannot be null");
        }
        if (componentRegistry == null) {
            throw new NullPointerException("childRegistry cannot be null");
        }
        this.componentFactory = componentFactory;
        this.componentRegistry = componentRegistry;
    }

    // see PicoContainer interface for Javadocs
    public final Object getCompositeComponent() {
        return getCompositeComponent(true, false);
    }

    // see PicoContainer interface for Javadocs
    public final Object getCompositeComponent(boolean callInInstantiationOrder, boolean callUnmanagedComponents) {
        List aggregateComponents = componentRegistry.getOrderedComponents();
        if (!callUnmanagedComponents) {
            for (Iterator iterator = unmanagedComponents.iterator(); iterator.hasNext();) {
                aggregateComponents.remove(iterator.next());
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
        checkKeyDuplication(componentKey);

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
        for (Iterator iterator = componentRegistry.getComponentSpecifications().iterator(); iterator.hasNext();) {
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

	public void unregisterComponent(Object componentKey) {
		componentRegistry.unregisterComponent(componentKey);
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
        for (Iterator iterator = componentRegistry.getComponentSpecifications().iterator(); iterator.hasNext();) {
            componentRegistry.createComponent((ComponentSpecification) iterator.next());
        }
    }


    public Object getComponent(Object componentKey) {
        return componentRegistry.getComponentInstance(componentKey);
    }

    public Collection getComponents() {
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

        return componentRegistry.getComponentInstances();
    }

    //TODO - remove from PicoContainer interface?
    //TODO - maybe not ?
    public Collection getComponentKeys() {
        return componentRegistry.getComponentInstanceKeys();
    }

    public final boolean hasComponent(Object componentKey) {
        return getComponent(componentKey) != null;
    }
}
