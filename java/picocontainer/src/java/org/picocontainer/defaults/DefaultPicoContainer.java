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

import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.RegistrationPicoContainer;
import org.picocontainer.extras.CompositeProxyFactory;
import org.picocontainer.internals.*;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;


/**
 * @author Aslak Hellesoy
 * @version $Revision: 1.8 $
 */
public class DefaultPicoContainer implements RegistrationPicoContainer, Serializable {

    private final ComponentRegistry componentRegistry;
    private final ComponentAdapterFactory componentAdapterFactory;

    // Keeps track of unmanaged components - components instantiated outside this internals
    protected List unmanagedComponents = new ArrayList();

    private CompositeProxyFactory compositeProxyFactory = new DefaultCompositeProxyFactory();

    public static class Default extends DefaultPicoContainer {
        public Default() {
            super(new DefaultComponentAdapterFactory(), new DefaultComponentRegistry());
        }
    }

    public static class WithComponentAdapterFactory extends DefaultPicoContainer {
        public WithComponentAdapterFactory(ComponentAdapterFactory componentAdapterFactory) {
            super(componentAdapterFactory, new DefaultComponentRegistry());
        }
    }

    public static class WithComponentRegistry extends DefaultPicoContainer {
        public WithComponentRegistry(ComponentRegistry componentRegistry) {
            super(new DefaultComponentAdapterFactory(), componentRegistry);
        }
    }

    public DefaultPicoContainer(ComponentAdapterFactory componentAdapterFactory, ComponentRegistry componentRegistry) {
        checkNotNull("componentAdapterFactory", componentAdapterFactory);
        checkNotNull("componentRegistry", componentRegistry);
        this.componentAdapterFactory = componentAdapterFactory;
        this.componentRegistry = componentRegistry;
    }

    // TODO: take a MultiCasterFactory as argument. That way we can support
    // multicasters based on reflection (e.g. look for execute() methods) too.
    // Nice for ppl who want lifecycle by following naming conventions
    // on methods instead of implementing interfaces. --Aslak

    public final Object getComponentMulticaster() throws PicoInitializationException {
        return getComponentMulticaster(true, false);
    }

    public final Object getComponentMulticaster(boolean callInInstantiationOrder, boolean callUnmanagedComponents) throws PicoInitializationException {
        getComponents();
        List componentsToMulticast = componentRegistry.getOrderedComponents();
        if (!callUnmanagedComponents) {
            for (Iterator iterator = unmanagedComponents.iterator(); iterator.hasNext();) {
                componentsToMulticast.remove(iterator.next());
            }
        }
        return compositeProxyFactory.createComponentMulticaster(
                getClass().getClassLoader(),
                componentsToMulticast,
                callInInstantiationOrder
        );
    }

    public void registerComponent(Object componentKey, Class componentImplementation) throws DuplicateComponentKeyRegistrationException, AssignabilityRegistrationException, NotConcreteRegistrationException, PicoIntrospectionException {
        checkNotNull("componentKey", componentKey);
        checkNotNull("componentImplementation", componentImplementation);
        checkConcrete(componentImplementation);
        checkTypeCompatibility(componentKey, componentImplementation);
        checkKeyDuplication(componentKey);

        registerComponent(createComponentAdapter(componentKey, componentImplementation, null));
    }

    private void checkNotNull(String name, Object object) {
        if(object == null) {
            throw new NullPointerException(name + " can't be null");
        }
    }

    private ComponentAdapter createComponentAdapter(Object componentKey,
                                                           Class componentImplementation,
                                                           Parameter[] parameters)
            throws PicoIntrospectionException {
        ComponentAdapter adapter = componentAdapterFactory.createComponentAdapter(componentKey, componentImplementation, parameters);
        return adapter;
    }

    public void registerComponent(Object componentKey, Class componentImplementation, Parameter[] parameters) throws NotConcreteRegistrationException, AssignabilityRegistrationException, DuplicateComponentKeyRegistrationException, PicoIntrospectionException {
        checkNotNull("componentKey", componentKey);
        checkNotNull("componentImplementation", componentImplementation);
        checkNotNull("parameters", parameters);
        checkConcrete(componentImplementation);
        checkTypeCompatibility(componentKey, componentImplementation);
        checkKeyDuplication(componentImplementation);

        registerComponent(createComponentAdapter(componentKey, componentImplementation, parameters));
    }

    private void registerComponent(ComponentAdapter componentAdapter) {
        componentRegistry.registerComponent(componentAdapter);
    }

    private void checkKeyDuplication(Object componentKey) throws DuplicateComponentKeyRegistrationException {
        for (Iterator iterator = componentRegistry.getComponentAdapters().iterator(); iterator.hasNext();) {
            Object key = ((ComponentAdapter) iterator.next()).getComponentKey();
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
        if(componentImplementation == null) {
            throw new NullPointerException("componentImplementation cant be null");
        }
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
        registerComponent(new InstanceComponentAdapter(componentKey, component));

        componentRegistry.addOrderedComponentInstance(component);
        unmanagedComponents.add(component);
    }

    public void registerComponentByClass(Class componentImplementation) throws DuplicateComponentKeyRegistrationException, AssignabilityRegistrationException, NotConcreteRegistrationException, PicoIntrospectionException {
        registerComponent(componentImplementation, componentImplementation);
    }

    /**
     * @param componentKey
     */
    public void unregisterComponent(Object componentKey) {
        componentRegistry.unregisterComponent(componentKey);
    }

    /**
     * @param componentKey
     * @return
     */
    public Object getComponent(Object componentKey) throws PicoInitializationException {
        return componentRegistry.getComponentInstance(componentKey);
    }

    public Collection getComponents() throws PicoInitializationException {
        return componentRegistry.getComponentInstances();
    }

    //TODO - remove from PicoContainer interface?
    //TODO - maybe not ?
    public Collection getComponentKeys() {
        return componentRegistry.getComponentKeys();
    }

    public final boolean hasComponent(Object componentKey) {
        return componentRegistry.hasComponentInstance(componentKey);
    }

    public ComponentRegistry getComponentRegistry() {
        return componentRegistry;
    }
}
