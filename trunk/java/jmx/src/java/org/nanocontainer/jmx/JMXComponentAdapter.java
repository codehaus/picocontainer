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
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoInitializationException;

import javax.management.MBeanInfo;
import java.text.MessageFormat;

/**
 * Allows for registering non-MBean instances to the MBeanServer by wrapping them in a PicoContainerMBean.
 * Usage requires that an associated MBeanInfo be registered with PicoContainer. The MBeanInfo registered MUSR
 *
 * @see PicoContainerMBean
 * @author Michael Ward
 * @version $Revision$
 */
public class JMXComponentAdapter extends DecoratingComponentAdapter {
	public static final String MBEAN_INFO_ERROR = "The Key \"{0}\" was not registered with the container (Key can either be a Class or String)";

	public JMXComponentAdapter(ComponentAdapter delegate) {
		super(delegate);
	}

	public Object getComponentInstance(PicoContainer pico) throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
		Object componentInstance = super.getComponentInstance(pico);
		Object mbean = new PicoContainerMBean(componentInstance, getMBeanInfo(pico, componentInstance));

		// register with MBean Server
		MBeanServerHelper.register(pico, this, mbean);
		return componentInstance;
	}

	protected MBeanInfo getMBeanInfo(PicoContainer pico, Object componentInstance) {
		String key = componentInstance.getClass().getName().concat("MBeanInfo");
		MBeanInfo mBeanInfo = (MBeanInfo)pico.getComponentInstance(key);

		if(mBeanInfo == null) {
			try {
				// see if the MBeanInfo is registered to the Class (per Jörg Schaible suggestion)
                Class clazz = Class.forName(key);
				mBeanInfo = (MBeanInfo)pico.getComponentInstance(clazz);
			} catch (ClassNotFoundException e) {
				throwMBeanInfoMissingException(key);
			}
		}

		if(mBeanInfo == null) { // An MBeanInfo MUST be registered
			throwMBeanInfoMissingException(key);
		}

		return mBeanInfo;
	}

	protected void throwMBeanInfoMissingException(String name) {
		String msg = MessageFormat.format(MBEAN_INFO_ERROR, new Object[] {name});
		throw new MBeanInfoMissingException(msg);
	}
}
