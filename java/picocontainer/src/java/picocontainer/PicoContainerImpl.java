/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package picocontainer;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;

public class PicoContainerImpl implements PicoContainer {

    private final Container parentContainer;
    private final StartableLifecycleManager startableLifecycleManager;
    private final ComponentFactory componentFactory;
    private List registeredComponents = new ArrayList();
    private Map componentTypeToInstanceMap = new HashMap();
    private List orderedComponents = new ArrayList();

    public PicoContainerImpl(Container parentContainer,
            StartableLifecycleManager startableLifecycleManager,
            ComponentFactory proxyFactory) {
        if (parentContainer == null) {
            throw new NullPointerException("parentContainer cannot be null");
        }
        if (startableLifecycleManager == null) {
            throw new NullPointerException("startableLifecycleManager cannot be null");
        }
        if (proxyFactory == null) {
            throw new NullPointerException("componentFactory cannot be null");
        }
        this.parentContainer = parentContainer;
        this.startableLifecycleManager = startableLifecycleManager;
        this.componentFactory = proxyFactory;
    }

    public static class Default extends PicoContainerImpl {
        public Default() {
            super(new DummyContainer(), new DummyStartableLifecycleManager(), new DefaultComponentFactory());
        }

    }

    public static class WithParentContainer extends PicoContainerImpl {
        public WithParentContainer(Container parentContainer) {
            super(parentContainer, new DummyStartableLifecycleManager(), new DefaultComponentFactory());
        }
    }

    public static class WithStartableLifecycleManager extends PicoContainerImpl {
        public WithStartableLifecycleManager(StartableLifecycleManager startableLifecycleManager) {
            super(new DummyContainer(), startableLifecycleManager, new DefaultComponentFactory());
        }
    }

    public static class WithComponentFactory extends PicoContainerImpl {
        public WithComponentFactory(ComponentFactory componentFactory) {
            super(new DummyContainer(), new DummyStartableLifecycleManager(), componentFactory);
        }
    }

    // TODO:ASLAK just declare PicoRegistrationException?
    // NOT-TODO:PAUL On interface spec yes, on impl no. The tightly-coupled (to impl) user will thank you :-)
    public void registerComponent(Class componentClass) throws DuplicateComponentTypeRegistrationException, AssignabilityRegistrationException, NotConcreteRegistrationException, WrongNumberOfConstructorsRegistrationException, DuplicateComponentClassRegistrationException {
        registerComponent(componentClass, componentClass);
    }

    // TODO:ASLAK just declare PicoRegistrationException?
    // NOT-TODO:PAUL On interface spec yes, on impl no. The tightly-coupled (to impl) user will thank you :-)
    public void registerComponent(Class componentType, Class componentClass) throws DuplicateComponentTypeRegistrationException, AssignabilityRegistrationException, NotConcreteRegistrationException, WrongNumberOfConstructorsRegistrationException, DuplicateComponentClassRegistrationException {
        checkConcrete(componentClass);
        checkConstructor(componentClass);
        checkTypeCompatibility(componentType, componentClass);
        checkTypeDuplication(componentType);
        checkImplementationDuplication(componentClass);
        registeredComponents.add(new ComponentSpecification(componentType, componentClass));
    }

    private void checkConstructor(Class componentClass) throws WrongNumberOfConstructorsRegistrationException {
        // TODO move this check to checkConstructor and rename the exception to
        // WrongNumberOfConstructorsRegistrationException extends PicoRegistrationException
        Constructor[] constructors = componentClass.getConstructors();
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

    // todo rename componentClass to componentImplementation everywhere
    private void checkImplementationDuplication(Class componentImplementation) throws DuplicateComponentClassRegistrationException {
        for (Iterator iterator = registeredComponents.iterator(); iterator.hasNext();) {
            Class aClass = ((ComponentSpecification) iterator.next()).getComponentClass();
            if (aClass == componentImplementation) {
                throw new DuplicateComponentClassRegistrationException(aClass);
            }
        }
    }

    private void checkTypeCompatibility(Class componentType, Class componentClass) throws AssignabilityRegistrationException {
        if (!componentType.isAssignableFrom(componentClass)) {
            throw new AssignabilityRegistrationException(componentType, componentClass);
        }
    }

    private void checkConcrete(Class componentClass) throws NotConcreteRegistrationException {
        // Assert that the component class is concrete.
        boolean isAbstract = (componentClass.getModifiers() & Modifier.ABSTRACT) == Modifier.ABSTRACT;
        if (componentClass.isInterface() || isAbstract ) {
            throw new NotConcreteRegistrationException(componentClass);
        }
    }

    public void registerComponent(Object component) throws PicoRegistrationException {
        registerComponent(component.getClass(), component);
        orderedComponents.add(component);
    }

    public void registerComponent(Class componentType, Object component) throws PicoRegistrationException {
        checkTypeCompatibility(componentType, component.getClass());
        checkTypeDuplication(componentType);
        checkImplementationDuplication(component.getClass());
        componentTypeToInstanceMap.put(componentType, component);
    }

    public void start() throws PicoStartException {
        boolean progress = true;
        while (progress == true) {
            progress = false;

            for (Iterator iterator = registeredComponents.iterator(); iterator.hasNext();) {
                ComponentSpecification componentSpec = (ComponentSpecification) iterator.next();
                Class componentClass = componentSpec.getComponentClass();
                Class componentType = componentSpec.getComponentType();
                if (componentTypeToInstanceMap.get(componentType) == null) {
                    // hook'em up
                    try {
                        Constructor[] constructors = componentClass.getConstructors();
                        Constructor constructor = constructors[0];
                        Class[] parameters = constructor.getParameterTypes();

                        // For each param, look up the instantiated componentClass.
                        Object[] args = new Object[parameters.length];
                        for (int i = 0; i < parameters.length; i++) {
                            Class param = parameters[i];
                            args[i] = getComponentForParam(param);
                        }
                        if (hasNullArgs(args) == false) {
                            Object componentInstance = null;
                         componentInstance = makeComponentInstance(componentType, constructor, args);
                            // Put the instantiated comp back in the map
                            componentTypeToInstanceMap.put(componentType, componentInstance);
                         orderedComponents.add(componentInstance);
                            progress = true;
                        }

                    } catch (InvocationTargetException e) {
                        throw new PicoInvocationTargetStartException(e);
                    } catch (InstantiationException e) {
                        e.printStackTrace();  //To change body of catch statement use Options | File Templates.
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();  //To change body of catch statement use Options | File Templates.
                    }
                }
            }
        }

        checkUnsatisfiedDependencies();

        startComponents();

    }

    public void stop() throws PicoStopException {
        stopComponents();
    }

    protected void startComponents() throws PicoStartException {
        for (int i = 0; i < orderedComponents.size(); i++) {
            Object component = orderedComponents.get(i);
            startableLifecycleManager.startComponent(component);
        }
    }

    protected void stopComponents() throws PicoStopException {
        for (int i = orderedComponents.size() -1 ; i >= 0 ; i--) {
            Object component = orderedComponents.get(i);
            startableLifecycleManager.stopComponent(component);
        }
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

    protected Object makeComponentInstance(Class type, Constructor constructor, Object[] args)
            throws InstantiationException, IllegalAccessException, InvocationTargetException {
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

    public Object getComponent(Class compType) {
        return componentTypeToInstanceMap.get(compType);
    }

    public Object[] getComponents() {
        return componentTypeToInstanceMap.values().toArray();

    }

    private boolean hasNullArgs(Object[] args) {
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg == null) {
                return true;
            }
        }
        return false;
    }

    public boolean hasComponent(Class compType) {
        return componentTypeToInstanceMap.get(compType) != null;
    }

}
