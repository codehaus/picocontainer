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

import org.picocontainer.PicoRegistrationException;
import org.picocontainer.defaults.*;
import org.picocontainer.defaults.ComponentAdapterFactory;

import javax.management.*;
import java.io.Serializable;

/**
 * A simple OldPicoContainer which allow multiple implementations of the same interface
 * and any component registered with the internals is also registered into JMX
 * for viewing in a JMX management console.
 *
 * @author James Strachan
 * @author Mauro Talevi
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.2 $
 */
public class NanoMXContainer extends DefaultPicoContainer implements Serializable {

    private MBeanServer mbeanServer;

    public NanoMXContainer(MBeanServer mbeanServer, ComponentAdapterFactory componentAdapterFactory) {
        super(new NanoMXComponentAdapterFactory(mbeanServer, componentAdapterFactory));
        this.mbeanServer = mbeanServer;
    }

    public static class Default extends NanoMXContainer {
        public Default(MBeanServer mbeanServer) {
            super(mbeanServer, new DefaultComponentAdapterFactory());
        }
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
    public synchronized void registerComponentInstance(Object key, Object component)
            throws PicoRegistrationException {
        try {
            ObjectName name = asObjectName(key);
            super.registerComponentInstance(key, component);

            Object mbean = asMBean(component);
            mbeanServer.registerMBean(mbean, name);
        } catch (InstanceAlreadyExistsException e) {
            throw new NanoMXRegistrationException(e);
        } catch (MBeanRegistrationException e) {
            throw new NanoMXRegistrationException(e);
        } catch (NotCompliantMBeanException e) {
            throw new NanoMXRegistrationException(e);
        } catch (MalformedObjectNameException e) {
            throw new NanoMXRegistrationException(e);
        } catch (PicoRegistrationException e) {
            throw new NanoMXRegistrationException(e);
        }
    }

    /**
     * @param component
     * @return
     */
    protected static Object asMBean(Object component) {
        return new NanoMBean(component);
    }

    /**
     * Ensures that the given key is converted to a JMX ObjectName
     * @param key
     * @return an ObjectName based on the given key
     */
    protected static ObjectName asObjectName(Object key) throws MalformedObjectNameException {
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
            return new ObjectName(text);
        }
    }
}
