/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by James Strachan                                           *
 *****************************************************************************/
package org.nanocontainer.jmx;

import mx4j.AbstractDynamicMBean;

import javax.management.MBeanInfo;

/**
 * @author James Strachan
 * @author Michael Ward
 * @version $Revision$
 */
public class NanoMBean extends AbstractDynamicMBean {

    public NanoMBean(Object componentInstance, MBeanInfo mBeanInfo) {
        setResource(componentInstance);
		setMBeanInfo(mBeanInfo);
    }

    /* Method of the second group that is overridden */
    protected String getMBeanDescription() {
        return "NanoMBean: " + getResource().toString();
    }
}
