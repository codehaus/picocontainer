/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Michael Ward                                    		 *
 *****************************************************************************/

package org.nanocontainer.jmx.mx4j;

import javax.management.DynamicMBean;
import javax.management.MBeanInfo;

import org.nanocontainer.jmx.StandardMBeanFactory;

/**
 * This is the default factory for creating DynamicMBean instances however it is tied specifically to MX4J. Those
 * not interested in being dependent on MX4J should implement another Factory and register it to the container.
 *
 * @author Michael Ward
 * @version $Revision$
 */
public class MX4JDynamicMBeanFactory extends StandardMBeanFactory {

	public DynamicMBean create(Object componentInstance, MBeanInfo mBeanInfo) {
		return new MX4JDynamicMBean(componentInstance, mBeanInfo);
	}
}
