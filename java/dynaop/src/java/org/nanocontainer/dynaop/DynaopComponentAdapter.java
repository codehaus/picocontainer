/*******************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved. *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD * style
 * license a copy of which has been included with this distribution in * the
 * license.html file. * * Idea by Rachel Davies, Original code by Aslak Hellesoy
 * and Paul Hammant *
 ******************************************************************************/
package org.nanocontainer.dynaop;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.DecoratingComponentAdapter;
import org.picocontainer.defaults.NotConcreteRegistrationException;

import dynaop.Aspects;
import dynaop.ProxyFactory;

/**
 * Applies Dynaop advice to component instances. Uses the
 * <code>dynaop.ProxyFactory.wrap</code> method to apply the advice; this
 * requires that the advised classes implement an interface.
 * 
 * @author Stephen Molitor
 */
public class DynaopComponentAdapter extends DecoratingComponentAdapter {

    private Aspects aspects;

    /**
     * Creates a new <code>DynaopComponentAdapter</code>.
     * 
     * @param aspects
     *            the collection of Dynaop aspects to apply to component
     *            instances.
     * @param delegate
     *            the <code>ComponenetAdapter</code> that will create the
     *            actual component instances, before wrapping with advise.
     */
    public DynaopComponentAdapter(Aspects aspects, ComponentAdapter delegate) {
        super(delegate);
        this.aspects = aspects;
    }

    /**
     * Retrieves the component instance and applies any Dynaop advice to the
     * component.
     * 
     * @return the (possibly advised) component instance.
     * @throws PicoInitializationException
     *             thrown when there is a problem initializing the container or
     *             some other part of the PicoContainer api, for example, when a
     *             cyclic dependency between components occurs.
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
    public Object getComponentInstance() throws PicoInitializationException,
                    PicoIntrospectionException,
                    AssignabilityRegistrationException,
                    NotConcreteRegistrationException {
        return ProxyFactory.getInstance(aspects).wrap(
                        super.getComponentInstance());
    }

}