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
 * A factory for DynamicMBeans, that creates {@link StandardMBean} instances from previously registered components. The
 * implementation offers special support for MBeans following the naming convention for their management interface using the
 * class name of the component with an appended <em>MBean</em>.
 * @author Michael Ward
 * @author J&ouml;rg Schaible
 * @version $Revision$
 */
public class StandardMBeanFactory implements DynamicMBeanFactory {

    /**
     * Create a StandardMBean for the component.
     * @param componentInstance {@inheritDoc}
     * @param management The management interface. If <code>null</code> the implementation will use the interface complying
     *                   with the naming convention for management interfaces.
     * @param mBeanInfo The {@link MBeanInfo} to use. If <code>null</code> the {@link StandardMBean} will use an automatically
     *                   generated one.
     * @return Returns a {@link StandardMBean}. If the <strong>mBeanInfo</strong> was not null, it is an instance of a
     *               {@link StandardNanoMBean}.
     * @see org.nanocontainer.jmx.DynamicMBeanFactory#create(java.lang.Object, java.lang.Class, javax.management.MBeanInfo)
     */
    public DynamicMBean create(final Object componentInstance, final Class management, final MBeanInfo mBeanInfo) {
        try {
            final Class managementInterface = getManagementInterface(componentInstance.getClass(), management, null);
            if (mBeanInfo == null) {
                return new StandardMBean(componentInstance, managementInterface);
            } else {
                return new StandardNanoMBean(componentInstance, managementInterface, mBeanInfo);
            }
        } catch (final ClassNotFoundException e) {
            throw new JMXRegistrationException("Cannot load management interface for StandardMBean", e);
        } catch (final NotCompliantMBeanException e) {
            throw new JMXRegistrationException("Cannot create StandardMBean", e);
        }
    }

    /**
     * Create a StandardMBean for the component. The implementation expects to find a management interface in the classpath
     * following the naming conventions.
     * @param componentInstance {@inheritDoc}
     * @param mBeanInfo The {@link MBeanInfo} to use. If <code>null</code> the {@link StandardMBean} will use an automatically
     *                   generated one.
     * @return Returns a {@link StandardMBean}. If the <strong>mBeanInfo</strong> was not null, it is an instance of a
     *               {@link StandardNanoMBean}.
     * @see org.nanocontainer.jmx.DynamicMBeanFactory#create(java.lang.Object, javax.management.MBeanInfo)
     */
    public DynamicMBean create(final Object componentInstance, final MBeanInfo mBeanInfo) {
        return create(componentInstance, null, mBeanInfo);
    }

    /**
     * Create a DynamicMBean for the component with a given description.
     * @param componentInstance {@inheritDoc}
     * @param management The management interface. If <code>null</code> the implementation will use the interface complying
     *                   with the naming convention for management interfaces.
     * @param description {@inheritDoc}
     * @return Returns a {@link StandardMBean}. If the description was not null, it is an instance of a
     *               {@link StandardNanoMBean}.
     * @see org.nanocontainer.jmx.DynamicMBeanFactory#create(java.lang.Object, java.lang.Class, java.lang.String)
     */
    public DynamicMBean create(final Object componentInstance, final Class management, final String description) {
        try {
            final Class managementInterface = getManagementInterface(componentInstance.getClass(), management, null);
            final DynamicMBean mBean = new StandardMBean(componentInstance, managementInterface);
            if (description != null && description.length() != 0) {
                final MBeanInfo mBeanInfo = mBean.getMBeanInfo();
                return create(componentInstance, management, new MBeanInfo(mBeanInfo.getClassName(), description, mBeanInfo
                        .getAttributes(), mBeanInfo.getConstructors(), mBeanInfo.getOperations(), mBeanInfo.getNotifications()));
            } else {
                return mBean;
            }
        } catch (final ClassNotFoundException e) {
            throw new JMXRegistrationException("Cannot load management interface for StandardMBean", e);
        } catch (final NotCompliantMBeanException e) {
            throw new JMXRegistrationException("Cannot create StandardMBean", e);
        }
    }

    private Class getManagementInterface(final Class type, final Class management, final MBeanInfo mBeanInfo)
            throws ClassNotFoundException {
        final Class managementInterface;
        if (management == null) {
            managementInterface = getDefaultManagementInterface(type, mBeanInfo);
        } else {
            managementInterface = management;
        }
        return managementInterface;
    }

    /**
     * Determin the management interface for the given type. The class name of the given type is used as class name of the mBean
     * unless the caller has provided a {@link MBeanInfo}, the class name of the MBean is retrieved a MBeanInfo that defines
     * this name. Following the naming conventions is the name of the management interface the same as the class name of the
     * MBean with an appended <em>MBean</em>. The {@link ClassLoader} of the type is used to load the interface type.
     * @param type The class of the MBean.
     * @param mBeanInfo The {@link MBeanInfo} for the MBean. May be <code>null</code>.
     * @return Returns the default management interface.
     * @throws ClassNotFoundException If the management interface cannot be found.
     * @since 1.0
     */
    public Class getDefaultManagementInterface(final Class type, final MBeanInfo mBeanInfo) throws ClassNotFoundException {
        return type.getClassLoader().loadClass((mBeanInfo == null ? type.getName() : mBeanInfo.getClassName()) + "MBean");
    }
}
