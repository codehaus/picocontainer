/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by James Strachan and Mauro Talevi                          *
 *****************************************************************************/

package org.picoextras.jmx;

import org.picocontainer.PicoRegistrationException;
import org.picocontainer.defaults.*;
import org.picocontainer.defaults.ComponentAdapterFactory;

import javax.management.*;
import java.io.Serializable;

/**
 * A simple PicoContainer which allow multiple implementations of the same interface
 * and any component registered with the internals is also registered into JMX
 * for viewing in a JMX management console.
 *
 * @author James Strachan
 * @author Mauro Talevi
 * @author Aslak Helles&oslash;y
 * @author Jeppe Cramon
 * @version $Revision$
 */
public class NanoMXContainer extends DefaultPicoContainer implements Serializable {

    private MBeanServer mbeanServer;

    public NanoMXContainer(MBeanServer mbeanServer, ComponentAdapterFactory componentAdapterFactory) {
        super(new NanoMXComponentAdapterFactory(mbeanServer, componentAdapterFactory));
        this.mbeanServer = mbeanServer;
    }

    public NanoMXContainer(MBeanServer mbeanServer) {
        this(mbeanServer, new DefaultComponentAdapterFactory());
    }

    public NanoMXContainer() {
        this(MBeanServerFactory.newMBeanServer());
        MBeanServerFactory.createMBeanServer();
    }

    public MBeanServer getMBeanServer() {
        return mbeanServer;
    }

    /**
     * Registers a new constructed component with the given key
     *
     * @param key is the key (or name) used to lookup the component in the future
     * @param component is the component to be registered
     * @throws PicoRegistrationException
     */
    public synchronized Object registerComponentInstance(Object key, Object component)
            throws PicoRegistrationException {
		ObjectName name = null;             	
        try {
            name = NanoMXComponentAdapter.asObjectName(key);
            Object result = super.registerComponentInstance(key, component);

            Object mbean = NanoMXComponentAdapter.asMBean(component);
            mbeanServer.registerMBean(mbean, name);

            return result;
        } catch (InstanceAlreadyExistsException e) {
            throw new NanoMXRegistrationException("Failed to register MBean '" + name + "' for component '" + key + "', due to " + e.getMessage(), e);
        } catch (MBeanRegistrationException e) {
			throw new NanoMXRegistrationException("Failed to register MBean '" + name + "' for component '" + key + "', due to " + e.getMessage(), e);
        } catch (NotCompliantMBeanException e) {
			throw new NanoMXRegistrationException("Failed to register MBean '" + name + "' for component '" + key + "', due to " + e.getMessage(), e);
        } catch (MalformedObjectNameException e) {
			throw new NanoMXRegistrationException("Failed to register MBean '" + name + "' for component '" + key + "', due to " + e.getMessage(), e);
        } catch (PicoRegistrationException e) {
			throw new NanoMXRegistrationException("Failed to register MBean '" + name + "' for component '" + key + "', due to " + e.getMessage(), e);
        }
    }

}
