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
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.ComponentCharacteristic;
import org.picocontainer.ComponentCharacteristics;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.adapters.CachingComponentAdapter;
import org.picocontainer.adapters.DecoratingComponentAdapterFactory;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.NotConcreteRegistrationException;
import org.picocontainer.defaults.LifecycleStrategy;

/**
 * @author Aslak Helles&oslash;y
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Revision$
 */
public class NonCachingComponentAdapterFactory extends DecoratingComponentAdapterFactory {

    public ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, ComponentCharacteristic registerationCharacteristic, Object componentKey, Class componentImplementation, Parameter... parameters)
            throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        if (ComponentCharacteristics.CACHE.isSoCharacterized(registerationCharacteristic)) {
            return new CachingComponentAdapter(super.createComponentAdapter(componentMonitor, lifecycleStrategy, registerationCharacteristic, componentKey, componentImplementation, parameters));
        }
        return super.createComponentAdapter(componentMonitor, lifecycleStrategy, registerationCharacteristic, componentKey, componentImplementation, parameters);

    }
}