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
import java.util.HashSet;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
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
        return getAggregateComponentProxy(true, true);
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
        return createAggregateProxy(
                getComponentInterfaces(),
                orderedComponents,
                callUnmanagedComponents ? Collections.EMPTY_LIST : unmanagedComponents,
                callInInstantiationOrder
        );
    }

    private Object createAggregateProxy(
            Class[] interfaces,
            List objectsToAggregateCallFor,
            List excludes,
            boolean callInReverseOrder
            ) {
        List copy = new ArrayList(objectsToAggregateCallFor);

        if (!callInReverseOrder) {
            // reverse the list
            Collections.reverse(copy);
        }
        Object[] objects = copy.toArray();

        Object result = Proxy.newProxyInstance(
                getClass().getClassLoader(),
                interfaces,
                new AggregatingInvocationHandler(objects, excludes)
        );

        return result;
    }

    private class AggregatingInvocationHandler implements InvocationHandler {
        private Object[] children;
        private List excludes;

        public AggregatingInvocationHandler(Object[] children, List excludes) {
            this.children = children;
            this.excludes = excludes;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return invokeOnChildren(children, method, args);
        }

        private Object invokeOnChildren(Object[] components, Method method, Object[] args) throws IllegalAccessException, InvocationTargetException {
            // Lazily created list holding all results.
            List results = null;
            for (int i = 0; i < components.length; i++) {
                Class declarer = method.getDeclaringClass();
                boolean isValidType = declarer.isAssignableFrom(components[i].getClass());
                boolean exclude = excludes.contains(components[i]);
                if (isValidType && !exclude) {
                    // It's ok to call the method on this one
                    Object result = method.invoke(components[i], args);
                    if (result != null && !result.getClass().isPrimitive()) {
                        // Only add to the results if the result is not null and isn't a primitive.
                        if (results == null) {
                            results = new ArrayList();
                        }
                        results.add(result);
                    }
                }
            }

            Object result;
            Class returnType = method.getReturnType();

            if (results == null) {
                // Method wasn't called. Return null
                result = null;
            } else if (results.size() == 1) {
                // Got exactly one result. Just return that.
                result = results.get(0);
            } else if (returnType.isInterface()) {
                // We have two or more results
                // We can make a new proxy that aggregates all the results.
                result = createAggregateProxy(
                        new Class[]{returnType},
                        results,
                        excludes,
                        true
                );
            } else {
                // Got m,ultiple results that can't be wrapped in a proxy. Return null.
                result = null;
            }

            return result;
        }
    }

    /**
     * Get all the interfaces implemented by the registered component instances.
     * @return an array of interfaces implemented by the concrete component instances.
     */
    private final Class[] getComponentInterfaces() {
        Set interfaces = new HashSet();
        Object[] components = getComponents();
        for (int i = 0; i < components.length; i++) {
            Class componentClass = components[i].getClass();
            // Strangely enough Class.getInterfaces() does not include the interfaces
            // implemented by superclasses. So we must loop up the hierarchy.
            while (componentClass != null) {
                Class[] implemeted = componentClass.getInterfaces();
                List implementedList = Arrays.asList(implemeted);
                interfaces.addAll(implementedList);
                componentClass = componentClass.getSuperclass();
            }
        }

        Class[] result = (Class[]) interfaces.toArray(new Class[interfaces.size()]);
        return result;
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

    public void instantiateComponents() throws PicoInstantiationException, PicoInvocationTargetInitailizationException, PicoIntrospectionException {
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

    private Object createComponent(ComponentSpecification componentSpec) throws PicoInstantiationException, PicoIntrospectionException {
        if (!componentTypeToInstanceMap.containsKey(componentSpec.getComponentType())) {

            Object component = null;

            // reuse implementation if appropriate
            Set compEntries = componentTypeToInstanceMap.entrySet();
            for (Iterator iterator = compEntries.iterator();
                 iterator.hasNext();) {
                Map.Entry entry = (Map.Entry) iterator.next();
                Object exisitingComp = entry.getValue();
                if (exisitingComp.getClass() == componentSpec.getComponentImplementation()) {
                    component = exisitingComp;
                }
            }

            // create it if it was not reused
            if (component == null) {
                component = componentSpec.instantiateComponent(this);
                orderedComponents.add(component);
            }

            componentTypeToInstanceMap.put(componentSpec.getComponentType(), component);

            return component;
        } else {
            return componentTypeToInstanceMap.get(componentSpec.getComponentType());
        }
    }

    public Object getComponent(Class componentType) {
        return componentTypeToInstanceMap.get(componentType);
    }

    public Object createComponent(Class componentType) throws PicoInstantiationException, PicoIntrospectionException {
        Object comp = getComponent(componentType);

        if (comp != null) {
            return comp;
        }

        comp = findSatisfyingComponent(componentType);

        if (comp != null) {
            return comp;
        }

        // try to find components that satisfy the interface (implements the component service asked for)
        ComponentSpecification componentSpec = findComponentSpecification(componentType);
        if (componentSpec == null) {
            return null;
        }

        componentType = componentSpec.getComponentType();

        // if the component does not exist yet, instantiate it lazily
        createComponent(componentSpec);

        comp = componentTypeToInstanceMap.get(componentType);

        return comp;
    }

    private Object findSatisfyingComponent(Class componentType) throws AmbiguousComponentResolutionException {
        List found = new ArrayList();

        for (Iterator iterator = componentTypeToInstanceMap.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            if (isOfComponentType((Class) entry.getKey(), componentType)) {
                found.add(entry.getKey());
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
            if (isOfComponentType(componentSpecification.getComponentType(), componentType)) {
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

    private boolean isOfComponentType(Class suspected, Class requested) {
        return requested.isAssignableFrom(suspected);
    }

    public Class[] getComponentTypes() {
        // Get my own
        Set types = componentTypeToInstanceMap.keySet();
        return (Class[]) types.toArray(new Class[types.size()]);
    }

    public boolean hasComponent(Class componentType) {
        return getComponent(componentType) != null;
    }
}
