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

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This ComponentAdapter will instantiate a new object for each call to
 * {@link org.picocontainer.ComponentAdapter#getComponentInstance()}. That means that
 * when used with a PicoContainer, getComponentInstance will return a new
 * object each time.
 *
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @version $Revision$
 * @since 1.0
 */
public abstract class InstantiatingComponentAdapter extends AbstractComponentAdapter {
    private transient boolean verifying;
    /** The parameters to use for initialization. */ 
    protected Parameter[] parameters;
    private final boolean allowNonPublicClasses;

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
     * Create a new component instance. The method will retrieve and 
     * possibly instantiate any dependent component.
     * 
     * @return the new component instance.
     * @throws PicoInitializationException {@inheritDoc}
     * @throws PicoIntrospectionException {@inheritDoc}
     * @throws AssignabilityRegistrationException {@inheritDoc}
     * @throws NotConcreteRegistrationException {@inheritDoc}
     * 
     */
    public Object getComponentInstance()
            throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        List adapterInstantiationOrderTrackingList = new ArrayList();
        Object instance = instantiateComponent(adapterInstantiationOrderTrackingList);

        // Now, track the instantiation order
        for (Iterator it = adapterInstantiationOrderTrackingList.iterator(); it.hasNext();) {
            ComponentAdapter dependencyAdapter = (ComponentAdapter) it.next();
            getContainer().addOrderedComponentAdapter(dependencyAdapter);
        }
        return instance;
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

            // Oh No we don't. Its a long story, but that is an open door for hackers.

            //if (PicoContainer.class.isAssignableFrom(parameters[i])) {
            //    componentParameters[i] = new ConstantParameter(getContainer());
            //} else {
                componentParameters[i] = new ComponentParameter();
            //}
        }
        return componentParameters;
    }

    /**
     * {@inheritDoc}
     */
    public void verify() throws UnsatisfiableDependenciesException {
        try {
            List adapterDependencies = new ArrayList();
            getGreediestSatisfiableConstructor(adapterDependencies);
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

    private Class[] getDependencyTypes(List adapterDependencies) {
        Class[] result = new Class[adapterDependencies.size()];
        for (int i = 0; i < adapterDependencies.size(); i++) {
            ComponentAdapter adapterDependency = (ComponentAdapter) adapterDependencies.get(i);
            result[i] = adapterDependency.getComponentImplementation();
        }
        return result;
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
     * Instantiate the object.
     *
     * @param adapterInstantiationOrderTrackingList
     *         This list is filled with the dependent adapters of the instance.
     * @return the new instance.
     * @throws PicoInitializationException
     * @throws PicoIntrospectionException
     * @throws AssignabilityRegistrationException
     * @throws NotConcreteRegistrationException
     *
     */
    protected abstract Object instantiateComponent(List adapterInstantiationOrderTrackingList) throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException;

    /**
     * Find and return the greediest satisfiable constructor.
     * 
     * @param adapterInstantiationOrderTrackingList
     *         This list is filled with the dependent adapters of the instance.
     * @return the found constructor.
     * @throws PicoIntrospectionException
     * @throws UnsatisfiableDependenciesException
     * @throws AmbiguousComponentResolutionException
     * @throws AssignabilityRegistrationException
     * @throws NotConcreteRegistrationException
     */
    protected abstract Constructor getGreediestSatisfiableConstructor(List adapterInstantiationOrderTrackingList) throws PicoIntrospectionException, UnsatisfiableDependenciesException, AmbiguousComponentResolutionException, AssignabilityRegistrationException, NotConcreteRegistrationException;
}
