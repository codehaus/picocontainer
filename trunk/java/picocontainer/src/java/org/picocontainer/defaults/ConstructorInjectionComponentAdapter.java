/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
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
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Arrays;

/**
 * {@inheritDoc}
 * Instantiates components using Constructor Injection.
 * <p>
 * <em>
 * Note that this class doesn't cache instances. If you want caching,
 * use a {@link CachingComponentAdapter} around this one.
 * </em>
 *
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Jon Tirs&eacute;n
 * @author Zohar Melamed
 * @author J&ouml;rg Schaible
 * @version $Revision$
 */
public class ConstructorInjectionComponentAdapter extends InstantiatingComponentAdapter {
    private transient boolean instantiating;
    private transient boolean verifying;
    private transient List sortedMatchingConstructors;

    /**
     * Explicitly specifies parameters, if null uses default parameters.
     * {@inheritDoc}
     */
    public ConstructorInjectionComponentAdapter(final Object componentKey,
                                       final Class componentImplementation,
                                       Parameter[] parameters) throws AssignabilityRegistrationException, NotConcreteRegistrationException {
        super(componentKey, componentImplementation, parameters);
        // prepare adapter independent stuff
        sortedMatchingConstructors = getSortedMatchingConstructors();
    }

    /**
     * Use default parameters.
     * {@inheritDoc}
     */
    public ConstructorInjectionComponentAdapter(Object componentKey,
                                       Class componentImplementation) throws AssignabilityRegistrationException, NotConcreteRegistrationException {
        this(componentKey, componentImplementation, null);
    }

    public void verify() throws UnsatisfiableDependenciesException {
        try {
            List adapterDependencies = new ArrayList();
            getGreediestSatisifableConstructor(adapterDependencies);
            if (verifying) {
                throw new CyclicDependencyException(getDependencyTypes(adapterDependencies));
            }
            verifying = true;
            for (int i = 0; i < adapterDependencies.size(); i++) {
                ComponentAdapter adapterDependency = (ComponentAdapter) adapterDependencies.get(i);
                adapterDependency.verify();
            }
        } finally {
            verifying = false;
        }
    }

    protected Constructor getGreediestSatisifableConstructor(List adapterDependencies) throws PicoIntrospectionException, UnsatisfiableDependenciesException, AmbiguousComponentResolutionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        Constructor greediestConstructor = null;
        final Set conflicts = new HashSet();
        final Set unsatisfiableDependencyTypes = new HashSet();
        for (int i = 0; i < sortedMatchingConstructors.size(); i++) {
            List dependencies = new ArrayList();
            boolean failedDependency = false;
            Constructor constructor = (Constructor) sortedMatchingConstructors.get(i);
            Class[] parameterTypes = constructor.getParameterTypes();
            Parameter[] currentParameters = parameters != null ? parameters : createDefaultParameters(parameterTypes);

            // remember: all constructors with less arguments as the given parameters are filtered out already
            for (int j = 0; j < currentParameters.length; j++) {
                ComponentAdapter adapter = currentParameters[j].resolveAdapter(getContainer(), parameterTypes[j]);
                if (adapter == null) {
                    failedDependency = true;
                    unsatisfiableDependencyTypes.add(Arrays.asList(parameterTypes));
                } else {
                    // we can't depend on ourself
                    if (adapter.equals(this)) {
                        failedDependency = true;
                        unsatisfiableDependencyTypes.add(Arrays.asList(parameterTypes));
                    } else if (getComponentKey().equals(adapter.getComponentKey())) {
                        failedDependency = true;
                        unsatisfiableDependencyTypes.add(Arrays.asList(parameterTypes));
                    } else {
                        dependencies.add(adapter);
                    }
                }
            }
            if (!failedDependency) {
                if(conflicts.size() == 0 && greediestConstructor == null) {
                    greediestConstructor = constructor;
                    adapterDependencies.addAll(dependencies);
                } else if (conflicts.size() == 0 && greediestConstructor.getParameterTypes().length > parameterTypes.length) {
                    // remember: we're sorted by length, therefore we've already found the optimal constructor
                    break;
                } else {
                    if (greediestConstructor != null) {
                    	conflicts.add(greediestConstructor);
                        greediestConstructor = null;
                    }
                    conflicts.add(constructor);
                    adapterDependencies.clear();
                }
            }
        }
        if (!conflicts.isEmpty()) {
            throw new TooManySatisfiableConstructorsException(getComponentImplementation(), conflicts);
        }
        if (greediestConstructor == null && unsatisfiableDependencyTypes.size() > 0) {
            throw new UnsatisfiableDependenciesException(this, unsatisfiableDependencyTypes);
        }
        if (greediestConstructor == null) {
            // be nice to the user, show all constructors that were filtered out 
            final Set nonMatching = new HashSet();
            final Constructor[] constructors = getComponentImplementation().getConstructors();
            for (int i = 0; i < constructors.length; i++) {
                if (!sortedMatchingConstructors.contains(constructors[i])) {
                    nonMatching.add(constructors[i]);
                }
            }
            throw new PicoInitializationException("The specified parameters do not match any of the following constructors: " + nonMatching.toString());
        }
        return greediestConstructor;
    }

    protected Object[] getConstructorArguments(ComponentAdapter[] adapterDependencies) {
        Object[] result = new Object[adapterDependencies.length];
        for (int i = 0; i < adapterDependencies.length; i++) {
            ComponentAdapter adapterDependency = adapterDependencies[i];
            result[i] = adapterDependency.getComponentInstance();
        }
        return result;
    }

    protected Object instantiateComponent(List adapterDependencies) throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        try {
            Constructor constructor = getGreediestSatisifableConstructor(adapterDependencies);
            if (instantiating) {
                throw new CyclicDependencyException(constructor.getParameterTypes());
            }
            instantiating = true;
            Object[] parameters = getConstructorArguments(adapterDependencies);

            return constructor.newInstance(parameters);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof RuntimeException) {
                throw (RuntimeException) e.getTargetException();
            } else if (e.getTargetException() instanceof Error) {
                throw (Error) e.getTargetException();
            }
            throw new PicoInvocationTargetInitializationException(e.getTargetException());
        } catch (InstantiationException e) {
            throw new PicoInvocationTargetInitializationException(e);
        } catch (IllegalAccessException e) {
            throw new PicoInvocationTargetInitializationException(e);
        } finally {
            instantiating = false;
        }
    }
    
    private Class[] getDependencyTypes(List adapterDependencies) {
        Class[] result = new Class[adapterDependencies.size()];
        for (int i = 0; i < adapterDependencies.size(); i++) {
            ComponentAdapter adapterDependency = (ComponentAdapter) adapterDependencies.get(i);
            result[i] = adapterDependency.getComponentImplementation();
        }
        return result;
    }

    protected Object[] getConstructorArguments(List adapterDependencies) {
        Object[] result = new Object[adapterDependencies.size()];
        for (int i = 0; i < adapterDependencies.size(); i++) {
            ComponentAdapter adapterDependency = (ComponentAdapter) adapterDependencies.get(i);
            result[i] = adapterDependency.getComponentInstance();
        }
        return result;
    }
    
    private List getSortedMatchingConstructors() {
        List matchingConstructors = new ArrayList();
        Constructor[] allConstructors = getComponentImplementation().getConstructors();
        // filter out all constructors that will definately not match 
        for (int i = 0; i < allConstructors.length; i++) {
            Constructor constructor = allConstructors[i];
            if (parameters == null || constructor.getParameterTypes().length == parameters.length) {
                matchingConstructors.add(constructor);
            }
        }
        // optimize list of constructors moving the longest at the beginning
        if (parameters == null) {
            Collections.sort(matchingConstructors, new Comparator() {
                public int compare(Object arg0, Object arg1) {
                    return ((Constructor)arg1).getParameterTypes().length - ((Constructor)arg0).getParameterTypes().length;
                }
            });
        }
        return matchingConstructors;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
    }
    
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        // we must initialize ourselves again
        sortedMatchingConstructors = getSortedMatchingConstructors();
    }
}
