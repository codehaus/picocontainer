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

import picocontainer.hierarchical.DuplicateComponentTypeRegistrationException;
import picocontainer.hierarchical.AssignabilityRegistrationException;
import picocontainer.hierarchical.NotConcreteRegistrationException;
import picocontainer.hierarchical.WrongNumberOfConstructorsRegistrationException;
import picocontainer.hierarchical.AmbiguousComponentResolutionException;
import picocontainer.hierarchical.UnsatisfiedDependencyStartupException;
import picocontainer.PicoContainer;
import picocontainer.ComponentFactory;
import picocontainer.PicoRegistrationException;
import picocontainer.PicoInitializationException;
import picocontainer.PicoInvocationTargetInitailizationException;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

/**
 * Abstract baseclass for various PicoContainer implementations.
 *
 * @author Aslak Hellesoy
 * @version $Revision: 1.8 $
 */
public class DefaultPicoContainer implements PicoContainer {

    private final ComponentFactory componentFactory;
    private List registeredComponents = new ArrayList();
    private Map componentTypeToInstanceMap = new HashMap();

    // Keeps track of the instantiation order
    protected List orderedComponents = new ArrayList();

    // Keeps track of unmanaged components - components instantiated outside this container
    protected List unmanagedComponents = new ArrayList();

    private Map parametersForComponent = new HashMap();
    private boolean initialized;

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
    public Object getMultipleInheritanceProxy() {
        return getAggregateComponentProxy( true, true);
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
        return Proxy.newProxyInstance(
                getClass().getClassLoader(),
                getComponentInterfaces(),
                new ComponentsInvocationHandler(callInInstantiationOrder, callUnmanagedComponents));
    }

    private class ComponentsInvocationHandler implements InvocationHandler {
        private boolean callInInstantiationOrder;
        private boolean callUnmanagedComponents;

        public ComponentsInvocationHandler(boolean callInInstantiationOrder, boolean callUnmanagedComponents) {
            this.callInInstantiationOrder = callInInstantiationOrder;
            this.callUnmanagedComponents = callUnmanagedComponents;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            List orderedComponentsCopy = new ArrayList(orderedComponents);

            if( !callInInstantiationOrder ) {
                // reverse the list
                Collections.reverse(orderedComponentsCopy);
            }
            Object[] components = orderedComponentsCopy.toArray();
            return invokeOnComponents(components, method, args);
        }

        private Object invokeOnComponents(Object[] components, Method method, Object[] args) throws IllegalAccessException, InvocationTargetException {
            Object result = null;
            int invokeCount = 0;
            for (int i = 0; i < components.length; i++) {
                Class declarer = method.getDeclaringClass();
                boolean isValidType = declarer.isAssignableFrom(components[i].getClass());
                boolean isUnmanaged = unmanagedComponents.contains(components[i]);
                boolean exclude = !callUnmanagedComponents && isUnmanaged;
                if (isValidType && !exclude) {
                    // It's ok to call the method on this one
                    Object resultCandidate = method.invoke(components[i], args);
                    invokeCount++;
                    if( invokeCount == 1 ) {
                        result = resultCandidate;
                    } else {
                        result = null;
                    }
                }
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
            while( componentClass != null ) {
                Class[] implemeted = componentClass.getInterfaces();
                List implementedList = Arrays.asList(implemeted);
                interfaces.addAll(implementedList);
                componentClass = componentClass.getSuperclass();
            }
        }

        Class[] result = (Class[]) interfaces.toArray(new Class[interfaces.size()]);
        return result;
    }

    public void registerComponent(Class componentType, Class componentImplementation) throws DuplicateComponentTypeRegistrationException, AssignabilityRegistrationException, NotConcreteRegistrationException, WrongNumberOfConstructorsRegistrationException {
        checkConcrete(componentImplementation);
        checkConstructor(componentImplementation);
        checkTypeCompatibility(componentType, componentImplementation);
        checkTypeDuplication(componentType);
        registeredComponents.add(new ComponentSpecification(componentType, componentImplementation));
    }

    private void checkConstructor(Class componentImplementation) throws WrongNumberOfConstructorsRegistrationException {
        // TODO move this check to checkConstructor and rename the exception to
        // WrongNumberOfConstructorsRegistrationException extends PicoRegistrationException
        Constructor[] constructors = componentImplementation.getConstructors();
        if (constructors.length != 1) {
            throw new WrongNumberOfConstructorsRegistrationException(constructors.length);
        }
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

    public void addParameterToComponent(Class componentType, Class parameter, Object arg) {
        if (!parametersForComponent.containsKey(componentType)) {
            parametersForComponent.put(componentType, new ArrayList());
        }
        List args = (List) parametersForComponent.get(componentType);
        args.add(new ParameterSpec(arg));
    }

    public void registerComponent(Class componentImplementation) throws DuplicateComponentTypeRegistrationException, AssignabilityRegistrationException, NotConcreteRegistrationException, WrongNumberOfConstructorsRegistrationException {
        registerComponent(componentImplementation, componentImplementation);
    }

    private class ParameterSpec {
        private Object arg;

        ParameterSpec(Object parameter) {
            this.arg = parameter;
        }
    }

    public void instantiateComponents() throws PicoInitializationException {
        if (initialized == false) {
            initializeComponents();
            checkUnsatisfiedDependencies();
            initialized = true;
        } else {
            throw new IllegalStateException("PicoContainer Started Already");
        }
    }

    // This is Lazy and NOT public :-)
    private void initializeComponents() throws AmbiguousComponentResolutionException, PicoInvocationTargetInitailizationException {
        boolean progress = true;
        while (progress == true) {
            progress = false;

            for (Iterator iterator = registeredComponents.iterator(); iterator.hasNext();) {
                ComponentSpecification componentSpec = (ComponentSpecification) iterator.next();
                Class componentImplementation = componentSpec.getComponentImplementation();
                Class componentType = componentSpec.getComponentType();

                if (componentTypeToInstanceMap.get(componentType) == null) {
                    boolean reused = reuseImplementationIfAppropriate(componentType, componentImplementation);
                    if (reused) {
                        progress = true;
                    } else {
                        // hook'em up
                        progress = hookEmUp(componentImplementation, componentType, progress);
                    }
                }
            }
        }
    }

    protected boolean hookEmUp(Class componentImplementation, Class componentType, boolean progress) throws AmbiguousComponentResolutionException, PicoInvocationTargetInitailizationException {
            Constructor[] constructors = componentImplementation.getConstructors();
            Constructor constructor = constructors[0];
            Class[] parameters = constructor.getParameterTypes();

            List paramSpecs = (List) parametersForComponent.get(componentImplementation);
            paramSpecs = paramSpecs == null ? Collections.EMPTY_LIST : new LinkedList(paramSpecs); // clone because we are going to modify it

            // For each param, look up the instantiated componentImplementation.
            Object[] args = new Object[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                Class param = parameters[i];
                args[i] = getComponentForParam(param); // lookup a service for this param
                if (args[i] == null && !paramSpecs.isEmpty()) { // failing that, check if any params are available from addParameterToComponent()
                    args[i] = ((ParameterSpec) paramSpecs.remove(0)).arg;
                }
            }
            if (hasAnyNullArguments(args) == false) {
                Object componentInstance = null;
                componentInstance = makeComponentInstance(componentType, constructor, args);
                // Put the instantiated comp back in the map
                componentTypeToInstanceMap.put(componentType, componentInstance);
                orderedComponents.add(componentInstance);
                progress = true;
            }

        return progress;
    }

    protected boolean reuseImplementationIfAppropriate(Class componentType, Class componentImplementation) {
        Set compEntries = componentTypeToInstanceMap.entrySet();
        for (Iterator iterator = compEntries.iterator();
             iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Object exisitingCompClass = entry.getValue();
            if (exisitingCompClass.getClass() == componentImplementation) {
                componentTypeToInstanceMap.put(componentType, exisitingCompClass);
                return true;
            }
        }
        return false;
    }

    private void checkUnsatisfiedDependencies() throws UnsatisfiedDependencyStartupException {
        for (Iterator iterator = registeredComponents.iterator(); iterator.hasNext();) {
            ComponentSpecification componentSpecification = (ComponentSpecification) iterator.next();
            Class componentType = componentSpecification.getComponentType();
            if (componentTypeToInstanceMap.get(componentType) == null) {
                throw new UnsatisfiedDependencyStartupException(componentType);
            }
        }
    }

    protected Object makeComponentInstance(Class type, Constructor constructor, Object[] args) throws PicoInvocationTargetInitailizationException {
        return componentFactory.createComponent(type, constructor, args);
    }

    protected Object getComponentForParam(Class parameter) throws AmbiguousComponentResolutionException {
        Object result = null;

        // We're keeping track of all candidate parameters, so we can bomb with a detailed error message
        // if there is ambiguity
        List candidateClasses = new ArrayList();

        for (Iterator iterator = componentTypeToInstanceMap.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Class clazz = (Class) entry.getKey();
            if (parameter.isAssignableFrom(clazz)) {
                candidateClasses.add(clazz);
                result = entry.getValue();
            }
        }

        // We should only have one here.
        if (candidateClasses.size() > 1) {
            Class[] ambiguities = (Class[]) candidateClasses.toArray(new Class[candidateClasses.size()]);
            throw new AmbiguousComponentResolutionException(ambiguities);
        }

        return result;
    }

    private boolean hasAnyNullArguments(Object[] args) {
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg == null) {
                return true;
            }
        }
        return false;
    }

    public Object getComponent(Class componentType) {
        return componentTypeToInstanceMap.get(componentType);
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
