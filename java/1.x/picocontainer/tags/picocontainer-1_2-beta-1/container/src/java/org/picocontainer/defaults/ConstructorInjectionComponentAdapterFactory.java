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

import java.io.Serializable;

/**
 * @author Jon Tirs&eacute;n
 * @version $Revision$
 */
public class ConstructorInjectionComponentAdapterFactory implements ComponentAdapterFactory, Serializable {
    private final boolean allowNonPublicClasses;
    private ComponentMonitor componentMonitor;

    public ConstructorInjectionComponentAdapterFactory(boolean allowNonPublicClasses, ComponentMonitor componentMonitor) {
        this.allowNonPublicClasses = allowNonPublicClasses;
        this.componentMonitor = componentMonitor;
    }

    public ConstructorInjectionComponentAdapterFactory(boolean allowNonPublicClasses) {
        this.allowNonPublicClasses = allowNonPublicClasses;
        this.componentMonitor = NullComponentMonitor.getInstance();
    }

    public ConstructorInjectionComponentAdapterFactory() {
        this(false);
    }

    public ComponentAdapter createComponentAdapter(Object componentKey,
                                                   Class componentImplementation,
                                                   Parameter[] parameters)
            throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        return new ConstructorInjectionComponentAdapter(componentKey, componentImplementation, parameters, allowNonPublicClasses, componentMonitor);
    }
}
