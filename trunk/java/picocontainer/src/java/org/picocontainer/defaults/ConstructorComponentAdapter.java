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

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.MutablePicoContainer;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Instantiates components using IoC type 3. That means trying to
 * pass the constructor arguments created by other ComponentAdapters
 * in the container.
 * {@inheritDoc}
 *
 * @author Jon Tirs&eacute;n
 * @author Aslak Helles&oslash;y
 * @author Zohar Melamed
 * @version $Revision$
 */
public class ConstructorComponentAdapter extends InstantiatingComponentAdapter {

    private List satisfiableConstructorsCache;
    private Constructor greediestConstructor;

    /**
     * Explicitly specifies parameters, if null uses default parameters.
     *
     * @param componentKey
     * @param componentImplementation
     * @param parameters
     */
    public ConstructorComponentAdapter(final Object componentKey,
                                     final Class componentImplementation,
                                     Parameter[] parameters) throws AssignabilityRegistrationException, NotConcreteRegistrationException {
        super(componentKey, componentImplementation, parameters);
    }

    /**
     * Use default parameters.
     *
     * @param componentKey
     * @param componentImplementation
     */
    public ConstructorComponentAdapter(Object componentKey,
                                     Class componentImplementation) throws AssignabilityRegistrationException, NotConcreteRegistrationException {
        this(componentKey, componentImplementation, null);
    }

    public Class[] getDependencies(PicoContainer picoContainer) throws PicoIntrospectionException, AmbiguousComponentResolutionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        Constructor constructor = getConstructor(picoContainer);
        return constructor.getParameterTypes();
    }

    /**
     * @return The greediest satisfiable constructor
     */
    protected Constructor getConstructor(PicoContainer picoContainer) throws PicoIntrospectionException, NoSatisfiableConstructorsException, AmbiguousComponentResolutionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        if (greediestConstructor == null) {
            List allConstructors = Arrays.asList(getComponentImplementation().getConstructors());
            List satisfiableConstructors = getSatisfiableConstructors(allConstructors, picoContainer);

            // now we'll just take the biggest one
            greediestConstructor = null;
            Set conflicts = new HashSet();
            for (int i = 0; i < satisfiableConstructors.size(); i++) {
                Constructor currentConstructor = (Constructor) satisfiableConstructors.get(i);
                if (greediestConstructor == null) {
                    greediestConstructor = currentConstructor;
                } else if (greediestConstructor.getParameterTypes().length < currentConstructor.getParameterTypes().length) {
                    conflicts.clear();
                    greediestConstructor = currentConstructor;
                } else if (greediestConstructor.getParameterTypes().length == currentConstructor.getParameterTypes().length) {
                    conflicts.add(greediestConstructor);
                    conflicts.add(currentConstructor);
                }
            }
            if (!conflicts.isEmpty()) {
                throw new TooManySatisfiableConstructorsException(getComponentImplementation(), conflicts);
            }
        }
        return greediestConstructor;
    }

    private List getSatisfiableConstructors(List constructors, PicoContainer picoContainer) throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        if (satisfiableConstructorsCache == null) {
            satisfiableConstructorsCache = new ArrayList();
            Set failedDependencies = new HashSet();
            for (Iterator iterator = constructors.iterator(); iterator.hasNext();) {
                boolean failedDependency = false;
                Constructor constructor = (Constructor) iterator.next();
                Class[] parameterTypes = constructor.getParameterTypes();
                Parameter[] currentParameters = parameters != null ? parameters : createDefaultParameters(parameterTypes);

                for (int i = 0; i < currentParameters.length; i++) {
                    ComponentAdapter adapter = currentParameters[i].resolveAdapter(picoContainer);
                    if (adapter == null) {
                        failedDependency = true;
                        failedDependencies.add(parameterTypes[i]);
                    } else {
                        // we can't depend on ourself
                        if (adapter.equals(this)) {
                            failedDependency = true;
                            failedDependencies.add(parameterTypes[i]);
                        }
                        if (getComponentKey().equals(adapter.getComponentKey())) {
                            failedDependency = true;
                            failedDependencies.add(parameterTypes[i]);
                        }
                    }
                }
                if (!failedDependency) {
                    satisfiableConstructorsCache.add(constructor);
                }
            }
            if (satisfiableConstructorsCache.isEmpty()) {
                throw new NoSatisfiableConstructorsException(getComponentImplementation(), failedDependencies);
            }
        }

        return satisfiableConstructorsCache;
    }

    protected Object[] getConstructorArguments(ComponentAdapter[] adapterDependencies, MutablePicoContainer picoContainer) {
        Object[] result = new Object[adapterDependencies.length];
        for (int i = 0; i < adapterDependencies.length; i++) {
            ComponentAdapter adapterDependency = adapterDependencies[i];
            result[i] = adapterDependency.getComponentInstance(picoContainer);
        }
        return result;
    }

    // TODO: remove? Only used from ParameterTestCase
    public static boolean isAssignableFrom(Class actual, Class requested) {
        if (actual == Character.TYPE || actual == Character.class) {
            return requested == Character.TYPE || requested == Character.class;
        }
        if (actual == Double.TYPE || actual == Double.class) {
            return requested == Double.TYPE || requested == Double.class;
        }
        if (actual == Float.TYPE || actual == Float.class) {
            return requested == Float.TYPE || requested == Float.class;
        }
        if (actual == Integer.TYPE || actual == Integer.class) {
            return requested == Integer.TYPE || requested == Integer.class;
        }
        if (actual == Long.TYPE || actual == Long.class) {
            return requested == Long.TYPE || requested == Long.class;
        }
        if (actual == Short.TYPE || actual == Short.class) {
            return requested == Short.TYPE || requested == Short.class;
        }
        if (actual == Byte.TYPE || actual == Byte.class) {
            return requested == Byte.TYPE || requested == Byte.class;
        }
        if (actual == Boolean.TYPE || actual == Boolean.class) {
            return requested == Boolean.TYPE || requested == Boolean.class;
        }
        return actual.isAssignableFrom(requested);
    }

    public boolean equals(Object object) {
        if (!(object instanceof ComponentAdapter)) {
            return false;
        }
        ComponentAdapter other = (ComponentAdapter) object;

        return getComponentKey().equals(other.getComponentKey()) &&
                getComponentImplementation().equals(other.getComponentImplementation());
    }

}
