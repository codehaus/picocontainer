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

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Instantiates components using Constructor Injection.
 * <p/>
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
    private transient List sortedMatchingConstructors;

    /**
     * Explicitly specifies parameters. If parameters are null, default parameters
     * will be used.
     */
    public ConstructorInjectionComponentAdapter(final Object componentKey,
                                                final Class componentImplementation,
                                                Parameter[] parameters,
                                                boolean allowNonPublicClasses) throws AssignabilityRegistrationException, NotConcreteRegistrationException {
        super(componentKey, componentImplementation, parameters, allowNonPublicClasses);
    }

    public ConstructorInjectionComponentAdapter(Object componentKey, Class componentImplementation, Parameter[] parameters) {
        this(componentKey, componentImplementation, parameters, false);
    }

    /**
     * Use default parameters.
     */
    public ConstructorInjectionComponentAdapter(Object componentKey,
                                                Class componentImplementation) throws AssignabilityRegistrationException, NotConcreteRegistrationException {
        this(componentKey, componentImplementation, null);
    }

    protected Constructor getGreediestSatisifableConstructor(List adapterInstantiationOrderTrackingList) throws PicoIntrospectionException, UnsatisfiableDependenciesException, AmbiguousComponentResolutionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        Constructor greediestConstructor = null;
        final Set conflicts = new HashSet();
        final Set unsatisfiableDependencyTypes = new HashSet();
        if (sortedMatchingConstructors == null) {
            sortedMatchingConstructors = getSortedMatchingConstructors();
        }
        for (int i = 0; i < sortedMatchingConstructors.size(); i++) {
            List adapterDependencies = new ArrayList();
            boolean failedDependency = false;
            Constructor constructor = (Constructor) sortedMatchingConstructors.get(i);
            Class[] parameterTypes = constructor.getParameterTypes();
            Parameter[] currentParameters = parameters != null ? parameters : createDefaultParameters(parameterTypes);
//            Type[] genericParameterTypes = constructor.getGenericParameterTypes();

            // remember: all constructors with less arguments than the given parameters are filtered out already
            for (int j = 0; j < currentParameters.length; j++) {
                ComponentAdapter adapter = currentParameters[j].resolveAdapter(getContainer(), parameterTypes[j]);
                if (adapter == null) {
                    // perhaps it is an array or a generic collection
                    if (parameterTypes[j].getComponentType() != null) {
                        ComponentAdapter genericCollectionComponentAdapter = getGenericCollectionComponentAdapter(parameterTypes[j].getComponentType());
                        if (genericCollectionComponentAdapter != null) {
                            genericCollectionComponentAdapter.setContainer(getContainer());
                            adapterDependencies.add(genericCollectionComponentAdapter);
                        } else {
                            failedDependency = true;
                            unsatisfiableDependencyTypes.add(Arrays.asList(parameterTypes));
                        }
                    } else {
                        failedDependency = true;
                        unsatisfiableDependencyTypes.add(Arrays.asList(parameterTypes));
                    }
                } else {
                    // we can't depend on ourself
                    if (adapter.equals(this)) {
                        failedDependency = true;
                        unsatisfiableDependencyTypes.add(Arrays.asList(parameterTypes));
                    } else if (getComponentKey().equals(adapter.getComponentKey())) {
                        failedDependency = true;
                        unsatisfiableDependencyTypes.add(Arrays.asList(parameterTypes));
                    } else {
                        adapterDependencies.add(adapter);
                    }
                }
            }
            if (!failedDependency) {
                if (conflicts.size() == 0 && greediestConstructor == null) {
                    greediestConstructor = constructor;
                    adapterInstantiationOrderTrackingList.addAll(adapterDependencies);
                } else if (conflicts.size() == 0 && greediestConstructor.getParameterTypes().length > parameterTypes.length) {
                    // remember: we're sorted by length, therefore we've already found the optimal constructor
                    break;
                } else {
                    if (greediestConstructor != null) {
                        conflicts.add(greediestConstructor);
                        greediestConstructor = null;
                    }
                    conflicts.add(constructor);
                    adapterInstantiationOrderTrackingList.clear();
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
            final Constructor[] constructors = getComponentImplementation().getDeclaredConstructors();
            for (int i = 0; i < constructors.length; i++) {
                if (!sortedMatchingConstructors.contains(constructors[i])) {
                    nonMatching.add(constructors[i]);
                } else {
                    // TODO - will it ever get here?, is the if() bogus?
                }
            }
            if (nonMatching.size() > 0) {
                throw new PicoInitializationException("The specified parameters do not match any of the following constructors: " + nonMatching.toString() + " for '" + getComponentImplementation() + "'");
            } else {
                throw new PicoInitializationException("There are no public constructors for '" + getComponentImplementation() + "'");
            }
        }
        return greediestConstructor;
    }

    private ComponentAdapter getGenericCollectionComponentAdapter(Class componentType) {
        if (getContainer().getComponentAdaptersOfType(componentType).size() == 0) {
            return null;
        } else {
            Object componentKey = new Object[]{this, componentType};
            return new GenericCollectionComponentAdapter(componentKey, null, componentType, Array.class);
        }
    }

//    private ComponentAdapter getGenericCollectionComponentAdapter(Class parameterType, Type genericType) {
//        ComponentAdapter result = null;
//
//        boolean isMap = Map.class.isAssignableFrom(parameterType);
//        boolean isCollection = Collection.class.isAssignableFrom(parameterType);
//        if((isMap || isCollection) && genericType instanceof ParameterizedType) {
//            ParameterizedType parameterizedType = (ParameterizedType) genericType;
//            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
//            Class keyType = null;
//            Class valueType = null;
//            if(isMap) {
//                keyType = (Class) actualTypeArguments[0];
//                valueType = (Class) actualTypeArguments[1];
//            } else {
//                valueType = (Class) actualTypeArguments[0];
//            }
//            Object componentKey = new Object[]{this, genericType};
//            result = new GenericCollectionComponentAdapter(componentKey, keyType, valueType, parameterType);
//        }
//        return result;
//    }

    protected Object instantiateComponent(List adapterInstantiationOrderTrackingList) throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        try {
            Constructor constructor = getGreediestSatisifableConstructor(adapterInstantiationOrderTrackingList);
            if (instantiating) {
                throw new CyclicDependencyException(constructor.getParameterTypes());
            }
            instantiating = true;
            Object[] parameters = getConstructorArguments(adapterInstantiationOrderTrackingList);

            return newInstance(constructor, parameters);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof RuntimeException) {
                throw (RuntimeException) e.getTargetException();
            } else if (e.getTargetException() instanceof Error) {
                throw (Error) e.getTargetException();
            }
            throw new PicoInvocationTargetInitializationException(e.getTargetException()); // <here>
        } catch (InstantiationException e) {
            // Handled by prior invocation of checkConcrete() is superclass. Caught and rethrown in line marked <here> above.
            throw new RuntimeException("Should never get here");
        } catch (IllegalAccessException e) {
            throw new PicoInitializationException(e);
        } finally {
            instantiating = false;
        }
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
        Constructor[] allConstructors = getComponentImplementation().getDeclaredConstructors();
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
                    return ((Constructor) arg1).getParameterTypes().length - ((Constructor) arg0).getParameterTypes().length;
                }
            });
        }
        return matchingConstructors;
    }
}
