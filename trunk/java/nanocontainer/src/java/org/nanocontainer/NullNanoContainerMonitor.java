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

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public class NullNanoContainerMonitor implements NanoContainerMonitor {

    public void componentsInstantiated(PicoContainer picoContainer) {
    }

    public void componentsLifecycleEvent(String eventName, PicoContainer container) {
    }
}
