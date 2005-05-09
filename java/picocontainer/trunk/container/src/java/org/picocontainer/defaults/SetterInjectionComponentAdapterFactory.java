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
import org.picocontainer.PicoIntrospectionException;

import java.io.Serializable;


/**
 * A {@link ComponentAdapterFactory} for JavaBeans.
 * The factory creates {@link SetterInjectionComponentAdapter}.
 *
 * @author J&ouml;rg Schaible
 * @version $Revision$
 */
public class SetterInjectionComponentAdapterFactory implements ComponentAdapterFactory, Serializable {
    private final boolean allowNonPublicClasses;

    public SetterInjectionComponentAdapterFactory(boolean allowNonPublicClasses) {
        this.allowNonPublicClasses = allowNonPublicClasses;
    }

    public SetterInjectionComponentAdapterFactory() {
        this(false);
    }

    /**
     * Create a {@link SetterInjectionComponentAdapter}.
     *
     * @param componentKey            The component's key
     * @param componentImplementation The class of the bean.
     * @param parameters              Any parameters for the setters. If null the adapter solves the
     *                                dependencies for all setters internally. Otherwise the number parameters must match
     *                                the number of the setter.
     * @return Returns a new {@link SetterInjectionComponentAdapter}.
     * @throws PicoIntrospectionException if dependencies cannot be solved
     * @throws AssignabilityRegistrationException
     *                                    if  the <code>componentKey</code> is a type
     *                                    that does not match the implementation
     * @throws NotConcreteRegistrationException
     *                                    if the implementation is an interface or an
     *                                    abstract class.
     */
    public ComponentAdapter createComponentAdapter(Object componentKey, Class componentImplementation, Parameter[] parameters)
            throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        return new SetterInjectionComponentAdapter(componentKey, componentImplementation, parameters, allowNonPublicClasses);
    }
}
