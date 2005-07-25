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
import org.picocontainer.ComponentMonitorStrategy;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;

/**
 * @author Jon Tirs&eacute;n
 * @version $Revision$
 */
public class ConstructorInjectionComponentAdapterFactory extends MonitoringComponentAdapterFactory {
    private final boolean allowNonPublicClasses;
    private ComponentMonitorStrategy componentMonitorStrategy;

    public ConstructorInjectionComponentAdapterFactory(boolean allowNonPublicClasses, ComponentMonitorStrategy componentMonitorStrategy) {
        this.allowNonPublicClasses = allowNonPublicClasses;
        this.changeMonitorStrategy(componentMonitorStrategy);
    }

    public ConstructorInjectionComponentAdapterFactory(boolean allowNonPublicClasses) {
        this.allowNonPublicClasses = allowNonPublicClasses;
    }

    public ConstructorInjectionComponentAdapterFactory() {
        this(false);
    }

    public ComponentAdapter createComponentAdapter(Object componentKey,
                                                   Class componentImplementation,
                                                   Parameter[] parameters)
            throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        return new ConstructorInjectionComponentAdapter(componentKey, componentImplementation, parameters, allowNonPublicClasses, currentMonitorStrategy());
    }
}
