/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

/*
TODO (Aslak):

1) Factor out a DependencyAnalyzer:
   public interface DependencyAnalyzer {
	   InstantiationSpecification[] getOrderedInstantiationSpecifications();
   }

   ConstructorDependencyAnalyzer would emerge from refactoring this class.

2) Refactor the ContainerFactory's createComponent method to take a
   InstantiationSpecification argument. This class/intf should contain'
   everything needed to instantiate a component.

*/

package picocontainer.hierarchical;


import picocontainer.AbstractContainer;
import picocontainer.ClassRegistrationPicoContainer;
import picocontainer.ComponentFactory;
import picocontainer.PicoContainer;
import picocontainer.PicoInitializationException;
import picocontainer.PicoInvocationTargetInitailizationException;
import picocontainer.PicoRegistrationException;
import picocontainer.defaults.DefaultComponentFactory;
import picocontainer.defaults.NullContainer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class HierarchicalPicoContainer extends AbstractContainer implements ClassRegistrationPicoContainer {

    private final PicoContainer parentContainer;
    private final ComponentFactory componentFactory;
    private List registeredComponents = new ArrayList();
    private Map componentTypeToInstanceMap = new HashMap();

    // Keeps track of the instantiation order
    protected List orderedComponents = new ArrayList();

    // Keeps track of unmanaged components - components instantiated outside this container
    protected List unmanagedComponents = new ArrayList();

    private Map parametersForComponent = new HashMap();
    private boolean initialized;

    public HierarchicalPicoContainer(PicoContainer parentContainer,
                                     ComponentFactory componentFactory) {
        if (parentContainer == null) {
            throw new NullPointerException("parentContainer cannot be null");
        }
        if (componentFactory == null) {
            throw new NullPointerException("componentFactory cannot be null");
        }
        this.parentContainer = parentContainer;
        this.componentFactory = componentFactory;
    }

    public static class Default extends HierarchicalPicoContainer {
        public Default() {
            super(new NullContainer(), new DefaultComponentFactory());
        }

    }

    public static class WithParentContainer extends HierarchicalPicoContainer {
        public WithParentContainer(PicoContainer parentContainer) {
            super(parentContainer, new DefaultComponentFactory());
        }
    }

    public static class WithComponentFactory extends HierarchicalPicoContainer {
        public WithComponentFactory(ComponentFactory componentFactory) {
            super(new NullContainer(), componentFactory);
        }
    }

    public void registerComponent(Class componentImplementation) throws DuplicateComponentTypeRegistrationException, AssignabilityRegistrationException, NotConcreteRegistrationException, WrongNumberOfConstructorsRegistrationException {
        registerComponent(componentImplementation, componentImplementation);
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

    private class ParameterSpec {
        private Object arg;

        ParameterSpec(Object parameter) {
            this.arg = parameter;
        }
    }

    public void initializeContainer() throws PicoInitializationException {
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

    private Object getComponentForParam(Class parameter) throws AmbiguousComponentResolutionException {
        Object result = null;

        // If the parent container has the component type
        // it can be seen to be dominant. No need to check
        // for ambiguities
        if (parentContainer.hasComponent(parameter)) {
            return parentContainer.getComponent(parameter);
        }

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
        Object result = componentTypeToInstanceMap.get(componentType);
        if (result == null) {
            result = parentContainer.getComponent(componentType);
        }
        return result;
    }

    public Class[] getComponentTypes() {
        // Get my own
        Set types = new HashSet(componentTypeToInstanceMap.keySet());

        // Get those from my parent.
        types.addAll(Arrays.asList(parentContainer.getComponentTypes()));

        return (Class[]) types.toArray(new Class[types.size()]);
    }

    public boolean hasComponent(Class componentType) {
        return getComponent(componentType) != null;
    }

}
