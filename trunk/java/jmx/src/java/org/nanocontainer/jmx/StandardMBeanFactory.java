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

import javax.management.DynamicMBean;
import javax.management.MBeanInfo;
import javax.management.StandardMBean;
import javax.management.NotCompliantMBeanException;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.text.MessageFormat;

/**
 * A factory for DynamicMBeans that can create at least {@link StandardMBean} instances.
 * @author Michael Ward
 * @author J&ouml,rg Schaible
 * @version $Revision$
 */
public class StandardMBeanFactory implements DynamicMBeanFactory {

	private static final String BUILD_STANDARDMBEAN_ERROR =
				"{0} must be an instance of {1}, or {2} should define the interface to use as a proxy.";

    /**
     * This method will throw a JMXRegistrationException, since the creation of a DynamicMBean with an explicit MBeanInfo is not
     * supported by this implementation.
     * @see org.nanocontainer.jmx.DynamicMBeanFactory#create(java.lang.Object, javax.management.MBeanInfo)
     */
    public DynamicMBean create(Object componentInstance, MBeanInfo mBeanInfo) {
        throw new JMXRegistrationException("A DynamicMBeanFactory instance supporting DynamicMBean creation with an MBeanInfo MUST be registered with the container");
    }

    /**
     * {@inheritDoc}
     * @see org.nanocontainer.jmx.DynamicMBeanFactory#create(java.lang.Object, java.lang.Class)
     */
    public DynamicMBean create(Object componentInstance, Class management) {
        try {
            return buildStandardMBean(componentInstance, management);
        } catch (final NotCompliantMBeanException e) {
            throw new JMXRegistrationException("Cannot create StandardMBean", e);
        }
    }

	/**
	 *
	 * @param implementation the instance of the Object being exposed for management.
	 * @param management the interface defining what to should be exposed.
	 */
	public static StandardMBean buildStandardMBean(final Object implementation, Class management) throws NotCompliantMBeanException {
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
	 * Allows an instance, or more specifically the instance of <code>implementation</code>, to be
	 * registered against a <code>management<code> interface that the <code>implementation</code>
	 * is not assignable from. In other words the implementation does NOT implement the management
	 * interface.
	 *
	 * For Example:
	 *
	 * public interface Foo {
	 *  	int size();
	 * }
	 *
	 * public class Bar {
	 *  	public int size() {
	 * 			return 1;
	 * 		}
	 * }
	 *
	 * Notice how Bar does not implement the interface Foo. But Bar does have an identical size() method.
	 * Allowing for the definition of Management interfaces which actually have no implementors.
	 * This will be useful when exposing an Object via JMX when the source code is not available or
	 * accesible (i.e. using third party jars or "closed" code).
	 */
	private static class StandardMBeanInvocationHandler implements InvocationHandler {
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
