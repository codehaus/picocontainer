/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by James Strachan and Mauro Talevi                          *
 *****************************************************************************/
package org.nanocontainer.jmx;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.picocontainer.ComponentFactory;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.ComponentSpecification;

/**
 * @author James Strachan
 * @author Mauro Talevi
 * @version $Revision: 1.1 $
 */
public class NanoMXComponentFactory implements ComponentFactory {

    private MBeanServer mbeanServer;
    private ComponentFactory delegate;

    /**
     * @param mbeanServer
     */
    public NanoMXComponentFactory(MBeanServer mbeanServer, ComponentFactory delegate) {
        this.mbeanServer = mbeanServer;
        this.delegate = delegate;
    }

    public Object createComponent(ComponentSpecification specification, Object[] args) throws PicoInitializationException, PicoIntrospectionException {
        Object component = delegate.createComponent(specification, args);

        try {
            ObjectName name = NanoMXContainer.asObjectName(specification.getComponentKey());
            Object mbean = NanoMXContainer.asMBean(component);
            mbeanServer.registerMBean(mbean, name);
        }
        catch (Exception e) {
            throw new NanoMXInitializationException(e);
        }
        return component;

    }

    public Class[] getDependencies(Class args) throws PicoIntrospectionException {
        return delegate.getDependencies(args);
    }

}
