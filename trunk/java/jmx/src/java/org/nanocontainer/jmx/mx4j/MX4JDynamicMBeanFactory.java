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
 * This is the a factory for creating DynamicMBean instances. However it is tied specifically to MX4J. Those not interested in
 * being dependent on MX4J should implement another Factory and register it to the container. The single difference to the
 * StandardMBeanFactory is, that it does not need a special management interface for a component to expose.
 * @author Michael Ward
 * @version $Revision$
 */
public class MX4JDynamicMBeanFactory extends StandardMBeanFactory {

    /**
     * Create a MX4JDynamicMBean for the component using its instance and a {@link MBeanInfo}.
     * @see org.nanocontainer.jmx.DynamicMBeanFactory#create(java.lang.Object, javax.management.MBeanInfo)
     */
    public DynamicMBean create(final Object componentInstance, final MBeanInfo mBeanInfo) {
        return new MX4JDynamicMBean(componentInstance, mBeanInfo);
    }
}
