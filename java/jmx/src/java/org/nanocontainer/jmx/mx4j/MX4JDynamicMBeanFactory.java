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

import org.nanocontainer.jmx.DynamicMBeanFactory;

import javax.management.DynamicMBean;
import javax.management.MBeanInfo;

/**
 * @author Michael Ward
 * @version $Revision$
 */
public class MX4JDynamicMBeanFactory implements DynamicMBeanFactory {

	public DynamicMBean create(Object componentInstance, MBeanInfo mBeanInfo) {
		return new MX4JDynamicMBean(componentInstance, mBeanInfo);
	}
}
