/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.nanocontainer;

import org.picocontainer.lifecycle.LifecyclePicoAdapter;
import org.picocontainer.PicoContainer;
import org.apache.log4j.Category;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public class Log4JNanoContainerMonitor implements NanoContainerMonitor {

    private Category category = Category.getInstance("NanoContainerMonitor");

    public void componentsInstantiated(PicoContainer picoContainer) {
        category.info("Components Instantiated For Container " + picoContainer);
    }

    public void componentsLifecycleEvent(String eventName, LifecyclePicoAdapter lpa) {
        category.info("ComponentEvent '" + eventName + "'" + lpa);
    }
}
