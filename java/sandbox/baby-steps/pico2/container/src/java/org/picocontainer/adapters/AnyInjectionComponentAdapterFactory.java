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


    ConstructorInjectionComponentAdapterFactory cdiCaf = new ConstructorInjectionComponentAdapterFactory();
    private SetterInjectionComponentAdapterFactory sdiCaf = new SetterInjectionComponentAdapterFactory();

    public ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, ComponentCharacteristic registerationCharacteristic, Object componentKey, Class componentImplementation, Parameter... parameters) throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        if (ComponentCharacteristics.SDI.isSoCharacterized(registerationCharacteristic)) {
            return sdiCaf.createComponentAdapter(componentMonitor, lifecycleStrategy, registerationCharacteristic, componentKey, componentImplementation, parameters);
        }
        return cdiCaf.createComponentAdapter(componentMonitor, lifecycleStrategy, registerationCharacteristic, componentKey, componentImplementation, parameters);
    }

}
