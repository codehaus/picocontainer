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

import org.picocontainer.extras.DecoratingComponentAdapter;
import org.picocontainer.defaults.ComponentAdapter;
import org.picocontainer.defaults.AbstractPicoContainer;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.NotConcreteRegistrationException;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;

import javax.management.*;

/**
 * @author James Strachan
 * @author Mauro Talevi
 * @version $Revision$
 */
public class NanoMXComponentAdapter extends DecoratingComponentAdapter {

    private MBeanServer mbeanServer;

    /**
     * @param mbeanServer
     */
    public NanoMXComponentAdapter(MBeanServer mbeanServer, ComponentAdapter delegate) {
        super(delegate);
        this.mbeanServer = mbeanServer;
    }

    public Object getComponentInstance(AbstractPicoContainer componentRegistry) throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        Object component = super.getComponentInstance(componentRegistry);

        try {
            ObjectName name = NanoMXContainer.asObjectName(getComponentKey());
            Object mbean = NanoMXContainer.asMBean(component);
            mbeanServer.registerMBean(mbean, name);
        } catch (MalformedObjectNameException e) {
            throw new NanoMXInitializationException(e);
        } catch (MBeanRegistrationException e) {
            throw new NanoMXInitializationException(e);
        } catch (NotCompliantMBeanException e) {
            throw new NanoMXInitializationException(e);
        } catch (InstanceAlreadyExistsException e) {
            throw new NanoMXInitializationException(e);
        }
        return component;

    }
}
