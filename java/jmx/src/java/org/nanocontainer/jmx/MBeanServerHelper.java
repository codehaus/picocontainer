/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Michael Ward                                    		 *
 *****************************************************************************/

package org.nanocontainer.jmx;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;

import javax.management.*;

/**
 * Simply a helper to register MBean's to the MBeanServer
 *
 * @author Michael Ward
 * @author James Strachan
 * @author Mauro Talevi
 * @author Jeppe Cramon
 * @version $Revision$
 */
public class MBeanServerHelper {
	protected MBeanServer mBeanServer;

	public MBeanServerHelper(MBeanServer mBeanServer) {
		this.mBeanServer = mBeanServer;
	}

	public static void register(PicoContainer pico, ComponentAdapter adapter, Object mbean) throws JMXRegistrationException {
		try {
			ObjectName objectName = asObjectName(adapter.getComponentKey());
			MBeanServer mBeanServer = getMBeanServer(pico);

			if(mBeanServer == null) {
             	throw new JMXRegistrationException("An MBeanServer instance MUST be registered with the container");
			}
			else if(!mBeanServer.isRegistered(objectName)) { // only register once!
				mBeanServer.registerMBean(mbean, objectName);
			}
		} catch (JMException e) {
			throw new JMXRegistrationException(e);
		}
	}

	protected static MBeanServer getMBeanServer(PicoContainer pico) {
		return (MBeanServer)pico.getComponentInstance(MBeanServer.class);
	}

	/**
     * Ensures that the given componentKey is converted to a JMX ObjectName
     * @param componentKey
     * @return an ObjectName based on the given componentKey
     */
    protected static ObjectName asObjectName(Object componentKey) throws MalformedObjectNameException {
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
