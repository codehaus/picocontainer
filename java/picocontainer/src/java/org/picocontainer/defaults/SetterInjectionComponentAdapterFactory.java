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


/**
 * A {@link ComponentAdapterFactory} for JavaBeans.
 * The factory creates {@link SetterInjectionComponentAdapter}.
 *
 * @author J&ouml;rg Schaible
 * @version $Revision$
 */
public class SetterInjectionComponentAdapterFactory extends DecoratingComponentAdapterFactory {
    /**
     * Constructs a SetterInjectionComponentAdapterFactory.
     *
     * @param delegate The delegated {@link ComponentAdapterFactory}
     */
    public SetterInjectionComponentAdapterFactory(ComponentAdapterFactory delegate) {
        super(delegate);
    }

    /**
     * @return Returns a {@link SetterInjectionComponentAdapter} as wrapper from the {@link ComponentAdapter} of the delegated factory.
     * @see org.picocontainer.defaults.DecoratingComponentAdapterFactory#createComponentAdapter(java.lang.Object, java.lang.Class, org.picocontainer.Parameter[])
     */
    public ComponentAdapter createComponentAdapter(Object componentKey,
                                                   Class componentImplementation,
                                                   Parameter[] parameters)
            throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        return new SetterInjectionComponentAdapter(super.createComponentAdapter(componentKey, componentImplementation, new Parameter[]{}));
    }
}
