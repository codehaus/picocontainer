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

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.NotConcreteRegistrationException;
import org.picocontainer.defaults.DecoratingComponentAdapter;
import org.picocontainer.defaults.DecoratingComponentAdapter;

import javax.management.*;

/**
 * @author James Strachan
 * @author Mauro Talevi
 * @author Jeppe Cramon
 * @version $Revision$
 */
public class MX4JComponentAdapter extends DecoratingComponentAdapter {

    private final MBeanServer mbeanServer;

    /**
     * @param mbeanServer
     */
    public MX4JComponentAdapter(MBeanServer mbeanServer, ComponentAdapter delegate) {
        super(delegate);
        this.mbeanServer = mbeanServer;
    }

    public Object getComponentInstance() throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        Object componentInstance = super.getComponentInstance();

        ObjectName name = null;
        try {
            name = asObjectName(getComponentKey());
            Object mbean = asMBean(componentInstance);
            mbeanServer.registerMBean(mbean, name);
        } catch (MalformedObjectNameException e) {
            throw new MX4JInitializationException("Failed to register MBean '" + name + "' for component '" + getComponentKey() + "', due to " + e.getMessage(), e);
        } catch (MBeanRegistrationException e) {
            throw new MX4JInitializationException("Failed to register MBean '" + name + "' for component '" + getComponentKey() + "', due to " + e.getMessage(), e);
        } catch (NotCompliantMBeanException e) {
            throw new MX4JInitializationException("Failed to register MBean '" + name + "' for component '" + getComponentKey() + "', due to " + e.getMessage(), e);
        } catch (InstanceAlreadyExistsException e) {
            throw new MX4JInitializationException("Failed to register MBean '" + name + "' for component '" + getComponentKey() + "', due to " + e.getMessage(), e);
        }
        return componentInstance;
    }

    public static Object asMBean(Object component) {
        return new PicoContainerMBean(component);
    }

    /**
     * Ensures that the given key is converted to a JMX ObjectName
     * @param key
     * @return an ObjectName based on the given key
     */
    public static ObjectName asObjectName(Object key) throws MalformedObjectNameException {
        if (key == null) {
            throw new NullPointerException("key cannot be null");
        }
        if (key instanceof ObjectName) {
            return (ObjectName) key;
        }
        if (key instanceof Class) {
            Class clazz = (Class) key;
            return new ObjectName("nanomx:type=" + clazz.getName());
        } else {
            String text = key.toString();
            // Fix, so it works under WebSphere ver. 5
            if (text.indexOf(':') == -1) {
                text = "nanomx:type=" + text;
            }
            return new ObjectName(text);
        }
    }
}
