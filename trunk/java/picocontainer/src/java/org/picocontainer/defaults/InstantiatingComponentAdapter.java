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
import org.picocontainer.PicoVerificationException;
import org.picocontainer.PicoVisitor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

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
    /** The cycle guard for the verification. */ 
    protected transient Guard verifyingGuard;
    /** The parameters to use for initialization. */ 
    protected Parameter[] parameters;
    /** Flag indicating instanciation of non-public classes. */ 
    protected boolean allowNonPublicClasses;
    
    protected static abstract class Guard extends ThreadLocalCyclicDependencyGuard {
        protected PicoContainer guardedContainer;
        protected void setArguments(PicoContainer container) {
            this.guardedContainer = container;
        }
    }

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
    public void verify(final PicoContainer container) throws PicoVerificationException {
        try {
            if (verifyingGuard == null) {
                verifyingGuard = new Guard() {
                    public Object run() {
                        final Constructor constructor = getGreediestSatisfiableConstructor(guardedContainer);
                        final Class[] parameterTypes = constructor.getParameterTypes();
                        final Parameter[] currentParameters = parameters != null ? parameters : createDefaultParameters(parameterTypes);
                        for (int i = 0; i < currentParameters.length; i++) {
                            currentParameters[i].verify(container, InstantiatingComponentAdapter.this, parameterTypes[i]);
                        }
                        return null;
                    }
                };
            }
            verifyingGuard.setArguments(container);
            verifyingGuard.observe(getComponentImplementation());
        } catch (PicoVerificationException ex) {
            throw ex;
        } catch (Exception ex) {
            final List list = new LinkedList();
            list.add(ex);
            throw new PicoVerificationException(list);
        }
    }

    /**
     * {@inheritDoc}
     * @see org.picocontainer.ComponentAdapter#accept(org.picocontainer.PicoVisitor)
     */
    public void accept(PicoVisitor visitor) {
        if (visitor.isReverseTraversal()) {
            acceptParameters(visitor);
        }
        super.accept(visitor);
        if (!visitor.isReverseTraversal()) {
            acceptParameters(visitor);
        }
    }
    
    private void acceptParameters(PicoVisitor visitor) {
        if (parameters != null) {
            if (!visitor.isReverseTraversal()) {
                for (int i = 0; i < parameters.length; i++) {
                    parameters[i].accept(visitor);
                }
            } else {
                for (int i = parameters.length; i-- > 0; ) {
                    parameters[i].accept(visitor);
                }
            }
        }
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
