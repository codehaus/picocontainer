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

/**
 * This factory is responsible for creating instances of DynamicMBean without being dependent on one particular
 * implementation or external dependency.
 *
 * @author Michael Ward
 * @author J&ouml,rg Schaible
 * @version $Revision$
 */
public interface DynamicMBeanFactory {

	/**
     * Create a DynamicMBean from instance and the provided {@link MBeanInfo}.
     * @param componentInstance the instance of the Object being exposed for management.
	 * @param mBeanInfo the explicitly provided management information
     * @return the {@link DynamicMBean}.
	 */
	DynamicMBean create(Object componentInstance, MBeanInfo mBeanInfo);

    /**
     * Create a {@link javax.management.StandardMBean} from the management interface and an instance.
     * @param componentInstance the instance of the Object being exposed for management.
     * @param management the interface defining what to should be exposed.
     * @return the {@link DynamicMBean}.
     */
    DynamicMBean create(Object componentInstance, Class management);
}
