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

/**
 * @author James Strachan
 * @version $Revision: 1.1 $
 */
public class NanoMBean extends AbstractDynamicMBean {

    public NanoMBean(Object component) {
        setResource(component);
    }
    /* Method of the second group that is overridden */
    protected String getMBeanDescription() {
        return "A simple DynamicMBean";
    }
}
