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
import org.picocontainer.lifecycle.LifecyclePicoAdapter;

// TODO can we deprecate this in favour of some sort of InvocationInterceptor
// that sits closer to the MulticastInvoker? See longer comment inside the Main class.
// AH

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public interface NanoContainerMonitor {
    void componentsLifecycleEvent(String eventName, LifecyclePicoAdapter lpa);

    void componentsInstantiated(PicoContainer picoContainer);
}
