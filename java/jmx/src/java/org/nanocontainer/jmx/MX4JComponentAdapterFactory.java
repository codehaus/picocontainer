/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.jmx;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.NotConcreteRegistrationException;
import org.picocontainer.defaults.DecoratingComponentAdapterFactory;

import javax.management.MBeanServer;
import java.io.Serializable;

public class MX4JComponentAdapterFactory extends DecoratingComponentAdapterFactory implements Serializable {
    private MBeanServer mbeanServer;

    public MX4JComponentAdapterFactory(MBeanServer mbeanServer, ComponentAdapterFactory delegate) {
        super(delegate);
        this.mbeanServer = mbeanServer;
    }

    public ComponentAdapter createComponentAdapter(Object componentKey, Class componentImplementation, Parameter[] parameters) throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        return new MX4JComponentAdapter(mbeanServer, super.createComponentAdapter(componentKey, componentImplementation, parameters));
    }
}
