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
import org.picocontainer.internals.*;

import java.io.Serializable;
import java.util.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class DefaultComponentAdapter implements Serializable, ComponentAdapter {

    private final Object componentKey;
    private final Class componentImplementation;
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
                                   Parameter[] parameters) {
        this.componentKey = componentKey;
        this.componentImplementation = componentImplementation;
        this.parameters = parameters;
    }

    /**
     * Use default parameters.
     *
     * @param componentKey
     * @param componentImplementation
     */
    public DefaultComponentAdapter(Object componentKey,
                                   Class componentImplementation) {
        this(componentKey, componentImplementation, null);
    }

    protected Parameter[] createDefaultParameters(Class[] parameters) {
        ComponentParameter[] componentParameters = new ComponentParameter[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            componentParameters[i] = new ComponentParameter(parameters[i]);
        }
        return componentParameters;
    }

    public Class[] getDependencies(ComponentRegistry componentRegistry) throws PicoIntrospectionException, AmbiguousComponentResolutionException {
        Constructor constructor = getConstructor(componentRegistry);
        return constructor.getParameterTypes();
    }

    /**
     * This is now IoC 2.5 compatible.  Multi ctors next.
     * @return
     * @throws org.picocontainer.defaults.CannotDecideWhatConstructorToUseException
     */
    private Constructor getConstructor(ComponentRegistry componentRegistry) throws PicoIntrospectionException, NoSatisfiableConstructorsException, AmbiguousComponentResolutionException {

        List allConstructors = Arrays.asList(componentImplementation.getConstructors());
        List satisfiableConstructors = getSatisfiableConstructors(allConstructors, componentRegistry);

        // now we'll just take the biggest one
        Constructor biggestConstructor = null;
        Set conflicts = new HashSet();
        for (int i = 0; i < satisfiableConstructors.size(); i++) {
            Constructor currentConstructor = (Constructor) satisfiableConstructors.get(i);
            if(biggestConstructor == null) {
                biggestConstructor = currentConstructor;
            } else if(biggestConstructor.getParameterTypes().length < currentConstructor.getParameterTypes().length) {
                conflicts.clear();
                biggestConstructor = currentConstructor;
            } else if (biggestConstructor.getParameterTypes().length == currentConstructor.getParameterTypes().length) {
                conflicts.add(biggestConstructor);
                conflicts.add(currentConstructor);
            }
        }
        if(!conflicts.isEmpty()) {
            throw new CannotDecideWhatConstructorToUseException(componentImplementation, conflicts);
        }
        if (biggestConstructor == null) {
            throw new NoSatisfiableConstructorsException(componentImplementation);
        }
        return biggestConstructor;
    }

    private List getSatisfiableConstructors(List constructors, ComponentRegistry componentRegistry) throws AmbiguousComponentResolutionException {
        List result = new ArrayList();
        for (Iterator iterator = constructors.iterator(); iterator.hasNext();) {
            Constructor constructor = (Constructor) iterator.next();
            Class[] parameterTypes = constructor.getParameterTypes();
            Parameter[] currentParameters = parameters != null ? parameters : createDefaultParameters(parameterTypes);

            boolean failedDependency = false;
            for (int i = 0; i < currentParameters.length; i++) {
                ComponentAdapter adapter = currentParameters[i].resolveAdapter(componentRegistry);
                if (adapter == null) {
                    failedDependency = true;
                    break;
                } else {
                    // we can't depend on ourself
                    if(getComponentKey().equals(adapter.getComponentKey())) {
                        failedDependency = true;
                        break;
                    }
                }
            }
            if (!failedDependency) {
                result.add(constructor);
            }
        }
        return result;
    }

    public Object getComponentKey() {
        return componentKey;
    }

    public Class getComponentImplementation() {
        return componentImplementation;
    }

    public Object instantiateComponent(ComponentRegistry componentRegistry)
            throws PicoInitializationException {
        if (componentInstance == null) {
            Class[] dependencyTypes = getDependencies(componentRegistry);
            ComponentAdapter[] adapterDependencies = new ComponentAdapter[dependencyTypes.length];

            Parameter[] componentParameters = getParameters(componentRegistry);
            for (int i = 0; i < adapterDependencies.length; i++) {
                adapterDependencies[i] = componentParameters[i].resolveAdapter(componentRegistry);
            }
            componentInstance = createComponent(adapterDependencies, componentRegistry);

            componentRegistry.addOrderedComponentInstance(componentInstance);

        }
        return componentInstance;
    }

    private Object createComponent(ComponentAdapter[] adapterDependencies, ComponentRegistry componentRegistry) throws PicoInitializationException, CannotDecideWhatConstructorToUseException {
        try {
            Constructor constructor = getConstructor(componentRegistry);
            Object[] parameters = new Object[adapterDependencies.length];
            for (int i = 0; i < adapterDependencies.length; i++) {
                ComponentAdapter adapterDependency = adapterDependencies[i];
                parameters[i] = adapterDependency.instantiateComponent(componentRegistry);
            }

            return constructor.newInstance(parameters);
        } catch (InvocationTargetException e) {
            throw new PicoInvocationTargetInitializationException(e.getCause());
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isAssignableFrom(Class actual, Class requested) {
        if (actual == Integer.TYPE || actual == Integer.class) {
            return requested == Integer.TYPE || requested == Integer.class;
        }

        // TODO handle the rest of the primitive types in the same way (Java really sucks concerning this!)

        return actual.isAssignableFrom(requested);
    }

    private Parameter[] getParameters(ComponentRegistry componentRegistry) {
        if (parameters == null) {
            try {
                return createDefaultParameters(getDependencies(componentRegistry));
            } catch (PicoIntrospectionException e) {
                throw new IllegalStateException("Unable to create default parameters:" + e.getMessage());
            }
        } else {
            return parameters;
        }
    }

    public boolean equals(Object object) {
       ComponentAdapter other = (ComponentAdapter) object;

       return getComponentKey().equals(other.getComponentKey()) &&
                getComponentImplementation().equals(other.getComponentImplementation());
    }
}
