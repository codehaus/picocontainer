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

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.ComponentCharacteristic;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.adapters.MonitoringComponentAdapterFactory;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.NotConcreteRegistrationException;
import org.picocontainer.defaults.ComponentMonitorStrategy;
import org.picocontainer.defaults.LifecycleStrategy;

public class DecoratingComponentAdapterFactory extends MonitoringComponentAdapterFactory {
    private ComponentAdapterFactory delegate;

    public DecoratingComponentAdapterFactory(ComponentAdapterFactory delegate) {
        this.delegate = delegate;
    }

    public ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, ComponentCharacteristic registerationCharacteristic, Object componentKey,
                                                   Class componentImplementation,
                                                   Parameter... parameters) throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        return delegate.createComponentAdapter(componentMonitor, lifecycleStrategy, registerationCharacteristic, componentKey, componentImplementation, parameters);
    }

}
