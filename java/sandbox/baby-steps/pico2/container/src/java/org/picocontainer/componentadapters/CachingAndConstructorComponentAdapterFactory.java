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
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.NotConcreteRegistrationException;
import org.picocontainer.componentadapters.ConstructorInjectionComponentAdapter;
import org.picocontainer.defaults.ComponentMonitorStrategy;
import org.picocontainer.lifecycle.StartableLifecycleStrategy;
import org.picocontainer.monitors.DefaultComponentMonitor;

/**
 * Creates instances of {@link ConstructorInjectionComponentAdapter} decorated by
 * {@link CachingComponentAdapter}.
 *
 * @author Jon Tirs&eacute;n
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class CachingAndConstructorComponentAdapterFactory extends MonitoringComponentAdapterFactory {

    private final LifecycleStrategy lifecycleStrategy;

    public CachingAndConstructorComponentAdapterFactory(ComponentMonitor monitor) {
        super(monitor);
        this.lifecycleStrategy = new StartableLifecycleStrategy(monitor);
    }

    public CachingAndConstructorComponentAdapterFactory(ComponentMonitor monitor, LifecycleStrategy lifecycleStrategy) {
        super(monitor);
        this.lifecycleStrategy = lifecycleStrategy;
    }

    public CachingAndConstructorComponentAdapterFactory() {
        this.lifecycleStrategy = new StartableLifecycleStrategy(new DefaultComponentMonitor());
    }

    public ComponentAdapter createComponentAdapter(Object componentKey, Class componentImplementation, Parameter... parameters) throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        return new CachingComponentAdapter(new ConstructorInjectionComponentAdapter(componentKey, componentImplementation, parameters, false, currentMonitor(), lifecycleStrategy));
    }

    public void changeMonitor(ComponentMonitor monitor) {
        super.changeMonitor(monitor);
        if (lifecycleStrategy instanceof ComponentMonitorStrategy) {
            ((ComponentMonitorStrategy) lifecycleStrategy).changeMonitor(monitor);
        }
    }

}
