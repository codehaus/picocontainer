/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.componentadapters;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.ComponentCharacteristic;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.NotConcreteRegistrationException;

/**
 * @author Aslak Helles&oslash;y
 * @see org.picocontainer.gems.HotSwappingComponentAdapterFactory for a more feature-rich version of the class
 * @since 1.2, moved from package {@link org.picocontainer.alternatives}
 */
public class ImplementationHidingComponentAdapterFactory extends DecoratingComponentAdapterFactory {

    /**
     * For serialisation only. Do not use this constructor explicitly.
     */
    public ImplementationHidingComponentAdapterFactory() {
        this(null);
    }

    public ImplementationHidingComponentAdapterFactory(ComponentAdapterFactory delegate) {
        super(delegate);
    }

    public ComponentAdapter createComponentAdapter(ComponentCharacteristic registerationCharacteristic, Object componentKey, Class componentImplementation, Parameter... parameters) throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        ComponentAdapter componentAdapter = super.createComponentAdapter(registerationCharacteristic, componentKey, componentImplementation, parameters);
        return new ImplementationHidingComponentAdapter(componentAdapter);
    }
}
