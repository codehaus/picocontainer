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
import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;


/**
 * A factory for DynamicMBeans that can create at least {@link StandardMBean}instances.
 * @author Michael Ward
 * @author J&ouml,rg Schaible
 * @version $Revision$
 */
public class StandardMBeanFactory implements DynamicMBeanFactory {

    /**
     * This method will throw a JMXRegistrationException, since the creation of a DynamicMBean with an explicit MBeanInfo is not
     * supported by this implementation.
     * @see org.nanocontainer.jmx.DynamicMBeanFactory#create(java.lang.Object, javax.management.MBeanInfo)
     */
    public DynamicMBean create(Object componentInstance, MBeanInfo mBeanInfo) {
        throw new JMXRegistrationException(
                "A DynamicMBeanFactory instance supporting DynamicMBean creation with an MBeanInfo MUST be registered with the container");
    }

    /**
     * {@inheritDoc}
     * @see org.nanocontainer.jmx.DynamicMBeanFactory#create(java.lang.Object, java.lang.Class)
     */
    public DynamicMBean create(Object componentInstance, Class management) {
        try {
            return new StandardMBean(componentInstance, management);
        } catch (final NotCompliantMBeanException e) {
            throw new JMXRegistrationException("Cannot create StandardMBean", e);
        }
    }
}
