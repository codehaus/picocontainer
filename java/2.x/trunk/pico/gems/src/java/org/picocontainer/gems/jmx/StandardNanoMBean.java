/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/

package org.picocontainer.gems.jmx;

import javax.management.MBeanInfo;
import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;


/**
 * StandardMBean with a provided MBeanInfo.
 * @author J&ouml;rg Schaible
 */
public final class StandardNanoMBean extends StandardMBean {
    private final MBeanInfo mBeanInfo;

    /**
     * Construct a StandardNanoMBean. The only difference to a {@link StandardMBean} of the JSR 3 is the user provided
     * {@link MBeanInfo}.
     * @param implementation
     * @param management
     * @param mBeanInfo
     * @throws NotCompliantMBeanException
     */
    public StandardNanoMBean(final Object implementation, final Class management, final MBeanInfo mBeanInfo)
            throws NotCompliantMBeanException {
        super(implementation, management);
        this.mBeanInfo = mBeanInfo;
    }

    /**
     * Return the provided {@link MBeanInfo}.
     * @see javax.management.StandardMBean#getMBeanInfo()
     */
    @Override
	public MBeanInfo getMBeanInfo() {
        return mBeanInfo;
    }
}