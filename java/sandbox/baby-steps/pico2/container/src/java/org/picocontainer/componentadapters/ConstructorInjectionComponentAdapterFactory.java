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
    private final boolean allowNonPublicClasses;
    private LifecycleStrategy lifecycleStrategy;

    public ConstructorInjectionComponentAdapterFactory(boolean allowNonPublicClasses, 
                        ComponentMonitor monitor, LifecycleStrategy lifecycleStrategy) {
        this.allowNonPublicClasses = allowNonPublicClasses;
        this.changeMonitor(monitor);
        this.lifecycleStrategy = lifecycleStrategy;
    }

    public ConstructorInjectionComponentAdapterFactory(boolean allowNonPublicClasses, ComponentMonitor monitor) {
        this(allowNonPublicClasses, monitor, new StartableLifecycleStrategy(monitor));
    }

    public ConstructorInjectionComponentAdapterFactory(boolean allowNonPublicClasses, LifecycleStrategy lifecycleStrategy) {
        this(allowNonPublicClasses, new DelegatingComponentMonitor(), lifecycleStrategy);
    }

    public ConstructorInjectionComponentAdapterFactory(boolean allowNonPublicClasses) {
        this(allowNonPublicClasses, new DelegatingComponentMonitor());
    }

    public ConstructorInjectionComponentAdapterFactory() {
        this(false);
    }

    public ComponentAdapter createComponentAdapter(Object componentKey,
                                                   Class componentImplementation,
                                                   Parameter... parameters)
            throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        return new ConstructorInjectionComponentAdapter(componentKey, componentImplementation, parameters, 
                allowNonPublicClasses, currentMonitor(), lifecycleStrategy);
    }
}
