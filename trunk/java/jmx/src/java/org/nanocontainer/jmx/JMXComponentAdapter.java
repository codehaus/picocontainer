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

import org.picocontainer.defaults.DecoratingComponentAdapter;
import org.picocontainer.defaults.NotConcreteRegistrationException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoInitializationException;

import javax.management.*;
import java.util.ArrayList;

/**
 * Allows for registering non-MBean instances to the MBeanServer by wrapping them in a PicoContainerMBean.
 * Usage requires that an associated MBeanInfo be registered with PicoContainer. The MBeanInfo registered MUSR
 *
 * @see PicoContainerMBean
 * @author Michael Ward
 * @version $Revision$
 */
public class JMXComponentAdapter extends DecoratingComponentAdapter {
	public JMXComponentAdapter(ComponentAdapter delegate) {
		super(delegate);
	}

	public Object getComponentInstance() throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
		Object componentInstance = super.getComponentInstance();
		Object mbean = new PicoContainerMBean(componentInstance, getMBeanInfo(componentInstance));

		// register with MBean Server
		MBeanServerHelper.register(this, mbean);
		return componentInstance;
	}

	protected MBeanInfo getMBeanInfo(Object componentInstance) {
		String mBeanInfoName = componentInstance.getClass().toString().concat("MBeanInfo");
		MBeanInfo mBeanInfo = (MBeanInfo)getContainer().getComponentInstance(mBeanInfoName);

		if(mBeanInfo == null) {
			try {
				// see if the MBeanInfo is registered to the Class (per Jörg Schaible suggestion)
				Class clazz = Class.forName(mBeanInfoName);
				mBeanInfo = (MBeanInfo)getContainer().getComponentInstance(clazz);
			} catch (ClassNotFoundException e) {
				throw new MBeanInfoMissingException(new ArrayList());
			}
		}

		return mBeanInfo;
	}
}
