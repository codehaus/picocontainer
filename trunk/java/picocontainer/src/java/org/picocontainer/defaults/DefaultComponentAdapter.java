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

import org.picocontainer.*;

import java.util.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Jon Tirs&eacute;n
 * @version $Revision$
 */
public class DefaultComponentAdapter extends AbstractComponentAdapter {

    private Parameter[] parameters;
    private Object componentInstance;

    /**
     * Explicitly specifies parameters, if null uses default parameters.
     *
     * @param componentKey
     * @param componentImplementation
     * @param parameters
     */
    public DefaultComponentAdapter(final Object componentKey,
                                   final Class componentImplementation,
                                   Parameter[] parameters) throws AssignabilityRegistrationException, NotConcreteRegistrationException {
        super(componentKey, componentImplementation);
        this.parameters = parameters;
    }

    /**
     * Use default parameters.
     *
     * @param componentKey
     * @param componentImplementation
     */
    public DefaultComponentAdapter(Object componentKey,
                                   Class componentImplementation) throws AssignabilityRegistrationException, NotConcreteRegistrationException {
        this(componentKey, componentImplementation, null);
    }

    public Class[] getDependencies(MutablePicoContainer componentRegistry) throws PicoIntrospectionException, AmbiguousComponentResolutionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        Constructor constructor = getConstructor(componentRegistry);
        return constructor.getParameterTypes();
    }

    /**
     * This is now IoC 2.5 compatible.  Multi ctors next.
     * @return
     * @throws org.picocontainer.defaults.TooManySatisfiableConstructorsException
     */
    private Constructor getConstructor(MutablePicoContainer componentRegistry) throws PicoIntrospectionException, NoSatisfiableConstructorsException, AmbiguousComponentResolutionException, AssignabilityRegistrationException, NotConcreteRegistrationException {

        List allConstructors = Arrays.asList(getComponentImplementation().getConstructors());
        List satisfiableConstructors = getSatisfiableConstructors(allConstructors, componentRegistry);

        // now we'll just take the biggest one
        Constructor biggestConstructor = null;
        Set conflicts = new HashSet();
        for (int i = 0; i < satisfiableConstructors.size(); i++) {
            Constructor currentConstructor = (Constructor) satisfiableConstructors.get(i);
            if (biggestConstructor == null) {
                biggestConstructor = currentConstructor;
            } else if (biggestConstructor.getParameterTypes().length < currentConstructor.getParameterTypes().length) {
                conflicts.clear();
                biggestConstructor = currentConstructor;
            } else if (biggestConstructor.getParameterTypes().length == currentConstructor.getParameterTypes().length) {
                conflicts.add(biggestConstructor);
                conflicts.add(currentConstructor);
            }
        }
        if (!conflicts.isEmpty()) {
            throw new TooManySatisfiableConstructorsException(getComponentImplementation(), conflicts);
        }
        return biggestConstructor;
    }

    private List getSatisfiableConstructors(List constructors, MutablePicoContainer componentRegistry) throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        List result = new ArrayList();
        Set failedDependencies = new HashSet();
        for (Iterator iterator = constructors.iterator(); iterator.hasNext();) {
            Constructor constructor = (Constructor) iterator.next();
            Class[] parameterTypes = constructor.getParameterTypes();
            Parameter[] currentParameters = parameters != null ? parameters : createDefaultParameters(parameterTypes);

            boolean failedDependency = false;
            for (int i = 0; i < currentParameters.length; i++) {
                ComponentAdapter adapter = currentParameters[i].resolveAdapter(componentRegistry);
                if (adapter == null) {
                    failedDependency = true;
                    failedDependencies.add(parameterTypes[i]);
                    break;
                } else {
                    // we can't depend on ourself
                    if (adapter.equals(this)) {
                        failedDependency = true;
                        failedDependencies.add(parameterTypes[i]);
                        break;
                    }
                    if (getComponentKey().equals(adapter.getComponentKey())) {
                        failedDependency = true;
                        failedDependencies.add(parameterTypes[i]);
                        break;
                    }
                }
            }
            if (!failedDependency) {
                result.add(constructor);
            }
        }
        if (result.isEmpty()) {
            throw new NoSatisfiableConstructorsException(getComponentImplementation(), failedDependencies);
        }

        return result;
    }

    public Object getComponentInstance(MutablePicoContainer mutablePicoContainer)
            throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        if (componentInstance == null) {
            final Class[] dependencyTypes = getDependencies(mutablePicoContainer);
            final ComponentAdapter[] adapterDependencies = new ComponentAdapter[dependencyTypes.length];

            final Parameter[] componentParameters = getParameters(mutablePicoContainer);

            if (componentParameters.length != adapterDependencies.length) {
                throw new PicoInitializationException() {
                    public String getMessage() {
                        return "The number of specified parameters (" +
                                componentParameters.length + ") doesn't match the number of arguments in the greediest satisfiable constructor (" +
                                adapterDependencies.length + "). When parameters are explicitly specified, specify them in the correct order, and one for each constructor argument." +
                                "The greediest satisfiable constructor takes the following arguments: " + Arrays.asList(dependencyTypes).toString();
                    }
                };
            }
            for (int i = 0; i < adapterDependencies.length; i++) {
                adapterDependencies[i] = componentParameters[i].resolveAdapter(mutablePicoContainer);
            }
            componentInstance = createComponent(adapterDependencies, mutablePicoContainer);

            mutablePicoContainer.addOrderedComponentAdapter(this);

        }
        return componentInstance;
    }

    private Object createComponent(ComponentAdapter[] adapterDependencies, MutablePicoContainer picoContainer) throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        try {
            Constructor constructor = getConstructor(picoContainer);
            Object[] parameters = new Object[adapterDependencies.length];
            for (int i = 0; i < adapterDependencies.length; i++) {
                ComponentAdapter adapterDependency = adapterDependencies[i];
                parameters[i] = adapterDependency.getComponentInstance(picoContainer);
            }

            return constructor.newInstance(parameters);
        } catch (InvocationTargetException e) {
            throw new PicoInvocationTargetInitializationException(e.getCause());
        } catch (InstantiationException e) {
            throw new PicoInvocationTargetInitializationException(e);
        } catch (IllegalAccessException e) {
            throw new PicoInvocationTargetInitializationException(e);
        }
    }

    public static boolean isAssignableFrom(Class actual, Class requested) {
        if (actual == Integer.TYPE || actual == Integer.class) {
            return requested == Integer.TYPE || requested == Integer.class;
        }

        // TODO handle the rest of the primitive types in the same way (Java really sucks concerning this!)

        return actual.isAssignableFrom(requested);
    }

    private Parameter[] getParameters(MutablePicoContainer componentRegistry) throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        if (parameters == null) {
            return createDefaultParameters(getDependencies(componentRegistry));
        } else {
            return parameters;
        }
    }

    public boolean equals(Object object) {
        if (!(object instanceof ComponentAdapter)) {
            return false;
        }
        ComponentAdapter other = (ComponentAdapter) object;

        return getComponentKey().equals(other.getComponentKey()) &&
                getComponentImplementation().equals(other.getComponentImplementation());
    }

    private Parameter[] createDefaultParameters(Class[] parameters) {
        ComponentParameter[] componentParameters = new ComponentParameter[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            componentParameters[i] = new ComponentParameter(parameters[i]);
        }
        return componentParameters;
    }
}
