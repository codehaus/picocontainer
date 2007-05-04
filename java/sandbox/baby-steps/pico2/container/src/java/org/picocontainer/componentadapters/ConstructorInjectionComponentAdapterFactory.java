/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.componentadapters;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.ComponentCharacteristic;
import org.picocontainer.componentadapters.MonitoringComponentAdapterFactory;
import org.picocontainer.defaults.LifecycleStrategy;
import org.picocontainer.defaults.DelegatingComponentMonitor;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.NotConcreteRegistrationException;
import org.picocontainer.lifecycle.StartableLifecycleStrategy;

/**
 * @author Jon Tirs&eacute;n
 * @version $Revision$
 */
public class ConstructorInjectionComponentAdapterFactory extends MonitoringComponentAdapterFactory {
    private LifecycleStrategy lifecycleStrategy;

    public ConstructorInjectionComponentAdapterFactory(
            ComponentMonitor monitor, LifecycleStrategy lifecycleStrategy) {
        this.changeMonitor(monitor);
        this.lifecycleStrategy = lifecycleStrategy;
    }

    public ConstructorInjectionComponentAdapterFactory(ComponentMonitor monitor) {
        this(monitor, new StartableLifecycleStrategy(monitor));
    }

    public ConstructorInjectionComponentAdapterFactory(LifecycleStrategy lifecycleStrategy) {
        this(new DelegatingComponentMonitor(), lifecycleStrategy);
    }

    public ConstructorInjectionComponentAdapterFactory() {
        this(new DelegatingComponentMonitor());
    }

    public ComponentAdapter createComponentAdapter(ComponentCharacteristic registerationCharacteristic, Object componentKey,
                                                   Class componentImplementation,
                                                   Parameter... parameters)
            throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        return new ConstructorInjectionComponentAdapter(componentKey, componentImplementation, parameters, 
                    currentMonitor(), lifecycleStrategy);
    }
}
