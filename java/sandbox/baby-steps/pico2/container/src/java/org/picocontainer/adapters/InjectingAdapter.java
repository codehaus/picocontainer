/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.adapters;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoVisitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.defaults.ThreadLocalCyclicDependencyGuard;
import org.picocontainer.monitors.DelegatingComponentMonitor;
import org.picocontainer.defaults.NotConcreteRegistrationException;
import org.picocontainer.parameters.ComponentParameter;
import org.picocontainer.defaults.UnsatisfiableDependenciesException;
import org.picocontainer.lifecycle.StartableLifecycleStrategy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

/**
 * This ComponentAdapter will instantiate a new object for each call to
 * {@link org.picocontainer.ComponentAdapter#getComponentInstance(PicoContainer)}.
 * That means that when used with a PicoContainer, getComponent will
 * return a new object each time.
 *
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @author J&ouml;rg Schaible
 * @author Mauro Talevi
 * @version $Revision$
 * @since 1.0
 */
public abstract class InjectingAdapter extends AbstractComponentAdapter
        implements LifecycleStrategy {
    /** The cycle guard for the verification. */ 
    protected transient ThreadLocalCyclicDependencyGuard verifyingGuard;
    /** The parameters to use for initialization. */ 
    protected transient Parameter[] parameters;
 
    /** The strategy used to control the lifecycle */
    protected LifecycleStrategy lifecycleStrategy;
    
    /**
     * Constructs a new ComponentAdapter for the given key and implementation. 
     * @param componentKey the search key for this implementation
     * @param componentImplementation the concrete implementation
     * @param parameters the parameters to use for the initialization
     * @param monitor the addComponent monitor used by this ComponentAdapter
     * @param lifecycleStrategy the lifecycle strategy used by this ComponentAdapter
     * @throws AssignabilityRegistrationException if the key is a type and the implementation cannot be assigned to
     * @throws org.picocontainer.defaults.NotConcreteRegistrationException if the implementation is not a concrete class
     * @throws NullPointerException if one of the parameters is <code>null</code>
     */
    protected InjectingAdapter(Object componentKey, Class componentImplementation, Parameter[] parameters,
                                            ComponentMonitor monitor, LifecycleStrategy lifecycleStrategy) {
        super(componentKey, componentImplementation, monitor);
        checkConcrete();
        if (parameters != null) {
            for (int i = 0; i < parameters.length; i++) {
                if(parameters[i] == null) {
                    throw new NullPointerException("Parameter " + i + " is null");
                }
            }
        }
        this.parameters = parameters;
        this.lifecycleStrategy = lifecycleStrategy;
    }

    /**
     * Constructs a new ComponentAdapter for the given key and implementation. 
     * @param componentKey the search key for this implementation
     * @param componentImplementation the concrete implementation
     * @param parameters the parameters to use for the initialization
     * @param monitor the addComponent monitor used by this ComponentAdapter
     * @throws AssignabilityRegistrationException if the key is a type and the implementation cannot be assigned to
     * @throws org.picocontainer.defaults.NotConcreteRegistrationException if the implementation is not a concrete class
     * @throws NullPointerException if one of the parameters is <code>null</code>
     */
    protected InjectingAdapter(Object componentKey, Class componentImplementation,
                                            Parameter[] parameters,
                                            ComponentMonitor monitor) {
        this(componentKey, componentImplementation, parameters, monitor, new StartableLifecycleStrategy(monitor));
    }

    /**
     * Constructs a new ComponentAdapter for the given key and implementation. 
     * @param componentKey the search key for this implementation
     * @param componentImplementation the concrete implementation
     * @param parameters the parameters to use for the initialization
     * @throws AssignabilityRegistrationException if the key is a type and the implementation cannot be assigned to.
     * @throws org.picocontainer.defaults.NotConcreteRegistrationException if the implementation is not a concrete class.
     * @throws NullPointerException if one of the parameters is <code>null</code>
     */
    protected InjectingAdapter(Object componentKey, Class componentImplementation, Parameter[] parameters) {
        this(componentKey, componentImplementation, parameters, new DelegatingComponentMonitor());
    }
    
    private void checkConcrete() throws NotConcreteRegistrationException {
        // Assert that the addComponent class is concrete.
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
            componentParameters[i] = ComponentParameter.DEFAULT;
        }
        return componentParameters;
    }

    public abstract void verify(PicoContainer container) throws PicoIntrospectionException;

    public void accept(PicoVisitor visitor) {
        super.accept(visitor);
        if (parameters != null) {
            for (Parameter parameter : parameters) {
                parameter.accept(visitor);
            }
        }
    }
  
    public void start(Object component) {
        lifecycleStrategy.start(component);
    }

    public void stop(Object component) {
        lifecycleStrategy.stop(component);
    }

    public void dispose(Object component) {
        lifecycleStrategy.dispose(component);
    }

    public boolean hasLifecycle(Class type) {
        return lifecycleStrategy.hasLifecycle(type);
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
        return constructor.newInstance(parameters);
    }

    /**
     * Find and return the greediest satisfiable constructor.
     * 
     * @param container the PicoContainer to resolve dependencies.
     * @return the found constructor.
     * @throws PicoIntrospectionException
     * @throws UnsatisfiableDependenciesException
     * @throws NotConcreteRegistrationException
     */
    protected abstract Constructor getGreediestSatisfiableConstructor(PicoContainer container) throws PicoIntrospectionException, NotConcreteRegistrationException;

}
