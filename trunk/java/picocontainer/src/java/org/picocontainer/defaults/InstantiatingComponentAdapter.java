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
import org.picocontainer.PicoContainer;
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
 */
public abstract class InstantiatingComponentAdapter extends AbstractComponentAdapter {
    private transient boolean verifying;
    protected Parameter[] parameters;
    private final boolean allowNonPublicClasses;

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

    private Class[] getDependencyTypes(List adapterDependencies) {
        Class[] result = new Class[adapterDependencies.size()];
        for (int i = 0; i < adapterDependencies.size(); i++) {
            ComponentAdapter adapterDependency = (ComponentAdapter) adapterDependencies.get(i);
            result[i] = adapterDependency.getComponentImplementation();
        }
        return result;
    }

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
     * @return Returns the new instance.
     * @throws PicoInitializationException
     * @throws PicoIntrospectionException
     * @throws AssignabilityRegistrationException
     *
     * @throws NotConcreteRegistrationException
     *
     */
    protected abstract Object instantiateComponent(List adapterInstantiationOrderTrackingList) throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException;

    protected abstract Constructor getGreediestSatisifableConstructor(List adapterInstantiationOrderTrackingList) throws PicoIntrospectionException, UnsatisfiableDependenciesException, AmbiguousComponentResolutionException, AssignabilityRegistrationException, NotConcreteRegistrationException;
}
