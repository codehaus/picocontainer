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

import org.picocontainer.defaults.InstanceComponentAdapter;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.NotConcreteRegistrationException;

import javax.management.StandardMBean;
import javax.management.NotCompliantMBeanException;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.text.MessageFormat;

/**
 * This component adapter is needed to ensure that a StandardMBean is registered with the MBeanServer
 *
 * @author Michael Ward
 * @version $Revision$
 */
public class StandardMBeanComponentAdapter extends InstanceComponentAdapter {
	public static final String BUILD_STANDARDMBEAN_ERROR = "{0} must be an instance of {1}, or {2} should define the interface to force.";

	private static StandardMBean buildStandardMBean(final Object implementation, Class management) throws NotCompliantMBeanException {
		if(management.isAssignableFrom(implementation.getClass())) {
			return new StandardMBean(implementation, management);
		}
		else if(management.isInterface()) {
			// wrap in a proxy to act as the interface passed in
			Class[] interfaces = new Class[] {management};

			Object proxy = Proxy.newProxyInstance(implementation.getClass().getClassLoader(),
					interfaces,
					new StandardMBeanInvocationHandler(implementation) );

			return new StandardMBean(proxy, management);
		}

		String error = MessageFormat
				.format(BUILD_STANDARDMBEAN_ERROR, new Object[] {implementation.getClass(), management, management});

		throw new NotCompliantMBeanException(error);
	}

	/**
	 *
	 * @param componentKey
	 * @param implementation the actual instance of the management interface
	 * @param management represents the interface to expose via JMX
	 */
	public StandardMBeanComponentAdapter(Object componentKey, Object implementation, Class management) throws AssignabilityRegistrationException, NotConcreteRegistrationException, NotCompliantMBeanException {
		super(componentKey, buildStandardMBean(implementation, management));
	}

	/**
	 * Registers the StandardMBean to the MBeanServer
	 * @return
	 */
	public Object getComponentInstance() {
		Object componentInstance = super.getComponentInstance();
		MBeanServerHelper.register(this, componentInstance);
		return componentInstance;
	}

	/**
	 * This allows the ability to force the implementation of an interface on a StandardMBean. Very powerful!
	 */
	static class StandardMBeanInvocationHandler implements InvocationHandler {
		private Object implementation;

		private StandardMBeanInvocationHandler(Object implementation) {
			this.implementation = implementation;
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return implementation
					.getClass()
					.getMethod(method.getName(), method.getParameterTypes())
					.invoke(implementation, args);
		}
	}
}
