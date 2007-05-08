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
import org.picocontainer.ParameterName;
import org.picocontainer.defaults.LifecycleStrategy;
import org.picocontainer.defaults.ThreadLocalCyclicDependencyGuard;
import org.picocontainer.defaults.DelegatingComponentMonitor;
import org.picocontainer.defaults.NotConcreteRegistrationException;
import org.picocontainer.defaults.ComponentParameter;
import org.picocontainer.defaults.UnsatisfiableDependenciesException;
import org.picocontainer.defaults.AmbiguousComponentResolutionException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.adapters.AbstractComponentAdapter;
import org.picocontainer.lifecycle.StartableLifecycleStrategy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import com.thoughtworks.paranamer.Paranamer;
import com.thoughtworks.paranamer.asm.AsmParanamer;

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
public abstract class InstantiatingComponentAdapter extends AbstractComponentAdapter
        implements LifecycleStrategy {
    /** The cycle guard for the verification. */ 
    protected transient Guard verifyingGuard;
    /** The parameters to use for initialization. */ 
    protected transient Parameter[] parameters;

    /** The cycle guard for the verification. */
    protected static abstract class Guard extends ThreadLocalCyclicDependencyGuard {
        protected PicoContainer guardedContainer;
        protected void setArguments(PicoContainer container) {
            this.guardedContainer = container;
        }
    }
    
    /** The strategy used to control the lifecycle */
    protected LifecycleStrategy lifecycleStrategy;
    
    /**
     * Constructs a new ComponentAdapter for the given key and implementation. 
     * @param componentKey the search key for this implementation
     * @param componentImplementation the concrete implementation
     * @param parameters the parameters to use for the initialization
     * @param monitor the addComponent monitor used by this ComponentAdapter
     * @param lifecycleStrategy the lifecycle strategy used by this ComponentAdapter
     * @throws org.picocontainer.defaults.AssignabilityRegistrationException if the key is a type and the implementation cannot be assigned to
     * @throws org.picocontainer.defaults.NotConcreteRegistrationException if the implementation is not a concrete class
     * @throws NullPointerException if one of the parameters is <code>null</code>
     */
    protected InstantiatingComponentAdapter(Object componentKey, Class componentImplementation, Parameter[] parameters,
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
     * @throws org.picocontainer.defaults.AssignabilityRegistrationException if the key is a type and the implementation cannot be assigned to
     * @throws org.picocontainer.defaults.NotConcreteRegistrationException if the implementation is not a concrete class
     * @throws NullPointerException if one of the parameters is <code>null</code>
     */
    protected InstantiatingComponentAdapter(Object componentKey, Class componentImplementation,
                                            Parameter[] parameters,
                                            ComponentMonitor monitor) {
        this(componentKey, componentImplementation, parameters, monitor, new StartableLifecycleStrategy(monitor));
    }

    /**
     * Constructs a new ComponentAdapter for the given key and implementation. 
     * @param componentKey the search key for this implementation
     * @param componentImplementation the concrete implementation
     * @param parameters the parameters to use for the initialization
     * @throws org.picocontainer.defaults.AssignabilityRegistrationException if the key is a type and the implementation cannot be assigned to.
     * @throws org.picocontainer.defaults.NotConcreteRegistrationException if the implementation is not a concrete class.
     * @throws NullPointerException if one of the parameters is <code>null</code>
     */
    protected InstantiatingComponentAdapter(Object componentKey, Class componentImplementation, Parameter[] parameters) {
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

    public void verify(final PicoContainer container) throws PicoIntrospectionException {
        if (verifyingGuard == null) {
            verifyingGuard = new Guard() {
                public Object run() {
                    final Constructor constructor = getGreediestSatisfiableConstructor(guardedContainer);
                    final Class[] parameterTypes = constructor.getParameterTypes();
                    final Parameter[] currentParameters = parameters != null ? parameters : createDefaultParameters(parameterTypes);
                    for (int i = 0; i < currentParameters.length; i++) {
                        final int i1 = i;
                        currentParameters[i].verify(container, InstantiatingComponentAdapter.this, parameterTypes[i], new ParameterName() {
                    public String getParameterName() {
                        Paranamer dpn = new AsmParanamer();
                        String[] names = dpn.lookupParameterNames(constructor);
                        if (names.length != 0) {
                            return names[i1];
                        }
                        return null;
                    }
                });
                    }
                    return null;
                }
            };
        }
        verifyingGuard.setArguments(container);
        verifyingGuard.observe(getComponentImplementation());
    }

    public void accept(PicoVisitor visitor) {
        super.accept(visitor);
        if (parameters != null) {
            for (int i = 0; i < parameters.length; i++) {
                parameters[i].accept(visitor);
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
     * @throws org.picocontainer.defaults.UnsatisfiableDependenciesException
     * @throws org.picocontainer.defaults.AmbiguousComponentResolutionException
     * @throws org.picocontainer.defaults.AssignabilityRegistrationException
     * @throws NotConcreteRegistrationException
     */
    protected abstract Constructor getGreediestSatisfiableConstructor(PicoContainer container) throws PicoIntrospectionException, UnsatisfiableDependenciesException, AmbiguousComponentResolutionException, AssignabilityRegistrationException, NotConcreteRegistrationException;
}
