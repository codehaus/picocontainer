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
import org.picocontainer.defaults.DecoratingComponentAdapter;
import org.picocontainer.defaults.NotConcreteRegistrationException;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

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
            Object mbean = new PicoContainerMBean(componentInstance);
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

    /**
     * Ensures that the given componentKey is converted to a JMX ObjectName
     * @param componentKey
     * @return an ObjectName based on the given componentKey
     */
    private static ObjectName asObjectName(Object componentKey) throws MalformedObjectNameException {
        if (componentKey == null) {
            throw new NullPointerException("componentKey cannot be null");
        }
        if (componentKey instanceof ObjectName) {
            return (ObjectName) componentKey;
        }
        if (componentKey instanceof Class) {
            Class clazz = (Class) componentKey;
            return new ObjectName("picomx:type=" + clazz.getName());
        } else {
            String text = componentKey.toString();
            // Fix, so it works under WebSphere ver. 5
            if (text.indexOf(':') == -1) {
                text = "picomx:type=" + text;
            }
            return new ObjectName(text);
        }
    }
}
