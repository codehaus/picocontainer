/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Original code by Aslak Hellesoy and Paul Hammant                          *
 *****************************************************************************/

package picocontainer;

import java.lang.reflect.*;
import java.util.*;

public class PicoContainerImpl implements PicoContainer {

    private final Container parentContainer;
    private final StartableLifecycleManager startableLifecycleManager;
    private List registeredComponents = new ArrayList();
    private Map componentTypeToInstanceMap = new HashMap();
    private List orderedComps = new ArrayList();

    public PicoContainerImpl(Container parentContainer,
            StartableLifecycleManager startableLifecycleManager) {
        this.parentContainer = parentContainer;
        this.startableLifecycleManager = startableLifecycleManager;
    }

    public static class Default extends PicoContainerImpl {
        public Default() {
            super(null, null);
        }

    }

    public static class WithParentContainer extends PicoContainerImpl {
        public WithParentContainer(Container parentContainer) {
            super(parentContainer, null);
        }
    }

    public static class WithStartableLifecycleManager extends PicoContainerImpl {
        public WithStartableLifecycleManager(StartableLifecycleManager startableLifecycleManager) {
            super(null, startableLifecycleManager);
        }
    }

    public void registerComponent(Class componentClass) throws DuplicateComponentRegistrationException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        registerComponent(componentClass, componentClass);
    }

    public void registerComponent(Class componentType, Class componentClass) throws DuplicateComponentRegistrationException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        checkConcrete(componentClass);
        checkConstructor(componentClass);
        checkType(componentType, componentClass);
        checkDuplication(componentType);
        registeredComponents.add(new ComponentSpecification(componentType, componentClass));
    }

    private void checkConstructor(Class componentClass) {
        // TODO
    }

    private void checkDuplication(Class componentType) throws DuplicateComponentRegistrationException {
        for (Iterator iterator = registeredComponents.iterator(); iterator.hasNext();) {
            Class aClass = ((ComponentSpecification) iterator.next()).getComponentType();
            if (aClass == componentType) {
                throw new DuplicateComponentRegistrationException(aClass);
            }
        }
    }

    private void checkType(Class componentType, Class componentClass) throws AssignabilityRegistrationException {
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
        orderedComps.add(component);
    }

    public void registerComponent(Class componentType, Object component) throws PicoRegistrationException {
        checkType(componentType, component.getClass());
        checkDuplication(componentType);
        componentTypeToInstanceMap.put(componentType, component);
    }

    public void start() throws PicoStartException {
        boolean progress = true;
        while (progress == true) {
            progress = false;

            for (Iterator iterator = registeredComponents.iterator(); iterator.hasNext();) {
                ComponentSpecification componentSpec = (ComponentSpecification) iterator.next();
                Class comp = componentSpec.getComponentClass();
                Class compType = componentSpec.getComponentType();
                if (componentTypeToInstanceMap.get(compType) == null) {
                    // hook'em up
                    try {
                        Constructor[] ctors = comp.getConstructors();

                        // TODO move this check to checkConstructor and rename the exception to
                        // WrongNumberOfConstructorsRegistrationException extends PicoRegistrationException
                        if (ctors.length != 1) {
                            throw new WrongNumberOfConstructorsStartException(ctors.length);
                        }
                        Constructor constructor = ctors[0];
                        Class[] params = constructor.getParameterTypes();

                        // For each param, look up the instantiated comp.
                        Object[] args = new Object[params.length];
                        for (int i = 0; i < params.length; i++) {
                            Class param = params[i];
                            args[i] = getComponentForParam(param);
                        }
                        if (hasNullArgs(args) == false) {
                            Object componentInstance = null;
                            componentInstance = makeComponentInstance(constructor, args);
                            // Put the instantiated comp back in the map
                            componentTypeToInstanceMap.put(compType, componentInstance);
                            orderedComps.add(componentInstance);
                            progress = true;
                        }

                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();  //To change body of catch statement use Options | File Templates.
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
        for (int i = 0; i < orderedComps.size(); i++) {
            Object o = (Object) orderedComps.get(i);
            if (startableLifecycleManager != null) {
                startableLifecycleManager.startComponent(o);
            }
        }
    }

    protected void stopComponents() throws PicoStopException {
        for (int i = orderedComps.size() -1 ; i >= 0 ; i--) {
            Object o = (Object) orderedComps.get(i);
            if (startableLifecycleManager != null) {
                startableLifecycleManager.stopComponent(o);
            }
        }
    }

    private void checkUnsatisfiedDependencies() throws UnsatisfiedDependencyStartupException {
        for (Iterator iterator = registeredComponents.iterator(); iterator.hasNext();) {
            ComponentSpecification componentSpecification = (ComponentSpecification) iterator.next();
            Class componentType = componentSpecification.getComponentType();
            Class componentClass = componentSpecification.getComponentClass();
            if (componentTypeToInstanceMap.get(componentType) == null) {
                throw new UnsatisfiedDependencyStartupException(componentType);
            }
        }
    }

    protected Object makeComponentInstance(Constructor constructor, Object[] args) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        return constructor.newInstance(args);
    }

    private Object getComponentForParam(Class param) throws AmbiguousComponentResolutionException {
        Object result = null;

        // If the parent container has the component type
        // it can be seen to be dominant. No need to check
        // for ambiguities
        if (parentContainer != null && parentContainer.hasComponent(param)) {
            return parentContainer.getComponent(param);
        }

        // We're keeping track of all candidate parameters, so we can bomb with a detailed error message
        // if there is ambiguity
        List candidateClasses = new ArrayList();

        for (Iterator iterator = componentTypeToInstanceMap.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Class clazz = (Class) entry.getKey();
            if (param.isAssignableFrom(clazz)) {
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

    public Object getProxy(InvocationHandler invocationHandler) {
        // TODO should fail if container isn't started. Throw checked exception?
        Set interfaces = new HashSet();
        for (Iterator iterator = orderedComps.iterator(); iterator.hasNext();) {
            Class componentClass = iterator.next().getClass();
            Class[] implemeted = componentClass.getInterfaces();
            List implementedList = Arrays.asList(implemeted);
            interfaces.addAll(implementedList);
        }

        Class[] interfaceArray = (Class[]) interfaces.toArray(new Class[interfaces.size()]);
        return Proxy.newProxyInstance( getClass().getClassLoader(), interfaceArray, invocationHandler);
    }
}
