/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.defaults;

import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoIntrospectionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

/**
 * This ComponentAdapter will instantiate a new object for each call to
 * {@link org.picocontainer.ComponentAdapter#getComponentInstance(PicoContainer)}.
 * That means that when used with a PicoContainer, getComponentInstance will 
 * return a new object each time.
 *
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @author J&ouml;rg Schaible
 * @version $Revision$
 * @since 1.0
 */
public abstract class InstantiatingComponentAdapter extends AbstractComponentAdapter {
    private transient ObjectReference verifyingGuard;
    /** The parameters to use for initialization. */ 
    protected Parameter[] parameters;
    /** Flag indicating instanciation of non-public classes. */ 
    protected final boolean allowNonPublicClasses;

    /**
     * Constructs a new ComponentAdapter for the given key and implementation. 
     * @param componentKey the search key for this implementation
     * @param componentImplementation the concrete implementation
     * @param parameters the parameters to use for the initialization
     * @param allowNonPublicClasses flag to allow instantiation of non-public classes.
     * @throws AssignabilityRegistrationException if the key is a type and the implementation cannot be assigned to.
     * @throws NotConcreteRegistrationException if the implementation is not a concrete class.
     */
    protected InstantiatingComponentAdapter(Object componentKey, Class componentImplementation, Parameter[] parameters, boolean allowNonPublicClasses) {
        super(componentKey, componentImplementation);
		checkConcrete();

        this.parameters = parameters;
        this.allowNonPublicClasses = allowNonPublicClasses;
    }

    private void checkConcrete() throws NotConcreteRegistrationException {
        // Assert that the component class is concrete.
        boolean isAbstract = (getComponentImplementation().getModifiers() & Modifier.ABSTRACT) == Modifier.ABSTRACT;
        if (getComponentImplementation().isInterface() || isAbstract) {
            throw new NotConcreteRegistrationException(getComponentImplementation());
        }
    }

    /**
     * Create default parameters for the given types.
     * 
     * @param parameters the parameter types
     * @return the array with the default parameters.
     */
    protected Parameter[] createDefaultParameters(Class[] parameters) {
        Parameter[] componentParameters = new Parameter[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            componentParameters[i] = new ComponentParameter();
        }
        return componentParameters;
    }

    /**
     * {@inheritDoc}
     */
    public void verify(final PicoContainer container) throws UnsatisfiableDependenciesException {
        final Constructor constructor = getGreediestSatisfiableConstructor(container);
        final Class[] parameterTypes = constructor.getParameterTypes();
        final Parameter[] currentParameters = parameters != null ? parameters : createDefaultParameters(parameterTypes);
        if (verifyingGuard == null) {
            verifyingGuard = new CyclicDependency.ThreadLocalGuard();
        }
        CyclicDependency.observe(verifyingGuard, getComponentImplementation(), new CyclicDependency() {
            public Object run() {
                for (int i = 0; i < currentParameters.length; i++) {
                    currentParameters[i].verify(container, InstantiatingComponentAdapter.this, parameterTypes[i]);
                }
                return null;
            }
        });
    }

    /**
     * Instantiate an object with given parameters and respect the accessible flag.
     * 
     * @param constructor the constructor to use
     * @param parameters the parameters for the constructor 
     * @return the new object.
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    protected Object newInstance(Constructor constructor, Object[] parameters) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        if (allowNonPublicClasses) {
            constructor.setAccessible(true);
        }
        return constructor.newInstance(parameters);
    }

    /**
     * Find and return the greediest satisfiable constructor.
     * 
     * @param container the PicoContainer to resolve dependencies.
     * @return the found constructor.
     * @throws PicoIntrospectionException
     * @throws UnsatisfiableDependenciesException
     * @throws AmbiguousComponentResolutionException
     * @throws AssignabilityRegistrationException
     * @throws NotConcreteRegistrationException
     */
    protected abstract Constructor getGreediestSatisfiableConstructor(PicoContainer container) throws PicoIntrospectionException, UnsatisfiableDependenciesException, AmbiguousComponentResolutionException, AssignabilityRegistrationException, NotConcreteRegistrationException;
}
