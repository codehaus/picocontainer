/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.adapters;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.ComponentCharacteristic;
import org.picocontainer.ComponentCharacteristics;
import org.picocontainer.adapters.MonitoringComponentAdapterFactory;
import org.picocontainer.adapters.SetterInjectionComponentAdapterFactory;
import org.picocontainer.defaults.LifecycleStrategy;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.NotConcreteRegistrationException;
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
public class AnyInjectionComponentAdapterFactory extends MonitoringComponentAdapterFactory {

    private final LifecycleStrategy lifecycleStrategy;

    ConstructorInjectionComponentAdapterFactory cdiCaf;
    private SetterInjectionComponentAdapterFactory sdiCaf;


    public AnyInjectionComponentAdapterFactory(ComponentMonitor monitor) {
        super(monitor);
        this.lifecycleStrategy = new StartableLifecycleStrategy(monitor);
        cafs(monitor, lifecycleStrategy);
    }

    private void cafs(ComponentMonitor monitor, LifecycleStrategy lifecycleStrategy) {
        cdiCaf = new ConstructorInjectionComponentAdapterFactory(monitor, lifecycleStrategy);
        sdiCaf = new SetterInjectionComponentAdapterFactory(lifecycleStrategy);
        sdiCaf.changeMonitor(monitor);
    }

    public AnyInjectionComponentAdapterFactory(ComponentMonitor monitor, LifecycleStrategy lifecycleStrategy) {
        super(monitor);
        this.lifecycleStrategy = lifecycleStrategy;
        cafs(monitor, lifecycleStrategy);
    }

    public AnyInjectionComponentAdapterFactory() {
        this.lifecycleStrategy = new StartableLifecycleStrategy(new DefaultComponentMonitor());
        cafs(currentMonitor(), lifecycleStrategy);
    }

    public ComponentAdapter createComponentAdapter(ComponentCharacteristic registerationCharacteristic, Object componentKey, Class componentImplementation, Parameter... parameters) throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        if (ComponentCharacteristics.SDI.isSoCharacterized(registerationCharacteristic)) {
            return sdiCaf.createComponentAdapter(registerationCharacteristic, componentKey, componentImplementation, parameters);
        }
        return cdiCaf.createComponentAdapter(registerationCharacteristic, componentKey, componentImplementation, parameters);
    }

    public void changeMonitor(ComponentMonitor monitor) {
        super.changeMonitor(monitor);
        if (lifecycleStrategy instanceof ComponentMonitorStrategy) {
            ((ComponentMonitorStrategy) lifecycleStrategy).changeMonitor(monitor);
        }
        cdiCaf.changeMonitor(monitor);
        sdiCaf.changeMonitor(monitor);
    }

}
