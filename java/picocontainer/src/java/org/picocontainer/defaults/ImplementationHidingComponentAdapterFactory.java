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
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class ImplementationHidingComponentAdapterFactory extends DecoratingComponentAdapterFactory {
    private final boolean strict;

    public ImplementationHidingComponentAdapterFactory() {
        this(new DefaultComponentAdapterFactory(), true);
    }

    public ImplementationHidingComponentAdapterFactory(ComponentAdapterFactory delegate) {
        this(delegate, true);
    }

    public ImplementationHidingComponentAdapterFactory(ComponentAdapterFactory delegate, boolean strict) {
        super(delegate);
        this.strict = strict;
    }

    public ComponentAdapter createComponentAdapter(Object componentKey,
                                                   Class componentImplementation,
                                                   Parameter[] parameters)
            throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        return new ImplementationHidingComponentAdapter(super.createComponentAdapter(componentKey, componentImplementation, parameters), strict);
    }
}
