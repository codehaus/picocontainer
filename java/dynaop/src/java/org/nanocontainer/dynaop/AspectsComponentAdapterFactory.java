/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/
package org.nanocontainer.dynaop;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.DecoratingComponentAdapterFactory;
import org.picocontainer.defaults.NotConcreteRegistrationException;

import dynaop.Aspects;

/**
 * Creates <code>AspectsComponentAdapter</code> instances.
 * 
 * @author Stephen Molitor
 */
public class AspectsComponentAdapterFactory extends
                DecoratingComponentAdapterFactory {

    private Aspects aspects;

    /**
     * Creates a new <code>AspectsComponentAdapterFactory</code>.
     * 
     * @param delegate
     *            the factory to delegate to.
     * @param aspects
     *            the collection of Dynaop advice that will be applied to
     *            component instances.
     */
    public AspectsComponentAdapterFactory(ComponentAdapterFactory delegate,
                    Aspects aspects) {
        super(delegate);
        this.aspects = aspects;
    }

    /**
     * Creates a new <code>AspectsComponentAdapterFactory</code> using the
     * default <code>dynaop.Aspects</code> instance. Configures the default
     * BeanShell script on the classpath at "/dynaop.bsh".
     * 
     * @param delegate
     *            the factory to delegate to.
     */
    public AspectsComponentAdapterFactory(ComponentAdapterFactory delegate) {
        this(delegate, Aspects.getInstance());
    }

    /**
     * Creates the component adapter.
     * 
     * @param componentKey
     *            the component key.
     * @param componentImplementation
     *            the component implementation class.
     * @param parameters
     *            the component parameters.
     * @return the component adapter instance.
     * @throws PicoIntrospectionException
     *             thrown if the component has dependencies which could not be
     *             resolved, or instantiation of the component lead to an
     *             ambiguous situation within the container.
     * @throws AssignabilityRegistrationException
     *             thrown during component registration if the component's key
     *             is a type and the implementation is not assignable to.
     * @throws NotConcreteRegistrationException
     *             thrown if the component implementation is not concrete and
     *             can not be instantiated.
     */
    public ComponentAdapter createComponentAdapter(Object componentKey,
                    Class componentImplementation, Parameter[] parameters)
                    throws PicoIntrospectionException,
                    AssignabilityRegistrationException,
                    NotConcreteRegistrationException {
        return new AspectsComponentAdapter(aspects, super
                        .createComponentAdapter(componentKey,
                                        componentImplementation, parameters));
    }

}