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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author Jon Tirs&eacute;n
 * @author Zohar Melamed
 * @version $Revision$
 */
public class TransientComponentAdapter extends AbstractComponentAdapter {

    private Parameter[] parameters;
    private List satisfiableConstructors;
    private Constructor greediestConstructor;
    private boolean instantiating;
    private boolean verifying;

    /**
     * Explicitly specifies parameters, if null uses default parameters.
     * 
     * @param componentKey            
     * @param componentImplementation 
     * @param parameters              
     */
    public TransientComponentAdapter(final Object componentKey,
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
    public TransientComponentAdapter(Object componentKey,
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
    private Constructor getConstructor(PicoContainer picoContainer) throws PicoIntrospectionException, NoSatisfiableConstructorsException, AmbiguousComponentResolutionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
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
        if (satisfiableConstructors == null) {
            satisfiableConstructors = new ArrayList();
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
                    satisfiableConstructors.add(constructor);
                }
            }
            if (satisfiableConstructors.isEmpty()) {
                throw new NoSatisfiableConstructorsException(getComponentImplementation(), failedDependencies);
            }
        }

        return satisfiableConstructors;
    }

    public Object getComponentInstance(MutablePicoContainer picoContainer)
            throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        ComponentAdapter[] adapterDependencies = findDependencies(picoContainer);
        return instantiateComponent(adapterDependencies, picoContainer);
    }

    private ComponentAdapter[] findDependencies(PicoContainer picoContainer) {
        final Class[] dependencyTypes = getDependencies(picoContainer);
        final ComponentAdapter[] adapterDependencies = new ComponentAdapter[dependencyTypes.length];

        final Parameter[] componentParameters = getParameters(picoContainer);

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
            adapterDependencies[i] = componentParameters[i].resolveAdapter(picoContainer);
        }
        return adapterDependencies;
    }

    public void verify(PicoContainer picoContainer) {
        ComponentAdapter[] adapterDependencies = findDependencies(picoContainer);
        verifyComponent(adapterDependencies, picoContainer);
    }

    private Object instantiateComponent(ComponentAdapter[] adapterDependencies, MutablePicoContainer picoContainer) throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        try {
            Constructor constructor = getConstructor(picoContainer);
            if (instantiating) {
                throw new CyclicDependencyException(constructor);
            }
            instantiating = true;
            Object[] parameters = new Object[adapterDependencies.length];
            for (int i = 0; i < adapterDependencies.length; i++) {
                ComponentAdapter adapterDependency = adapterDependencies[i];
                parameters[i] = adapterDependency.getComponentInstance(picoContainer);
            }

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

    private void verifyComponent(ComponentAdapter[] adapterDependencies, PicoContainer picoContainer) throws NoSatisfiableConstructorsException {
        try {
            Constructor constructor = getConstructor(picoContainer);
            if (verifying) {
                throw new CyclicDependencyException(constructor);
            }
            verifying = true;
            for (int i = 0; i < adapterDependencies.length; i++) {
                ComponentAdapter adapterDependency = adapterDependencies[i];
                adapterDependency.verify(picoContainer);
            }
        } catch (Exception e) {
            throw new PicoInvocationTargetInitializationException(e);
        } finally {
            verifying = false;
        }
    }

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

    private Parameter[] getParameters(PicoContainer componentRegistry) throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
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
