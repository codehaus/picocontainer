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

import picocontainer.ClassRegistrationPicoContainer;
import picocontainer.ComponentFactory;
import picocontainer.PicoInitializationException;
import picocontainer.PicoIntrospectionException;
import picocontainer.PicoRegistrationException;

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
public class DefaultPicoContainer implements ClassRegistrationPicoContainer {

    private final ComponentFactory componentFactory;
    private List registeredComponents = new ArrayList();
    private Map componentTypeToInstanceMap = new HashMap();

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
        Class[] componentTypes = getComponentTypes();
        Object[] components = new Object[componentTypes.length];
        for (int i = 0; i < componentTypes.length; i++) {
            Class componentType = componentTypes[i];
            components[i] = getComponent(componentType);
        }
        return components;
    }

    /**
     * @deprecated Use {@link #getCompositeComponent} instead
     */
    public Object getAggregateComponentProxy() {
        return getCompositeComponent();
    }

    /**
     * @deprecated Use getCompositeComponent instead
     */
    public Object getAggregateComponentProxy(boolean callInInstantiationOrder, boolean callUnmanagedComponents) {
        return getCompositeComponent(callInInstantiationOrder, callUnmanagedComponents);
    }

    // see PicoContainer interface for Javadocs
    public Object getCompositeComponent()
    {
        return getCompositeComponent(true, false);
    }

    // see PicoContainer interface for Javadocs
    public Object getCompositeComponent(boolean callInInstantiationOrder, boolean callUnmanagedComponents)
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

    public void registerComponent(Class componentType, Class componentImplementation) throws DuplicateComponentTypeRegistrationException, AssignabilityRegistrationException, NotConcreteRegistrationException, PicoIntrospectionException {
        checkConcrete(componentImplementation);

        Parameter[] parameters = new Parameter[componentFactory.getDependencies(componentImplementation).length];
        for (int i = 0; i < parameters.length; i++) {
            parameters[i] = createDefaultParameter();
        }

        registerComponent(componentImplementation, componentType, parameters);
    }

    public void registerComponent(Class componentImplementation, Class componentType, Parameter[] parameters) throws NotConcreteRegistrationException, AssignabilityRegistrationException, DuplicateComponentTypeRegistrationException {
        checkConcrete(componentImplementation);
        checkTypeCompatibility(componentType, componentImplementation);
        checkTypeDuplication(componentType);

        registerComponent(new ComponentSpecification(componentFactory, componentType, componentImplementation, parameters));
    }

    private void registerComponent(ComponentSpecification compSpec) {
        componentToSpec.put(compSpec.getComponentImplementation(), compSpec);
        registeredComponents.add(compSpec);
    }

    protected Parameter createDefaultParameter() {
        return new ComponentParameter();
    }

    private void checkTypeDuplication(Class componentType) throws DuplicateComponentTypeRegistrationException {
        for (Iterator iterator = registeredComponents.iterator(); iterator.hasNext();) {
            Class aClass = ((ComponentSpecification) iterator.next()).getComponentType();
            if (aClass == componentType) {
                throw new DuplicateComponentTypeRegistrationException(aClass);
            }
        }
    }

    private void checkTypeCompatibility(Class componentType, Class componentImplementation) throws AssignabilityRegistrationException {
        if (!componentType.isAssignableFrom(componentImplementation)) {
            throw new AssignabilityRegistrationException(componentType, componentImplementation);
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

    public void registerComponent(Class componentType, Object component) throws PicoRegistrationException, PicoIntrospectionException {
        checkTypeCompatibility(componentType, component.getClass());
        checkTypeDuplication(componentType);
        //checkImplementationDuplication(component.getClass());
        registerComponent(new ComponentSpecification(defaultComponentFactory(), componentType, component.getClass(), null));
        componentTypeToInstanceMap.put(componentType, component);
        orderedComponents.add(component);
        unmanagedComponents.add(component);
    }

    private ComponentFactory defaultComponentFactory() {
        return componentFactory;
    }

    public void addParameterToComponent(Class componentType, Class parameter, Object arg) throws PicoIntrospectionException {
        ComponentSpecification componentSpec = ((ComponentSpecification) componentToSpec.get(componentType));
        componentSpec.addConstantParameterBasedOnType(componentType, parameter, arg);
    }

    public void registerComponentByClass(Class componentImplementation) throws DuplicateComponentTypeRegistrationException, AssignabilityRegistrationException, NotConcreteRegistrationException, PicoIntrospectionException {
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
        if (!componentTypeToInstanceMap.containsKey(componentSpecification.getComponentType())) {

            Object component = null;

            // reuse implementation if appropriate
            Set compEntries = componentTypeToInstanceMap.entrySet();
            for (Iterator iterator = compEntries.iterator();
                 iterator.hasNext();) {
                Map.Entry entry = (Map.Entry) iterator.next();
                Object exisitingComp = entry.getValue();
                if (exisitingComp.getClass() == componentSpecification.getComponentImplementation()) {
                    component = exisitingComp;
                    // We can exit now.
                    break;
                }
            }

            // create it if it was not reused
            if (component == null) {
                component = componentSpecification.instantiateComponent(this);
                orderedComponents.add(component);
            }

            componentTypeToInstanceMap.put(componentSpecification.getComponentType(), component);

            return component;
        } else {
            return componentTypeToInstanceMap.get(componentSpecification.getComponentType());
        }
    }

    public Object getComponent(Class componentType) {
        return componentTypeToInstanceMap.get(componentType);
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

        componentType = componentSpecification.getComponentType();

        // if the component does not exist yet, instantiate it lazily
        componentInstance = createComponent(componentSpecification);

        return componentInstance;
    }

    Object findImplementingComponent(Class componentType) throws AmbiguousComponentResolutionException {
        List found = new ArrayList();

        for (Iterator iterator = componentTypeToInstanceMap.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Class candidateType = (Class) entry.getKey();
            if(componentType.isAssignableFrom(candidateType)) {
                found.add(candidateType);
            }
        }

        if (found.size() > 1) {
            Class[] ambiguousClasses = (Class[]) found.toArray(new Class[found.size()]);
            throw new AmbiguousComponentResolutionException(componentType, ambiguousClasses);
        }

        return found.isEmpty() ? null : componentTypeToInstanceMap.get(found.get(0));
    }

    Object getComponent(ComponentSpecification componentSpec) {
        return componentSpec == null ? null : getComponent(componentSpec.getComponentType());
    }

    ComponentSpecification findImplementingComponentSpecification(Class componentType) throws AmbiguousComponentResolutionException {
        List found = new ArrayList();
        for (Iterator iterator = registeredComponents.iterator(); iterator.hasNext();) {
            ComponentSpecification componentSpecification = (ComponentSpecification) iterator.next();

            if (componentType.isAssignableFrom(componentSpecification.getComponentType())) {
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

    public Class[] getComponentTypes() {
        // Get my own
        Set types = componentTypeToInstanceMap.keySet();
        return (Class[]) types.toArray(new Class[types.size()]);
    }

    public final boolean hasComponent(Class componentType) {
        return getComponent(componentType) != null;
    }
}
