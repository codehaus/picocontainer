package org.nanocontainer;

import org.picocontainer.lifecycle.LifecyclePicoAdapter;
import org.picocontainer.PicoContainer;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public class NullNanoContainerMonitor implements NanoContainerMonitor {

    public void componentsInstantiated(PicoContainer picoContainer) {
    }

    public void componentsLifecycleEvent(String eventName, LifecyclePicoAdapter lpa) {
    }
}
