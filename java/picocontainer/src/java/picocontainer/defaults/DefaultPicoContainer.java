/*****************************************************************************
 * Copyright (Cc) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package picocontainer.defaults;

import picocontainer.ComponentFactory;
import picocontainer.ComponentFactory;
import picocontainer.PicoInstantiationException;
import picocontainer.PicoRegistrationException;
import picocontainer.PicoInstantiationException;
import picocontainer.PicoIntrospectionException;
import picocontainer.ClassRegistrationPicoContainer;

import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.lang.reflect.Modifier;

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
    private AggregateProxyFactory aggregateProxyFactory = new DefaultAggregateProxyFactory();

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

    public final Object[] getComponents() {
        Class[] componentTypes = getComponentTypes();
        Object[] components = new Object[componentTypes.length];
        for (int i = 0; i < componentTypes.length; i++) {
            Class componentType = componentTypes[i];
            components[i] = getComponent(componentType);
        }
        return components;
    }

    /**
     * Shorthand for {@link #getAggregateComponentProxy(boolean, boolean)}(true, true).
     * @return a proxy.
     */
    public Object getAggregateComponentProxy() {
        return getAggregateComponentProxy(true, false);
    }

    /**
     * Returns a proxy that implements the union of all the components'
     * interfaces.
     * Calling a method on the returned Object will call the
     * method on all components in the container that implement
     * that interface.
     *
     * @param callInInstantiationOrder whether to call the methods in the order of instantiation (true) or reverse (false)
     * @param callInInstantiationOrder whether to exclude components registered with {@link #registerComponent(Class, Object)}
     * or {@link #registerComponent(Object)}
     */
    public Object getAggregateComponentProxy(boolean callInInstantiationOrder, boolean callUnmanagedComponents) {
        List aggregateComponents = new ArrayList(orderedComponents);
        if(!callUnmanagedComponents) {
            for (Iterator iterator = unmanagedComponents.iterator(); iterator.hasNext();) {
                aggregateComponents.remove( iterator.next() );
            }
        }
        return aggregateProxyFactory.createAggregateProxy(
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

        registerComponent(componentType, componentImplementation, parameters);
    }

    public void registerComponent(Class componentType, Class componentImplementation, Parameter[] parameters) throws NotConcreteRegistrationException, AssignabilityRegistrationException, DuplicateComponentTypeRegistrationException {
        checkConcrete(componentImplementation);
        checkTypeCompatibility(componentType, componentImplementation);
        checkTypeDuplication(componentType);

        ComponentSpecification compSpec = new ComponentSpecification(componentFactory, componentType, componentImplementation, parameters);
        componentToSpec.put(componentImplementation, compSpec);
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

    public void registerComponent(Object component) throws PicoRegistrationException {
        registerComponent(component.getClass(), component);
    }

    public void registerComponent(Class componentType, Object component) throws PicoRegistrationException {
        checkTypeCompatibility(componentType, component.getClass());
        checkTypeDuplication(componentType);
        //checkImplementationDuplication(component.getClass());
        componentTypeToInstanceMap.put(componentType, component);
        orderedComponents.add(component);
        unmanagedComponents.add(component);
    }

    public void addParameterToComponent(Class componentType, Class parameter, Object arg) throws PicoIntrospectionException {
        ComponentSpecification componentSpec = ((ComponentSpecification) componentToSpec.get(componentType));
        componentSpec.addConstantParameterBasedOnType(componentType, parameter, arg);
    }

    public void registerComponent(Class componentImplementation) throws DuplicateComponentTypeRegistrationException, AssignabilityRegistrationException, NotConcreteRegistrationException, PicoIntrospectionException {
        registerComponent(componentImplementation, componentImplementation);
    }

    public void instantiateComponents() throws PicoInstantiationException, PicoInvocationTargetInitializationException, PicoIntrospectionException {
        if (initialized == false) {
            initializeComponents();
            initialized = true;
        } else {
            throw new IllegalStateException("PicoContainer Started Already");
        }
    }

    // This is Lazy and NOT public :-)
    private void initializeComponents() throws PicoInstantiationException, PicoIntrospectionException {
        for (Iterator iterator = registeredComponents.iterator(); iterator.hasNext();) {
            ComponentSpecification componentSpec = (ComponentSpecification) iterator.next();
            createComponent(componentSpec);
        }
    }

    private Object createComponent(ComponentSpecification componentSpecification) throws PicoInstantiationException, PicoIntrospectionException {
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

    Object createComponent(Class componentType) throws PicoInstantiationException, PicoIntrospectionException {
        Object componentInstance = getComponent(componentType);

        if (componentInstance != null) {
            return componentInstance;
        }

        componentInstance = findSatisfyingComponent(componentType);

        if (componentInstance != null) {
            return componentInstance;
        }

        // try to find components that satisfy the interface (implements the component service asked for)
        ComponentSpecification componentSpecification = findComponentSpecification(componentType);
        if (componentSpecification == null) {
            return null;
        }

        componentType = componentSpecification.getComponentType();

        // if the component does not exist yet, instantiate it lazily
        componentInstance = createComponent(componentSpecification);

        return componentInstance;
    }

    private Object findSatisfyingComponent(Class componentType) throws AmbiguousComponentResolutionException {
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
            throw new AmbiguousComponentResolutionException(ambiguousClasses);
        }

        return found.isEmpty() ? null : componentTypeToInstanceMap.get(found.get(0));
    }

    ComponentSpecification findComponentSpecification(Class componentType) throws AmbiguousComponentResolutionException {
        List found = new ArrayList();
        for (Iterator iterator = registeredComponents.iterator(); iterator.hasNext();) {
            ComponentSpecification componentSpecification = (ComponentSpecification) iterator.next();

            if (componentType.isAssignableFrom(componentSpecification.getComponentType())) {
                found.add(componentSpecification);
            }
        }

        if (found.size() > 1) {
            Class[] ambiguousClass = new Class[found.size()];
            for (int i = 0; i < ambiguousClass.length; i++) {
                ambiguousClass[i] = ((ComponentSpecification) found.get(i)).getComponentImplementation();
            }
            throw new AmbiguousComponentResolutionException(ambiguousClass);
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
