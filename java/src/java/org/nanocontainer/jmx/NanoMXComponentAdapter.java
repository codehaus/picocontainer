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

import org.picocontainer.PicoInitializationException;
import org.picocontainer.extras.DecoratingComponentAdapter;
import org.picocontainer.internals.ComponentAdapter;
import org.picocontainer.internals.ComponentRegistry;

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

    public Object instantiateComponent(ComponentRegistry componentRegistry)
            throws PicoInitializationException {
        Object component = super.instantiateComponent(componentRegistry);

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
