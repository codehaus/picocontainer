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
 * A factory for DynamicMBeans, that creates {@link StandardMBean}instances from previously registered components.
 * @author Michael Ward
 * @author J&ouml;rg Schaible
 * @version $Revision$
 */
public class StandardMBeanFactory implements DynamicMBeanFactory {

    /**
     * Create a StandardNanoMBean for the component.
     * @see org.nanocontainer.jmx.DynamicMBeanFactory#create(java.lang.Object, java.lang.Class, javax.management.MBeanInfo)
     */
    public DynamicMBean create(final Object componentInstance, final Class management, final MBeanInfo mBeanInfo) {
        try {
            return new StandardNanoMBean(componentInstance, management, mBeanInfo);
        } catch (final NotCompliantMBeanException e) {
            throw new JMXRegistrationException("Cannot create StandardMBean", e);
        }
    }

    /**
     * Create a StandardNanoMBean for the component. The implementation expects to find the corresponding management interface
     * in the classpath.
     * @see org.nanocontainer.jmx.DynamicMBeanFactory#create(java.lang.Object, javax.management.MBeanInfo)
     */
    public DynamicMBean create(final Object componentInstance, final MBeanInfo mBeanInfo) {
        try {
            return create(componentInstance, componentInstance.getClass().getClassLoader().loadClass(
                    mBeanInfo.getClassName() + "MBean"), mBeanInfo);
        } catch (final ClassNotFoundException e) {
            throw new JMXRegistrationException("Cannot load management interface for StandardMBean", e);
        }
    }

    /**
     * Create a DynamicMBean for the component. If the description is null a StandradMBean is created else a StandardNanoMBean.
     * @see org.nanocontainer.jmx.DynamicMBeanFactory#create(java.lang.Object, java.lang.Class, java.lang.String)
     */
    public DynamicMBean create(final Object componentInstance, final Class management, final String description) {
        try {
            final DynamicMBean mBean = new StandardMBean(componentInstance, management);
            if (description != null && description.length() != 0) {
                final MBeanInfo mBeanInfo = mBean.getMBeanInfo();
                return create(componentInstance, management, new MBeanInfo(mBeanInfo.getClassName(), description, mBeanInfo
                        .getAttributes(), mBeanInfo.getConstructors(), mBeanInfo.getOperations(), mBeanInfo.getNotifications()));
            } else {
                return mBean;
            }
        } catch (final NotCompliantMBeanException e) {
            throw new JMXRegistrationException("Cannot create StandardMBean", e);
        }
    }
}
