/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.nanocontainer;

import org.picocontainer.PicoContainer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public class CommonsLoggingNanoContainerMonitor implements NanoContainerMonitor {

    private Log log = LogFactory.getLog("NanoContainerMonitor");

    public void componentsInstantiated(PicoContainer picoContainer) {
        log.info("Components Instantiated For Container " + picoContainer);
    }

    public void componentsLifecycleEvent(String eventName, PicoContainer container) {
        log.info("ComponentEvent '" + eventName + "'" + container);
    }
}
